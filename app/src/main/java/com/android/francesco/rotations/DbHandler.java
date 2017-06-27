package com.android.francesco.rotations;

/**
 * Created by Francesco on 18/05/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class DbHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "userManager",
            TABLE_USERS = "users",
            KEY_ID = "id",
            KEY_USERNAME = "username",
            KEY_PASSWORD = "password",
            KEY_NAME = "name",
            KEY_SURNAME = "surname";

    public DbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_USERNAME + " TEXT," + KEY_PASSWORD + " TEXT," + KEY_NAME + " TEXT," + KEY_SURNAME + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        onCreate(db);
    }

    public void createUser(User user) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_NAME, user.getName());
        values.put(KEY_SURNAME, user.getSurname());

        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public User getUser(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS, new String[] { KEY_ID, KEY_USERNAME, KEY_PASSWORD}, KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null );

        if (cursor != null)
            cursor.moveToFirst();

        User user = new User(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
        db.close();
        cursor.close();
        return user;
    }

    public void deleteUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_USERS, KEY_ID + "=?", new String[] { String.valueOf(user.getId()) });
        db.close();
    }

    public int getUsersCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
        int count = cursor.getCount();
        db.close();
        cursor.close();

        return count;
    }

    public int updateUsers(User user) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_NAME, user.getName());
        values.put(KEY_SURNAME, user.getSurname());

        int rowsAffected = db.update(TABLE_USERS, values, KEY_ID + "=?", new String[] { String.valueOf(user.getId()) });
        db.close();

        return rowsAffected;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<User>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);

        if (cursor.moveToFirst()) {
            do {
                users.add(new User(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2)));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return users;
    }

    public void deleteAllUsers(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        onCreate(db);
    }
}