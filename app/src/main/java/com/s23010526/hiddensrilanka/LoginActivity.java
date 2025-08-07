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

    // Define your database URL as a constant i have given reson in Signup Activity
    private static final String FIREBASE_DATABASE_URL = "https://hidden-sri-lanka-c3ec5-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        loginUsername = findViewById(R.id.userName);
        loginPassword = findViewById(R.id.password);
        loginButton = findViewById(R.id.logingButton);
        signUpRederect = findViewById(R.id.signUpRederect);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isUsernameValid = validateUsername();
                boolean isPasswordValid = validatePassword();
                if (isUsernameValid && isPasswordValid) {
                    checkUser();
                }
            }
        });

        signUpRederect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
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
        String val = loginUsername.getText().toString().trim(); // Added trim()
        if (val.isEmpty()) {
            loginUsername.setError("User Name cannot be Empty !");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
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
        String userPassword = loginPassword.getText().toString().trim(); // Also trim password input for consistency if needed, but be careful

        // Get FirebaseDatabase instance with the correct URL
        FirebaseDatabase database = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL);
        DatabaseReference reference = database.getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userName);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    loginUsername.setError(null);
                    boolean credentialsValid = false;
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {


                        String passwordFromDB = userSnapshot.child("password").getValue(String.class);

                        if (Objects.equals(passwordFromDB, userPassword)) {
                            credentialsValid = true;
                            loginPassword.setError(null);

                            // Get user data from database
                            String actualUsernameFromDB = userSnapshot.child("username").getValue(String.class);
                            String emailFromDB = userSnapshot.child("email").getValue(String.class);
                            String nameFromDB = userSnapshot.child("name").getValue(String.class);

                            // Save session data
                            sessionManager.createLoginSession(
                                    actualUsernameFromDB != null ? actualUsernameFromDB : userName,
                                    emailFromDB != null ? emailFromDB : "",
                                    nameFromDB != null ? nameFromDB : ""
                            );

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("USERNAME", actualUsernameFromDB != null ? actualUsernameFromDB : userName);
                            startActivity(intent);
                            finish();
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