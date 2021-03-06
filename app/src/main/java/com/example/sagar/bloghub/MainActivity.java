package com.example.sagar.bloghub;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    FirebaseAuth mAuth;
    FloatingActionButton addPostButton;
    FirebaseFirestore firebaseFirestore;
    String currentUserId;
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment;
    Fragment currentFragment;
    NotificationFragment notificationFragment;
    AccountFragment accountFragment;
    FragmentTransaction fragmentTransaction;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth= FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();
        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Blog Hub");

        if(mAuth.getCurrentUser()!= null) {
            addPostButton = findViewById(R.id.addPostButton);
            bottomNavigationView = findViewById(R.id.mainBottomNav);
            homeFragment = new HomeFragment();
            notificationFragment = new NotificationFragment();
            accountFragment = new AccountFragment();

            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.main_container,homeFragment);
            fragmentTransaction.add(R.id.main_container,notificationFragment);
            fragmentTransaction.add(R.id.main_container,accountFragment);
            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(accountFragment);
            fragmentTransaction.hide(notificationFragment);
            fragmentTransaction.commit();
            replaceFragment(homeFragment);

            addPostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(MainActivity.this, NewPostActivity.class);
                    startActivity(intent);
                }
            });

            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {

                        case R.id.bottom_home:
                            replaceFragment(homeFragment);
                            return true;

                        case R.id.bottom_notification:
                            replaceFragment(notificationFragment);
                            return true;

                        case R.id.bottom_account:
                            replaceFragment(accountFragment);
                            return true;

                        default:
                            return false;
                    }

                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser== null){
           sendToLogin();
        }else{
            currentUserId= mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if(!task.getResult().exists()){
                            Intent intent = new Intent(MainActivity.this, SetupActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    }else {
                        String error = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            CheckFirstTime firstTime = new CheckFirstTime();

            if(firstTime.isFirstTime(MainActivity.this)){
                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                startActivity(intent);
                finish();
            }

        }

    }
    void replaceFragment(Fragment fragment){
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(currentFragment!=null)
            fragmentTransaction.hide(currentFragment);
        fragmentTransaction.show(fragment);
        currentFragment = fragment;
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
            switch (item.getItemId()) {

                case R.id.action_search_btn:
                     onSearchRequested();
                     Log.d("searchPresssed",onSearchRequested()+"");
                     return true;

                case R.id.action_logout_btn:
                    logOut();
                    return true;

                case R.id.action_settings_btn:

                    //Intent settingsIntent = new Intent(MainActivity.this, SetupActivity.class);
                    //startActivity(settingsIntent);

                    return true;


                default:
                    return false;


            }


    }

    private void logOut() {
        mAuth.signOut();
        sendToLogin();
    }

    public void sendToLogin(){
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
