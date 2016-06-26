package com.example.yinhui.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Yinhui on 2016/6/12.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    String dropUser = "drop table if exists user";
    String dropPass = "drop table if exists pass";
    String createUser = "create table user (adminuser text primary key, password text)";
    String createPass = "create table pass (adminuser text, website text, account text, password text)";

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createPass);
        db.execSQL(createUser);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
