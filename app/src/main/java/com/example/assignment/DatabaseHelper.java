package com.example.assignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "calorietracker.db";
    private static final int DATABASE_VERSION = 1;

    // User table
    private static final String TABLE_USER = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    // Entries table
    private static final String TABLE_ENTRIES = "entries";
    private static final String COL_ENTRY_ID = "id";
    private static final String COL_CALORIES = "calories";
    private static final String COL_MEAL_TYPE = "meal_type";
    private static final String COL_DATE = "date";

    // User data table
    private static final String TABLE_USER_DATA = "user_data";
    public static final String COL_AGE = "age";
    public static final String COL_WEIGHT = "weight";
    public static final String COL_HEIGHT = "height";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USER + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT, " +
                COL_PASSWORD + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_ENTRIES + " (" +
                COL_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CALORIES + " INTEGER, " +
                COL_MEAL_TYPE + " TEXT, " +
                COL_DATE + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_USER_DATA + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY, " +
                COL_AGE + " INTEGER, " +
                COL_WEIGHT + " REAL, " +
                COL_HEIGHT + " REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_DATA);
        onCreate(db);
    }

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " +
                COL_USERNAME + "=? AND " + COL_PASSWORD + "=?", new String[]{username, password});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    public boolean addEntry(int calories, String mealType, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CALORIES, calories);
        values.put(COL_MEAL_TYPE, mealType);
        values.put(COL_DATE, date);
        long result = db.insert(TABLE_ENTRIES, null, values);
        return result != -1;
    }

    public boolean updateUserData(int age, float weight, float height) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_AGE, age);
        values.put(COL_WEIGHT, weight);
        values.put(COL_HEIGHT, height);
        long result = db.replace(TABLE_USER_DATA, null, values);
        return result != -1;
    }

    public Cursor getEntries() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ENTRIES, null);
    }

    public boolean userExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        long result = db.insert("users", null, values);
        db.close();
        return result != -1;
    }

    public Cursor getUserData(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USER_DATA + " WHERE " + COL_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
    }

    public boolean updateUserData(int userId, int age, float weight, float height) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, userId);
        values.put(COL_AGE, age);
        values.put(COL_WEIGHT, weight);
        values.put(COL_HEIGHT, height);

        long result = db.insertWithOnConflict(TABLE_USER_DATA, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    public int getLoggedInUserId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_USER_ID + " FROM " + TABLE_USER + " LIMIT 1", null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }
        cursor.close();
        return -1;
    }

    public boolean deleteAllEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_ENTRIES, null, null);
        return deletedRows > 0;
    }
}
