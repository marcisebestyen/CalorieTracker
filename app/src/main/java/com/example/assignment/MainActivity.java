package com.example.assignment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper db = new DatabaseHelper(this);
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private EditText etDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnHistory = findViewById(R.id.btnHistory);
        Button btnProfile = findViewById(R.id.btnProfile);
        EditText etCalories = findViewById(R.id.etCalories);
        EditText etMealType = findViewById(R.id.etMealType);
        Button datePicker = findViewById(R.id.btnDatePicker);
        etDate = findViewById(R.id.etDate);
        Button btnSave = findViewById(R.id.btnSave);

        etDate.setText(dateFormatter.format(new Date()));

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        datePicker.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> {
            try {
                int calories = Integer.parseInt(etCalories.getText().toString());
                String mealType = etMealType.getText().toString();
                String date = etDate.getText().toString();

                if (mealType.isEmpty() || date.isEmpty()) {
                    Toast.makeText(this, "Minden mezőt ki kell tölteni!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (db.addEntry(calories, mealType, date)) {
                    Toast.makeText(this, "Bejegyzés mentve!", Toast.LENGTH_SHORT).show();
                    etCalories.getText().clear();
                    etMealType.getText().clear();
                } else {
                    Toast.makeText(this, "Hiba történt!", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Érvénytelen kalóriaérték!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);
                    etDate.setText(dateFormatter.format(selectedDate.getTime()));
                },
                year, month, day
        );
        datePickerDialog.show();
    }
}