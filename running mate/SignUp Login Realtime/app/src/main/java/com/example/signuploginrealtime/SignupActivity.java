package com.example.signuploginrealtime;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    static String LOG_TAG = "SignUpDebug";
    FirebaseAuth mAuth;

    HelpClass mHelpClass;
    EditText signupName, signupUsername, signupEmail, signupPassword;
    TextView loginRedirectText;

    View loadingView;
    ViewGroup rootView;
    Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        mHelpClass = HelpClass.getInstance();
        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);

        rootView = findViewById(android.R.id.content);
        loadingView = LayoutInflater.from(this).inflate(R.layout.layout_loading, rootView, false);
        rootView.addView(loadingView);

        signupButton.setOnClickListener(this::doSignUp);

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    gotoUserMain();
                }
                rootView.removeView(loadingView);
            });
        } else {
            rootView.removeView(loadingView);
        }
    }

    private void gotoUserMain() {
        Intent intent = new Intent(SignupActivity.this, UserMainActivity.class);
        startActivity(intent);
    }

    private void doSignUp(View view) {
        String name = signupName.getText().toString();
        String email = signupEmail.getText().toString();
        String username = signupUsername.getText().toString();
        String password = signupPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Context context = getApplicationContext();
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG, "成功註冊");
                    FirebaseUser user = mAuth.getCurrentUser(); // 取得已註冊成功的使用者

                    if (user != null) {
                        Log.d(LOG_TAG, user.getUid());
                        String userId = user.getUid();
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        UserProfile userProfile = new UserProfile(name, email, username, password);
                        mHelpClass.setProfile(userProfile);
                        Log.d(LOG_TAG, "setProfile");
                        db.child("users").child(userId).setValue(userProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(LOG_TAG, "onSuccess");
                                Toast.makeText(context, "註冊成功", Toast.LENGTH_SHORT).show();
                                gotoUserMain();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(LOG_TAG, "onFailure:" + e.toString());
                                Toast.makeText(context, "新增使用者資料失敗", Toast.LENGTH_SHORT).show();
                                // TODO 刪掉使用者註冊帳號
                            }
                        });
                    }
                } else {
                    Toast.makeText(context, "註冊失敗", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(LOG_TAG, e.toString());
                Context context = getApplicationContext();
                FirebaseAuthException authException = (FirebaseAuthException)e;
                String errorCode = authException.getErrorCode();
                Log.d(LOG_TAG, errorCode);
                if (errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                    Toast.makeText(context, "這個email已註冊過", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}