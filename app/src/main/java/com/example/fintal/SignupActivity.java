package com.example.fintal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fintal.Models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {
    private final String TAG = "SignUpActivity";
    //Context of activity
    private Context mContext = this;

    private TextInputEditText etName;
    private TextInputEditText etLastName;
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private Button btnSignup;
    private TextView tvLoginHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        etName = (TextInputEditText) findViewById(R.id.etName);
        etLastName = (TextInputEditText) findViewById(R.id.etLastName);
        etUsername = (TextInputEditText) findViewById(R.id.etUsernameSignup);
        etPassword = (TextInputEditText) findViewById(R.id.etPasswordSignup);
        btnSignup = findViewById(R.id.btnSignup);
        tvLoginHere = findViewById(R.id.loginHere);

        //Launch LoginActivity
        tvLoginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            }
        });

        //Signup user
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getEditableText().toString();
                String lastname = etLastName.getEditableText().toString();
                String username = etUsername.getEditableText().toString();
                String password = etPassword.getEditableText().toString();
                signUp(name, lastname, username, password);
            }
        });
    }

    private void signUp(String name, String lastname, String username, String password) {
        //Create parse user
        User user = new User();
        //Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        user.setLastname(lastname);
        //Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    goMainActivity();
                } else {
                    Toast.makeText(mContext, "There was an error with sign up", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.getMessage());
                }
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(mContext, MainActivity.class);
        startActivity(i);
        finish();
    }
}