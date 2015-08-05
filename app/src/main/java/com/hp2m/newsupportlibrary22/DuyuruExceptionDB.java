package com.hp2m.newsupportlibrary22;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class DuyuruExceptionDB extends SQLiteOpenHelper {

    public final static int DATABASE_VERSION = 1;
    public static final String TABLE_EXCEPTION = "cengaziExceptions";
    public static final String COLUMNN_ID = "_id";
    public static final String COLUMN_HEADER = "header";
    public static final String COLUMN_LINK = "link";
    private static final String DATABASE_NAME = "duyuruExceptions.db";

    public DuyuruExceptionDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.i("tuna", "onCreate Exception DB");
        String query = "CREATE TABLE " + TABLE_EXCEPTION + " (" +
                COLUMNN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_HEADER + " TEXT, " +
                COLUMN_LINK + " TEXT" +
                ");";
        db.execSQL(query);

        Log.i("tuna", "Exception db created");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("tuna", "onUpgrade on DExceptionDB");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }


    public void addFailedDuyuru(DuyuruExceptionGetSet fetcher) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_HEADER, fetcher.getHeader());
        contentValues.put(COLUMN_LINK, fetcher.getLink());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_EXCEPTION, null, contentValues);
        //db.update(TABLE_DUYURU, contentValues, COLUMNN_ID + "=" + 5, null);
        db.close();
    }

    public void deleteFailedDuyuru(String header) {
        Log.i("tuna", "onDelete from ExceptionDB " + header);
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.delete(TABLE_EXCEPTION, COLUMN_HEADER + "=?", new String[]{header});
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public ArrayList<String> fetchFailedDuyuru(int row) {
        Log.i("tuna", "fetchMeMyDuyuru");
        ArrayList<String> fetchFailedDuyuru = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EXCEPTION + " WHERE " + COLUMNN_ID + "=" + row;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            for (int i = 1; i < 3; i++) { // 0=_id, 1=theader 2=Link
                fetchFailedDuyuru.add(c.getString(i));
                //Log.i("tuna", "is this a loop");
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return fetchFailedDuyuru;
    }

    public int getExceptionedDuyuruSayisi() {
        String query = "SELECT MAX(" + COLUMNN_ID + ") FROM " + TABLE_EXCEPTION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        int idMax = 0;
        if (c.moveToFirst())
            do {
                idMax = c.getInt((0));
            } while (c.moveToNext());

        return idMax - 1; // niye 1 fazla veriyor bilmiyorum
    }


}
