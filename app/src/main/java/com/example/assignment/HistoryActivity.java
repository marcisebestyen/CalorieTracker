package com.example.assignment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private TextView tvHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        db = new DatabaseHelper(this);
        tvHistory = findViewById(R.id.tvHistory);

        Button btnDeleteHistory = findViewById(R.id.btnDeleteHistory);
        btnDeleteHistory.setOnClickListener(v -> showDeleteConfirmationDialog());

        loadHistoryData();
    }

    private void loadHistoryData() {
        Cursor cursor = db.getEntries();
        StringBuilder builder = new StringBuilder();

        if (cursor != null) {
            try {
                int caloriesIndex = cursor.getColumnIndexOrThrow("calories");
                int mealTypeIndex = cursor.getColumnIndexOrThrow("meal_type"); // Fixed column name
                int dateIndex = cursor.getColumnIndexOrThrow("date");

                if (cursor.getCount() == 0) {
                    tvHistory.setText("Nincs adat");
                    return;
                }

                while (cursor.moveToNext()) {
                    int calories = cursor.getInt(caloriesIndex);
                    String mealType = cursor.getString(mealTypeIndex);
                    String date = cursor.getString(dateIndex);
                    builder.append(date)
                            .append(" - ")
                            .append(mealType)
                            .append(": ")
                            .append(calories)
                            .append(" kalória\n");
                }

                tvHistory.setText(builder.toString());
            } catch (IllegalArgumentException e) {
                Log.e("HistoryActivity", "Missing database column: " + e.getMessage());
                tvHistory.setText("Hiba az adatok betöltésekor");
            } finally {
                cursor.close();
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Minden adat törlése")
                .setMessage("Biztosan törölni szeretné az összes bejegyzést?")
                .setPositiveButton("Törlés", (dialog, which) -> deleteHistory())
                .setNegativeButton("Mégse", null)
                .show();
    }

    private void deleteHistory() {
        if (db.deleteAllEntries()) {
            Toast.makeText(this, "Minden adat sikeresen törölve", Toast.LENGTH_SHORT).show();
            tvHistory.setText("Nincs adat");
        } else {
            Toast.makeText(this, "Nincs törölhető adat", Toast.LENGTH_SHORT).show();
        }
    }
}