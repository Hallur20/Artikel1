package com.example.hvn15.firebaseapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticationActivity extends AppCompatActivity {
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mStatusField;
    private Button mRegisterBtn;
    private Button mLogoutBtn;
    private Button mLoginBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mEmailField = (EditText) findViewById(R.id.email_field);
        mPasswordField = (EditText) findViewById(R.id.password_field);
        mRegisterBtn = (Button) findViewById(R.id.register_button);
        mLogoutBtn = (Button) findViewById(R.id.logout_button);
        mLoginBtn = (Button) findViewById(R.id.login_button);
        mStatusField = (EditText) findViewById(R.id.status_field);

        //mAuth is being assigned to be able to do authentication to the database
        mAuth = FirebaseAuth.getInstance();

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if we click on the register button then we call the createaccount method and send email and password with it
                createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
            }
        });
        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if we click on the login button then we call the signout method
                signOut();
            }
        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if we click on the login button then we call the signin method and send email and password with it
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void createAccount(String email, String password) {
        // we check if the email exists and password is correct, and if these things are true then
        // check (task.isSuccessfull()), then we will be able to login
        // with the same email
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            //if the email doesnt exist
                            mStatusField.setText("Could not create account, please try again");
                            updateUI(null);
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        //check if the email exists in firebase and if password is correct
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            //here we update that user is not null, so unnecessary buttons and fields will be gone
                            updateUI(user);
                        } else {
                            mStatusField.setText("login failed");
                            //here we update that user is null (login failed), so necessary buttons and fields will be visible
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            //maybe this is unnecessary, but just in case we check if task isn't successful here
                            mStatusField.setText("login failed");
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        //if user is not null (logged in) then we remove the unnecessary buttons and fields
        if (user != null) {
            mStatusField.setText("user is logged in");

            findViewById(R.id.email_field).setVisibility(View.GONE);
            findViewById(R.id.password_field).setVisibility(View.GONE);
            findViewById(R.id.register_button).setVisibility(View.GONE);
            findViewById(R.id.login_button).setVisibility(View.GONE);
        } else {
            //if user is null (not logged in) then we add the necessary buttons and fields so we can register/login
            mStatusField.setText("user is not logged in");

            findViewById(R.id.email_field).setVisibility(View.VISIBLE);
            findViewById(R.id.password_field).setVisibility(View.VISIBLE);
            findViewById(R.id.register_button).setVisibility(View.VISIBLE);
            findViewById(R.id.login_button).setVisibility(View.VISIBLE);
        }
    }

    //pretty simple, we make use of the signouot method that comes with firebase, and update the ui that we are not logged in anymore
    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    public void goToMain(View view) {
        //if we are done here and logged in, then we can click on the button and go back to main activity
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }
}
