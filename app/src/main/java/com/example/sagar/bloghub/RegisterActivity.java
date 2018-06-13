package com.example.sagar.bloghub;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    EditText emailEditText,passEditText,confirmPassEditText;
    Button register,login;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        progressBar= findViewById(R.id.registrationProgressBar);
        emailEditText= findViewById(R.id.reg_email);
        passEditText= findViewById(R.id.reg_password);
        confirmPassEditText= findViewById(R.id.reg_ConfirmPassword);
        register= findViewById(R.id.registerButton);
        login= findViewById(R.id.reg_loginButton);
        mAuth= FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email= emailEditText.getText().toString();
                String pass= passEditText.getText().toString();
                String confPass= confirmPassEditText.getText().toString();

                if(!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(pass)&&!TextUtils.isEmpty(confPass)){
                    if(pass.equals(confPass)){
                        progressBar.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Intent mainIntent = new Intent(RegisterActivity.this,SetupActivity.class);
                                    startActivity(mainIntent);
                                    finish();
                                }
                                else{
                                    String error= task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                    else{
                        Toast.makeText(RegisterActivity.this,"Passwords dont match",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser!= null){
            sendToMain();
        }
    }
    private void sendToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }


}
