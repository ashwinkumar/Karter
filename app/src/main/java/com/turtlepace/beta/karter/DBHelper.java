package com.turtlepace.beta.karter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by akumar on 08/08/15.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "driver.db";
    public static final String USER_TABLE_NAME = "user";
    public static final String USER_COLUMN_DRIVER_ID = "id";
    public static final String USER_COLUMN_PASSWORD = "password";
    private static final String USER_DEFAULT_PASSWORD= "kArTeR@123";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table user " +
                        "(id integer primary key,password text)"
        );
    }
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS user");
        onCreate(database);
    }

    public boolean insertUser  (String userId, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String countQuery = "SELECT  * FROM " + USER_TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        if(cnt >0){
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", userId);
        contentValues.put("password", password);
        db.insert("user", null, contentValues);
        return true;
    }
    public String getUserId(){
        SQLiteDatabase db = this.getReadableDatabase();
        String res ="";
        Cursor cur =  db.rawQuery( "select * from user", null );
        if (cur != null ) {
            if (cur.moveToFirst()) {
                res = cur.getString(cur.getColumnIndex("id"));

            }
        }
        cur.close();
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, USER_TABLE_NAME);
        return numRows;
    }
    public void deleteTable ()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete * from user");
    }
    public boolean validatePassword(String pwd){
        return pwd.toString().equals(USER_DEFAULT_PASSWORD.toString());
    }


}