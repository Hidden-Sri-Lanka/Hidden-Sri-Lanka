package com.s23010526.hiddensrilanka;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

    EditText signupName, signupUserName, signupEmail, signupPassword, signupRePassword;
    Button signupButton;
    Button loginRedirectText;

    // Define your database URL as a constant
    private static final String FIREBASE_DATABASE_URL = "https://hidden-sri-lanka-c3ec5-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // Initialize views
        signupName = findViewById(R.id.userName); // This might be reused for name
        signupUserName = findViewById(R.id.userName);
        signupEmail = findViewById(R.id.email);
        signupPassword = findViewById(R.id.password);
        signupRePassword = findViewById(R.id.rePassword);
        signupButton = findViewById(R.id.signUpButton);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get FirebaseDatabase instance with the correct URL
                FirebaseDatabase database = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL);
                DatabaseReference reference = database.getReference("users");

                String name = signupName.getText().toString().trim();
                String email = signupEmail.getText().toString().trim();
                String username = signupUserName.getText().toString().trim();
                String password = signupPassword.getText().toString();
                String rePassword = signupRePassword.getText().toString();

                // Validation
                if (name.isEmpty()) {
                    signupName.setError("Name cannot be empty");
                    return;
                }
                if (email.isEmpty()) {
                    signupEmail.setError("Email cannot be empty");
                    return;
                }
                if (username.isEmpty()) {
                    signupUserName.setError("Username cannot be empty");
                    return;
                }
                if (password.isEmpty()) {
                    signupPassword.setError("Password cannot be empty");
                    return;
                }
                if (!password.equals(rePassword)) {
                    signupRePassword.setError("Passwords do not match");
                    return;
                }

                // Create helper class instance
                HelperClass helperClass = new HelperClass(name, email, username, password);

                // Save to Firebase
                reference.child(username).setValue(helperClass).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Failed to create account", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toLogin(v);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void toLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
