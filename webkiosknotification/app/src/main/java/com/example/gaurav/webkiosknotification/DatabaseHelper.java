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





    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_NAME + "(name String ,avatar_url String,forks_count Integer,watchers_count Integer,full_name String)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);


    }

 public void insertdata(String name,String avatar_url,Integer forks_count,Integer watchers_count,String full_name) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,name);
        contentValues.put(COL_2,avatar_url);
        contentValues.put(COL_3,forks_count);
        contentValues.put(COL_4,watchers_count);
        contentValues.put(COL_5, full_name);


        db.insert(TABLE_NAME, null, contentValues);

 }

    public Cursor getList() {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME , null);
        return c;

    }





    public void removeAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_NAME, null, null);
    }

    public void updatedata(String name,String avatar_url,Integer forks_count,Integer watchers_count,String full_name) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cr1 = db.rawQuery("SELECT * FROM repos where name = " + "'" + name + "'", null);

        System.out.println("SELECT * FROM repos where name = " + "'" + name + "'" +"Watchers"+ watchers_count + "Cursor" + cr1.getCount());


        if (cr1.getCount() != 0) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_1,name);
            contentValues.put(COL_2,avatar_url);
            contentValues.put(COL_3,forks_count);
            contentValues.put(COL_4,watchers_count);
            contentValues.put(COL_5, full_name);

            System.out.println(TABLE_NAME + contentValues + COL_1 + "=" + "'" + name + "'");

            db.update(TABLE_NAME, contentValues, COL_1 + "=" + "'" + name + "'", null);

            System.out.println("I am update");

        }
    }





}
