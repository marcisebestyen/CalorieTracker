package com.example.assignment;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private EditText etAge, etWeight, etHeight;
    private TextView tvSavedAge, tvSavedWeight, tvSavedHeight;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        tvSavedAge = findViewById(R.id.tvSavedAge);
        tvSavedWeight = findViewById(R.id.tvSavedWeight);
        tvSavedHeight = findViewById(R.id.tvSavedHeight);

        db = new DatabaseHelper(this);
        currentUserId = getCurrentUserId();
        loadUserData();

        Button btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnSaveProfile.setOnClickListener(v -> saveUserData());
    }

    private int getCurrentUserId() {
        return db.getLoggedInUserId();
    }

    private void loadUserData() {
        Cursor cursor = db.getUserData(currentUserId);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int ageIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_AGE);
                    int weightIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WEIGHT);
                    int heightIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_HEIGHT);

                    int age = cursor.getInt(ageIndex);
                    float weight = cursor.getFloat(weightIndex);
                    float height = cursor.getFloat(heightIndex);

                    tvSavedAge.setText(String.format("Életkor: %d év", age));
                    tvSavedWeight.setText(String.format(Locale.getDefault(), "Testsúly: %.1f kg", weight));
                    tvSavedHeight.setText(String.format(Locale.getDefault(), "Magasság: %.1f cm", height));

                    etAge.setText(String.valueOf(age));
                    etWeight.setText(String.format(Locale.getDefault(), "%.1f", weight));
                    etHeight.setText(String.format(Locale.getDefault(), "%.1f", height));
                } else {
                    tvSavedAge.setText("Nincsenek elmentett adatok");
                    tvSavedWeight.setText("");
                    tvSavedHeight.setText("");
                }
            } catch (IllegalArgumentException e) {
                Log.e("ProfileActivity", "Missing column: " + e.getMessage());
                Toast.makeText(this, "Hiba az adatok betöltésekor", Toast.LENGTH_SHORT).show();
            } finally {
                cursor.close();
            }
        }
    }

    private void saveUserData() {
        String ageStr = etAge.getText().toString();
        String weightStr = etWeight.getText().toString().replace(',', '.');
        String heightStr = etHeight.getText().toString().replace(',', '.');

        if (ageStr.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty()) {
            Toast.makeText(this, "Minden mezőt ki kell tölteni!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            float weight = Float.parseFloat(weightStr);
            float height = Float.parseFloat(heightStr);

            boolean success = db.updateUserData(currentUserId, age, weight, height);

            if (success) {
                Toast.makeText(this, "Adatok mentve!", Toast.LENGTH_SHORT).show();
                loadUserData();
            } else {
                Toast.makeText(this, "Hiba történt a mentés során!", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Érvénytelen számformátum!", Toast.LENGTH_SHORT).show();
        }
    }
}