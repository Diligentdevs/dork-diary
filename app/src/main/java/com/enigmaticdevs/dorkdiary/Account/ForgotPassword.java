package com.enigmaticdevs.dorkdiary.Account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.enigmaticdevs.dorkdiary.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    TextView forgot_email;
    TextInputLayout forgot_email_layout;
    String mEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        forgot_email = findViewById(R.id.forgot_email);
        forgot_email_layout = findViewById(R.id.forgot_email_layout);
        forgot_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                forgot_email_layout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void sendlink(View view) {
        hideKeyboard(view);
        if(forgot_email.getText().toString().isEmpty()){
            forgot_email_layout.setError("Enter an email");
            return;
        }
        else
            mEmail = forgot_email.getText().toString();
        if (!isEmailValid(mEmail)){
            forgot_email_layout.setError("Invalid Email");
            return;
        }
        FirebaseAuth.getInstance().sendPasswordResetEmail(mEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPassword.this, "Email Sent", Toast.LENGTH_SHORT).show();
                            resetsuccessful();
                        }
                        else
                            Toast.makeText(ForgotPassword.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public void resetsuccessful(){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}