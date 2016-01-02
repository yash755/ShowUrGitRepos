package com.example.gaurav.webkiosknotification;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class DatabaseHelper  extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "GitHubRepo.db";
    public static final String TABLE_NAME = "repos";
    public static final String COL_1 = "name";
    public static final String COL_2 = "avatar_url";
    public static final String COL_3 = "forks_count";
    public static final String COL_4 = "watchers_count";
    public static final String COL_5 = "full_name";
    public static final String COL_6 = "login";




    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_NAME + "(name String ,avatar_url String,forks_count Integer,watchers_count Integer,full_name String,login String )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);


    }

 public void insertdata(String name,String avatar_url,Integer forks_count,Integer watchers_count,String full_name,String login) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,name);
        contentValues.put(COL_2,avatar_url);
        contentValues.put(COL_3,forks_count);
        contentValues.put(COL_4,watchers_count);
        contentValues.put(COL_5, full_name);
        contentValues.put(COL_6,login);

        db.insert(TABLE_NAME, null, contentValues);

 }

    public Cursor getList() {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME , null);
        return c;

    }





    public void removeAll() {
        // db.delete(String tableName, String whereClause, String[] whereArgs);
        // If whereClause is null, it will delete all rows.
        SQLiteDatabase db = this.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        db.delete(DatabaseHelper.TABLE_NAME, null, null);
    }





}
