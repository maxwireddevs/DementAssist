package com.wireddevs.dementassist.Contacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ContactsHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "contactlist_db";

    public ContactsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(Contacts.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Contacts.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void insertItem(String name,String relations,String phonenum) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Contacts.COLUMN_ITEM_NAME, name);
        values.put(Contacts.COLUMN_ITEM_RELATIONS,relations);
        values.put(Contacts.COLUMN_ITEM_PHONENUM,phonenum);

        db.insert(Contacts.TABLE_NAME, null, values);

        // close db connection
        db.close();

    }

    public int getItemCount() {
        String countQuery = "SELECT  * FROM " + Contacts.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void deleteItem(String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Contacts.TABLE_NAME, Contacts.COLUMN_ITEM_PHONENUM + " = ?",
                new String[]{String.valueOf(phone)});
        db.close();
    }

    public void editItem(String phonenum,String newname,String newrelations,String newphonenum) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Contacts.TABLE_NAME, Contacts.COLUMN_ITEM_PHONENUM + " = ?",
                new String[]{String.valueOf(phonenum)});

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Contacts.COLUMN_ITEM_NAME, newname);
        values.put(Contacts.COLUMN_ITEM_RELATIONS,newrelations);
        values.put(Contacts.COLUMN_ITEM_PHONENUM,newphonenum);

        db.insert(Contacts.TABLE_NAME, null, values);

        // close db connection
        db.close();

    }

    public void deleteAllData(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from "+ Contacts.TABLE_NAME);
        db.close();
    }

    public Cursor getAllData(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT * FROM "+Contacts.TABLE_NAME,null);
        return res;
    }

    public String getPhone(String name) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Contacts.TABLE_NAME,
                new String[]{Contacts.COLUMN_ITEM_NAME, Contacts.COLUMN_ITEM_PHONENUM},
                Contacts.COLUMN_ITEM_NAME + "=?",
                new String[]{name}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            String num = cursor.getString(2);
            // close the db connection
            cursor.close();
            db.close();
            return num;
        } else {
            return null;
        }
    }

}
