package com.hp2m.newsupportlibrary22;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Tuna on 8/19/2015.
 */
public class NotificationzDB extends SQLiteOpenHelper {

    public final static int DATABASE_VERSION = 1;
    public static final String TABLE_FAKULTE = "fakulte";
    public static final String TABLE_BOLUM = "bolum";
    public static final String COLUMNN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    private static final String DATABASE_NAME = "notificationz.db";


    public NotificationzDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public int getBildirimSayisi(String generalMode) {
        SQLiteDatabase db = this.getReadableDatabase();
        int idMax = 0;
        String query;
        if (generalMode.equals("bolum"))
            query = "SELECT MAX(" + COLUMNN_ID + ") FROM " + TABLE_BOLUM;
        else
            query = "SELECT MAX(" + COLUMNN_ID + ") FROM " + TABLE_FAKULTE;
        Cursor c = db.rawQuery(query, null);
        idMax = 0;
        if (c.moveToFirst())
            do {
                idMax = c.getInt((0));
            } while (c.moveToNext());

        //return idMax; // niye 1 fazla veriyor bilmiyorum
        c.close();
        db.close();
        return idMax;
    }

    public void deleteAllNotificationz() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        db.delete(TABLE_BOLUM, null, null);
        db.delete(TABLE_FAKULTE, null, null);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.i("gazinotifications", "onCreate DB");
        String query = "CREATE TABLE " + TABLE_BOLUM + " (" +
                COLUMNN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT" +
                ");";
        db.execSQL(query);

        String query2 = "CREATE TABLE " + TABLE_FAKULTE + " (" +
                COLUMNN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT" +
                ");";
        db.execSQL(query2);

    }

    public void addNotification(String title, String generalMode) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, title);
        SQLiteDatabase db = getWritableDatabase();
        if (generalMode.equals("bolum"))
            db.insert(TABLE_BOLUM, null, contentValues);
        else
            db.insert(TABLE_FAKULTE, null, contentValues);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public String fetchMeNotificationz(int row, String generalMode) { // 1 is title, 2 is content
        String theTitle = "none";
        SQLiteDatabase db = this.getReadableDatabase();
        String query;
        if (generalMode.equals("bolum"))
            query = "SELECT * FROM " + TABLE_BOLUM + " WHERE " + COLUMNN_ID + "=" + row;
        else
            query = "SELECT * FROM " + TABLE_FAKULTE + " WHERE " + COLUMNN_ID + "=" + row;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            for (int i = 1; i < 2; i++) { // 0=_id, 1=title 2=content 3=tarih, 4=contentLinks, 5=newsList 6=newORold 7=imageLinks
                theTitle = c.getString(i);
                //Log.i("tuna", "is this a loop");
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return theTitle;
    }

}
