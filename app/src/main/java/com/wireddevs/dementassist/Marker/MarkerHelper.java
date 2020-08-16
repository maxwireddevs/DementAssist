package com.wireddevs.dementassist.Marker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MarkerHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "markerlist_db";

    public MarkerHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(MarkerStore.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + MarkerStore.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void insertItem(String name,double v,double v1,int type) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(MarkerStore.COLUMN_ITEM_NAME, name);
        values.put(MarkerStore.COLUMN_ITEM_V,v);
        values.put(MarkerStore.COLUMN_ITEM_V1,v1);
        values.put(MarkerStore.COLUMN_ITEM_TYPE,type);

        db.insert(MarkerStore.TABLE_NAME, null, values);

        // close db connection
        db.close();

    }

    public int getItemCount() {
        String countQuery = "SELECT  * FROM " + MarkerStore.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void deleteItem(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MarkerStore.TABLE_NAME, MarkerStore.COLUMN_ITEM_NAME + " = ?",
                new String[]{String.valueOf(name)});
        db.close();
    }

    public void editItem(String name,double newv,double newv1) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(MarkerStore.COLUMN_ITEM_V,newv);
        values.put(MarkerStore.COLUMN_ITEM_V1,newv1);

        db.update(MarkerStore.TABLE_NAME,values, MarkerStore.COLUMN_ITEM_NAME + " = ? ", new String[]{name});
        // close db connection
        db.close();

    }

    public void deleteAllData(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from "+ MarkerStore.TABLE_NAME);
        db.close();
    }

    public Cursor getAllData(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT * FROM "+ MarkerStore.TABLE_NAME,null);
        return res;
    }

    public double getV(String name) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(MarkerStore.TABLE_NAME,
                new String[]{MarkerStore.COLUMN_ITEM_NAME, MarkerStore.COLUMN_ITEM_V},
                MarkerStore.COLUMN_ITEM_NAME + "=?",
                new String[]{name}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            double num = cursor.getDouble(1);
            // close the db connection
            cursor.close();
            db.close();
            return num;
        } else {
            return -1;
        }
    }
    public double getV1(String name) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(MarkerStore.TABLE_NAME,
                new String[]{MarkerStore.COLUMN_ITEM_NAME, MarkerStore.COLUMN_ITEM_V1},
                MarkerStore.COLUMN_ITEM_NAME + "=?",
                new String[]{name}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            double num = cursor.getDouble(1);
            // close the db connection
            cursor.close();
            db.close();
            return num;
        } else {
            return -1;
        }
    }

    public int getType(String name){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(MarkerStore.TABLE_NAME,
                new String[]{MarkerStore.COLUMN_ITEM_NAME, MarkerStore.COLUMN_ITEM_TYPE},
                MarkerStore.COLUMN_ITEM_NAME + "=?",
                new String[]{name}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            int num = cursor.getInt(1);
            // close the db connection
            cursor.close();
            db.close();
            return num;
        } else {
            return -1;
        }
    }
}
