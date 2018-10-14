package com.example.sagar.bloghub;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class SearchableActivity extends ListActivity {

    FirebaseFirestore firebaseFirestore;
    ArrayList<User> userList;

    SearchUserAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SEARCH", "HERE");
        handleIntent(getIntent());
    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        // call the appropriate detail activity
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doSearch(query);
        }
    }

    private void doSearch(String queryStr) {

        firebaseFirestore= FirebaseFirestore.getInstance();
        userList= new ArrayList<>();
        adapter= new SearchUserAdapter(getBaseContext(),userList);
        setListAdapter(adapter);

        firebaseFirestore.collection("Users")
                .whereEqualTo("name", queryStr)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
                                Toast.makeText(getBaseContext(),"No result found",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            for (DocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                user.setId(document.getId());
                                userList.add(user);
                                adapter.notifyDataSetChanged();
                                Log.d(TAG, user.getId() + " => " + user.getName()+"/"+user.getImage());
                                Log.d(TAG, document.getId() + " => " + document.getData()+"/"+user.getName());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}