package com.s23010526.hiddensrilanka;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText signupEmail, signupUsername, signupPassword, signupRePassword; // Added signupRePassword
    Button loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    // Define your database URL as a constant
    // reson the default authentication is not with sigappoor server so i got error so i have specify the databae url now it working
    private static final String FIREBASE_DATABASE_URL = "https://hidden-sri-lanka-c3ec5-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // getting data from xml file
        signupUsername = findViewById(R.id.userName); // This is the "User Name" from XML
        signupEmail = findViewById(R.id.email);
        signupPassword = findViewById(R.id.password);
        signupRePassword = findViewById(R.id.rePassword); // Initialize for re-typed password
        signupButton = findViewById(R.id.signUpButton);
        loginRedirectText = findViewById(R.id.loginRedirectText);


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get FirebaseDatabase instance with the correct URL
                database = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL);
                reference = database.getReference("users");

                String email = signupEmail.getText().toString().trim();
                String username = signupUsername.getText().toString().trim(); // This comes from R.id.userName
                String password = signupPassword.getText().toString().trim();
                String rePassword = signupRePassword.getText().toString().trim();

                // Basic validation for input fields
                if (email.isEmpty() || username.isEmpty() || password.isEmpty()) { // Removed name check
                    Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(rePassword)) {
                    Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    signupRePassword.setError("Passwords do not match");
                    signupPassword.setError("Passwords do not match");
                    return;
                } else {
                    signupRePassword.setError(null);
                    signupPassword.setError(null);
                }

                HelperClass helperClass = new HelperClass(username, email, username, password);

                reference.child(username).setValue(helperClass)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(SignUpActivity.this, "You Have Signed Up Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(SignUpActivity.this, "Sign Up Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

}