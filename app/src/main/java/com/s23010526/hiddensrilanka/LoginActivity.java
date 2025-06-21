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

    // Define your database URL as a constant or retrieve it from a config
    private static final String FIREBASE_DATABASE_URL = "https://hidden-sri-lanka-c3ec5-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

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
                        // Assuming your user data is directly under the node found by username
                        // If 'username' is the key of the node, userSnapshot.getKey() would be the username.
                        // And you are querying for a child "username", so the structure might be:
                        // users: {
                        //   <unique_user_id>: {
                        //     username: "actualUsername",
                        //     password: "userPassword",
                        //     ...
                        //   }
                        // }
                        // If so, then 'userName' in snapshot.child(userName) is incorrect.
                        // You should get the password from the userSnapshot directly if username is a field

                        String passwordFromDB = userSnapshot.child("password").getValue(String.class);

                        if (Objects.equals(passwordFromDB, userPassword)) {
                            credentialsValid = true;
                            loginPassword.setError(null);
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            // Pass the actual username from DB if it can differ in case from input
                            String actualUsernameFromDB = userSnapshot.child("username").getValue(String.class);
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
                // It's good practice to handle errors, e.g., show a Toast to the user
                Toast.makeText(LoginActivity.this, "Database Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                // You might want to log the error as well for debugging
                // Log.e("LoginActivityDBError", "onCancelled: " + error.getMessage());
            }
        });
    }
}