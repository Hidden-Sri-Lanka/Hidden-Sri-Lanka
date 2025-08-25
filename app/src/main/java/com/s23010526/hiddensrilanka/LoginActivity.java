package com.s23010526.hiddensrilanka;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast; // Added for onCancelled

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText loginUsername, loginPassword;
    Button loginButton;
    Button signUpRederect;
    private SessionManager sessionManager;

    // i have Define  database URL as a constant i have given reson in Signup Activity
    private static final String FIREBASE_DATABASE_URL = "https://hidden-sri-lanka-c3ec5-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        loginUsername = findViewById(R.id.userName); // getting data from xml file
        loginPassword = findViewById(R.id.password);
        loginButton = findViewById(R.id.logingButton);
        signUpRederect = findViewById(R.id.signUpRederect);

        loginButton.setOnClickListener(new View.OnClickListener() {// when user click on login button
            @Override
            public void onClick(View v) {// validate username and password
                boolean isUsernameValid = validateUsername();
                boolean isPasswordValid = validatePassword();
                if (isUsernameValid && isPasswordValid) {// if both are valid then check user
                    checkUser();
                }
            }
        });

        signUpRederect.setOnClickListener(new View.OnClickListener() {// when user click on sign up button
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);// it will rederect to sinup activity
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public Boolean validateUsername() {
        String val = loginUsername.getText().toString().trim(); // Added trim() to remove leading/trailing spaces
        if (val.isEmpty()) {
            loginUsername.setError("User Name cannot be Empty !"); // if user name is empty it will show error
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() { // validate password
        String val = loginPassword.getText().toString(); // No trim() here, as passwords can have leading/trailing spaces
        if (val.isEmpty()) {
            loginPassword.setError("Password cannot be Empty !");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    public void checkUser() {
        String userName = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim(); // Also trim password input for consistency

        // Get FirebaseDatabase instance with the correct URL
        FirebaseDatabase database = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL); // use the constant URL
        DatabaseReference reference = database.getReference("users");// refer to user table
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userName);// check if the username is equal to the user name entered by user

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {// it will check the user name and password
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {// if any data change in database
                if (snapshot.exists()) {// if user name exist in database
                    loginUsername.setError(null);
                    boolean credentialsValid = false;// flag to track if credentials are valid
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {// loop through all matching users (should be one due to unique usernames)


                        String passwordFromDB = userSnapshot.child("password").getValue(String.class);

                        if (Objects.equals(passwordFromDB, userPassword)) {// check if password is equal to the password entered by user
                            credentialsValid = true;
                            loginPassword.setError(null);// if both are valid then it will rederect to home activity

                            // Get user data from database
                            String actualUsernameFromDB = userSnapshot.child("username").getValue(String.class);
                            String emailFromDB = userSnapshot.child("email").getValue(String.class);
                            String nameFromDB = userSnapshot.child("name").getValue(String.class);

                            // Save session data
                            sessionManager.createLoginSession(// save session data
                                    actualUsernameFromDB != null ? actualUsernameFromDB : userName,// if actual username is null then it will save the user name entered by user
                                    emailFromDB != null ? emailFromDB : "",// if email is null then it will save empty string
                                    nameFromDB != null ? nameFromDB : ""// if name is null then it will save empty string
                            );

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);// rederect to home activity
                            intent.putExtra("USERNAME", actualUsernameFromDB != null ? actualUsernameFromDB : userName);// pass the user name to home activity
                            startActivity(intent);// start home activity
                            finish();// finish login activity
                            return; // Exit after successful login
                        }
                    }

                    // If loop completes and credentials are not valid
                    if (!credentialsValid) {
                        loginPassword.setError("Invalid Credentials!");
                        loginPassword.requestFocus();
                    }

                } else {
                    loginUsername.setError("User Does Not Exist");
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//if any error
                Toast.makeText(LoginActivity.this, "Database Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}