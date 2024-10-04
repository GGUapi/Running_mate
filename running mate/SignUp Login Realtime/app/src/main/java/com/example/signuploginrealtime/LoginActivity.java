package com.example.signuploginrealtime;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    static String LOG_TAG = "LoginDebug";

    FirebaseAuth mAuth;
    EditText loginEmail, loginPassword;
    Button loginButton;
    TextView signupRedirectText;

    View loadingView;
    ViewGroup rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        mAuth = FirebaseAuth.getInstance();

        rootView = findViewById(android.R.id.content);
        loadingView = LayoutInflater.from(this).inflate(R.layout.layout_loading, rootView, false);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                if (!validateEmail() | !validatePassword()) {
                    Toast.makeText(context, "email/password 不能填空", Toast.LENGTH_SHORT).show();
                } else {
                    login();
                }
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

    }

    public Boolean validateEmail() {
        String val = loginEmail.getText().toString();
        if (val.isEmpty()) {
            loginEmail.setError("Email cannot be empty");
            return false;
        } else {
            loginEmail.setError(null);
            return true;
        }
    }

    public Boolean validatePassword(){
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            loginPassword.setError("Password cannot be empty");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    private  void login() {
        rootView.addView(loadingView);
        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Context context = getApplicationContext();
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this, UserMainActivity.class);
                        startActivity(intent);
                    } else {
                        Exception e = task.getException();
                        Log.d(LOG_TAG, "signInWithEmail:failure"+e.getMessage());
                        Toast.makeText(context, "登入失敗:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    rootView.removeView(loadingView);
                }
            });
    }

}