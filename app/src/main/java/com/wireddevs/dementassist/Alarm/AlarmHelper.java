package com.wireddevs.dementassist.Alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class AlarmHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "alarmlist_db";

    public AlarmHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(Alarm.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Alarm.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void insertItem(String name,long time,int code,long interval,String daysinweek,int logo) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Alarm.COLUMN_ITEM_NAME, name);
        values.put(Alarm.COLUMN_ITEM_TIMESTAMP, time);
        values.put(Alarm.COLUMN_ITEM_CODE,code);
        values.put(Alarm.COLUMN_ITEM_INTERVAL,interval);
        values.put(Alarm.COLUMN_ITEM_WEEKLY,daysinweek);
        values.put(Alarm.COLUMN_ITEM_LOGO,logo);

        db.insert(Alarm.TABLE_NAME, null, values);

        // close db connection
        db.close();

    }

    public Alarm getAlarm(int code){
        SQLiteDatabase db = this.getReadableDatabase();
        Alarm tr=new Alarm();
        Cursor cursor = db.query(Alarm.TABLE_NAME,
                new String[]{Alarm.COLUMN_ITEM_NAME,Alarm.COLUMN_ITEM_TIMESTAMP,Alarm.COLUMN_ITEM_CODE,Alarm.COLUMN_ITEM_INTERVAL,Alarm.COLUMN_ITEM_WEEKLY,Alarm.COLUMN_ITEM_LOGO},
                Alarm.COLUMN_ITEM_CODE + "=?",
                new String[]{String.valueOf(code)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            tr.setName(cursor.getString(0));
            tr.setTimestamp(cursor.getLong(1));
            tr.setCode(cursor.getInt(2));
            tr.setInterval(cursor.getLong(3));
            tr.setDaysinweek(cursor.getString(4));
            tr.setLogo(cursor.getInt(5));
            cursor.close();
        }
        db.close();
        return tr;
    }

    public int getItemCount() {
        String countQuery = "SELECT  * FROM " + Alarm.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void deleteItem(int code) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Alarm.TABLE_NAME, Alarm.COLUMN_ITEM_CODE + " = ?",
                new String[]{String.valueOf(code)});
        db.close();
    }

    public void deleteAllData(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from "+ Alarm.TABLE_NAME);
        db.close();
    }

    public Cursor getAllData(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT * FROM "+Alarm.TABLE_NAME,null);
        return res;
    }

    public long getTimestamp(String name) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Alarm.TABLE_NAME,
                new String[]{Alarm.COLUMN_ITEM_NAME,Alarm.COLUMN_ITEM_TIMESTAMP},
                Alarm.COLUMN_ITEM_NAME + "=?",
                new String[]{name}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            long tr = cursor.getLong(1);
            // close the db connection
            cursor.close();
            db.close();
            return tr;
        }
        else{
            db.close();
            return -1;
        }
    }

    public String getName(int code) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Alarm.TABLE_NAME,
                new String[]{Alarm.COLUMN_ITEM_NAME,Alarm.COLUMN_ITEM_CODE},
                Alarm.COLUMN_ITEM_CODE + "=?",
                new String[]{String.valueOf(code)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            String tr = cursor.getString(0);
            // close the db connection
            cursor.close();
            db.close();
            return tr;
        }
        else{
            db.close();
            return null;
        }
    }

    public int getCode(String name){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Alarm.TABLE_NAME,
                new String[]{Alarm.COLUMN_ITEM_NAME,Alarm.COLUMN_ITEM_CODE},
                Alarm.COLUMN_ITEM_NAME + "=?",
                new String[]{name}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            int tr = cursor.getInt(1);
            // close the db connection
            cursor.close();
            db.close();
            return tr;
        }
        else{
            db.close();
            return -1;
        }
    }



    public long getInterval(int code){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Alarm.TABLE_NAME,
                new String[]{Alarm.COLUMN_ITEM_CODE,Alarm.COLUMN_ITEM_INTERVAL},
                Alarm.COLUMN_ITEM_CODE + "=?",
                new String[]{String.valueOf(code)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            long tr = cursor.getLong(3);
            // close the db connection
            cursor.close();
            db.close();
            return tr;
        }
        else{
            db.close();
            return -1;
        }
    }

    public int getLogo(int code){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Alarm.TABLE_NAME,
                new String[]{Alarm.COLUMN_ITEM_CODE,Alarm.COLUMN_ITEM_LOGO},
                Alarm.COLUMN_ITEM_CODE + "=?",
                new String[]{String.valueOf(code)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            int tr = cursor.getInt(1);
            // close the db connection
            cursor.close();
            db.close();
            return tr;
        }
        else{
            db.close();
            return -1;
        }
    }

    public String getDaysInWeek(int code){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Alarm.TABLE_NAME,
                new String[]{Alarm.COLUMN_ITEM_CODE,Alarm.COLUMN_ITEM_WEEKLY},
                Alarm.COLUMN_ITEM_CODE + "=?",
                new String[]{String.valueOf(code)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            String tr = cursor.getString(1);
            // close the db connection
            cursor.close();
            db.close();
            return tr;
        }
        else{
            db.close();
            return null;
        }
    }

    public void updateTime(String name,long timestamp) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Alarm.COLUMN_ITEM_TIMESTAMP, timestamp);

        db.update(Alarm.TABLE_NAME,values,Alarm.COLUMN_ITEM_NAME + " = ? ", new String[]{name});
        // close db connection
        db.close();

    }
}
