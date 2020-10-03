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
import android.widget.Toast;

import com.enigmaticdevs.dorkdiary.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mingle.widget.LoadingView;
import com.mingle.widget.ShapeLoadingView;

public class SignUp extends AppCompatActivity {
    TextInputLayout email_layout,password_layout,password_again_layout;
    TextInputEditText email,password,password_again;
    FirebaseAuth firebaseAuth;
    ConstraintLayout shapeLoadingView;
    int e,p,pa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.signup_email);
        email_layout = findViewById(R.id.signup_email_layout);
        password = findViewById(R.id.signup_password);
        password_layout = findViewById(R.id.signup_password_layout);
        password_again = findViewById(R.id.signup_password_again);
        password_again_layout = findViewById(R.id.signup_password_again_layout);
        shapeLoadingView = findViewById(R.id.signup_loading);
        shapeLoadingView.setVisibility(View.GONE);
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
email_layout.setError(null);
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
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password_again.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
             password_again_layout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void NewAcc(View view) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        shapeLoadingView.setVisibility(View.VISIBLE);
        if (!isEmailValid(email.getText().toString())){
            email_layout.setError("Invalid Email");
        }
        else
            e=1;
        if(password.getText().toString().isEmpty()){
            password_layout.setError("Enter Password");

        }
        else
            p=1;
        if(password_again.getText().toString().isEmpty()){
            password_again_layout.setError("Enter same password again");
        }
        else
            pa=1;
        if(e==1 && p==1 && pa==1){
            if (password.getText().toString().equals(password_again.getText().toString())) {
                if (password.getText().toString().length() >= 6) {
                    hideKeyboard(view);
                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        shapeLoadingView.setVisibility(View.INVISIBLE);
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        user.sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("Email Sent", "Email sent.");
                                                        }
                                                        else
                                                            Toast.makeText(SignUp.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(SignUp.this, "Verify Your Email To Login", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignUp.this, LoginActivity.class);
                                        finish();
                                        startActivity(intent);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                         shapeLoadingView.setVisibility(View.INVISIBLE);
                                        Toast.makeText(SignUp.this, task.getException().getLocalizedMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    // ...
                                }
                            });
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    shapeLoadingView.setVisibility(View.INVISIBLE);
                    password_layout.setError("password too short");
                    password_again_layout.setError("password too short");
                }
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                shapeLoadingView.setVisibility(View.INVISIBLE);
                password_layout.setError("passwords doesnt match");
                password_again_layout.setError("passwords doesnt match");
            }
        }
        else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            shapeLoadingView.setVisibility(View.INVISIBLE);

        }


    }
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void policy(View view) {
        Intent intent = new Intent(this,PrivacyPolicy.class);
        startActivity(intent);
    }
}