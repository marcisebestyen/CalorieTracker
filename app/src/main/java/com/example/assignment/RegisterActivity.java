package com.example.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    DatabaseHelper db;
    FirebaseAuth auth;
    FirebaseFirestore fsDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);
        auth = FirebaseAuth.getInstance();
        fsDb = FirebaseFirestore.getInstance();

        EditText etNewUsername = findViewById(R.id.etNewUsername);
        EditText etNewPassword = findViewById(R.id.etNewPassword);
        EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String username = etNewUsername.getText().toString();
            String password = etNewPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Minden mezőt ki kell tölteni!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "A jelszavak nem egyeznek!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.userExists(username)) {
                Toast.makeText(this, "Ez a felhasználónév már foglalt!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = db.registerUser(username, password);
            if (success) {
                Toast.makeText(this, "Sikeres regisztráció!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Hiba történt a regisztráció során!", Toast.LENGTH_SHORT).show();
            }
        });

        // Firebase register
        registerButton.setOnClickListener(v -> registerUser());
    }

    // Firebase register
    private void registerUser(){
        String username = registerUsernameEditText.getText().toString();
        String email = registerEmailEditText.getText().toString();
        String password = registerPasswordEditText.getText().toString();

        if (username.isEmpty()  email.isEmpty()  password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        saveUserDataToFirestore(username, email);
                        Toast.makeText(RegisterActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserDataToFirestore(String username, String email) {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            Map<String, Object> user = new HashMap<>();
            user.put("username", username);
            user.put("email", email);

            db.collection("users").document(userId)
                    .set(user)
                    .addOnSuccessListener(aVoid -> Toast.makeText(RegisterActivity.this, "User data saved.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Failed to save user data.", Toast.LENGTH_SHORT).show());
        }
    }
}
