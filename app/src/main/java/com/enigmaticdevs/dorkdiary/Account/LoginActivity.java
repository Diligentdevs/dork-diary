package com.enigmaticdevs.dorkdiary.Account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextClock;
import android.widget.Toast;

import com.enigmaticdevs.dorkdiary.MainActivity;
import com.enigmaticdevs.dorkdiary.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    TextInputLayout email_layout,password_layout;
    TextInputEditText email,password;
    ConstraintLayout loading_view;
    int e,v,p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        email_layout = findViewById(R.id.email_layout);
        password_layout = findViewById(R.id.password_layout);
        loading_view = findViewById(R.id.login_loading);
        loading_view.setVisibility(View.GONE);
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            email_layout.setError(null);
            e=0;
            v=0;
            p=0;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            password_layout.setError(null);
                e=0;
                v=0;
                p=0;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void signUp(View view) {
        Intent intent = new Intent(this,SignUp.class);
        startActivity(intent);
    }

    public void login(View view) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loading_view.setVisibility(View.VISIBLE);
        if(email.getText().toString().isEmpty())
            email_layout.setError("Enter an email");
        else
            e=1;
        if (password.getText().toString().isEmpty())
            password_layout.setError("Enter Valid password");
        else
            p=1;
        if(isEmailValid(email.getText().toString()))
            v=1;
        else
           email_layout.setError("Invalid Email");
        if(e==1 && p==1 && v==1) {
            hideKeyboard(view);
            firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                loading_view.setVisibility(View.INVISIBLE);
                                // Sign in success, update UI with the signed-in user's information
                                if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finishAffinity();
                                } else
                                    Toast.makeText(LoginActivity.this, "Verify Email First", Toast.LENGTH_SHORT).show();

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                loading_view.setVisibility(View.INVISIBLE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }

                            // ...
                        }
                    });
        }
        else{
            loading_view.setVisibility(View.INVISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void forgot(View view) {
        Intent intent = new Intent(this,ForgotPassword.class);
        startActivity(intent);
    }
}