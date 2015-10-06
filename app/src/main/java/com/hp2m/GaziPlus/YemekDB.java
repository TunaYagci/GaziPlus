package com.hp2m.GaziPlus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class YemekDB extends SQLiteOpenHelper {

    public static final String TABLE_YEMEK = "haftalikyemekler16";
    public static final String COLUMNN_ID = "_id";
    public static final String COLUMN_TARIH = "tarih1";
    public static final String COLUMN_YEMEK1 = "yemek1";
    public static final String COLUMN_YEMEK2 = "yemek2";
    public static final String COLUMN_YEMEK3 = "yemek3";
    public static final String COLUMN_YEMEK4 = "yemek4";
    private static final String DATABASE_NAME = "yemeklistesi16.db";
    public static int DATABASE_VERSION = 1;

    public YemekDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public int getYemekSayisi() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MAX(" + COLUMNN_ID + ") FROM " + TABLE_YEMEK;
        Cursor c = db.rawQuery(query, null);
        int idMax = 0;
        if (c.moveToFirst())
            do {
                idMax = c.getInt((0));
            } while (c.moveToNext());
        db.close();
        //return idMax; // niye 1 fazla veriyor bilmiyorum
        return idMax;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("tuna", "onCreate DB");
        String query = "CREATE TABLE " + TABLE_YEMEK + " (" +
                COLUMNN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TARIH + " TEXT, " +
                COLUMN_YEMEK1 + " TEXT, " +
                COLUMN_YEMEK2 + " TEXT, " +
                COLUMN_YEMEK3 + " TEXT, " +
                COLUMN_YEMEK4 + " TEXT" +
                ");";
        db.execSQL(query);

        Log.i("tuna", "db created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("tuna", "onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_YEMEK);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    // Tarihe gore yemek ekle
    public void addHaftalikYemek(YemekGetSet yemek, int id, boolean isUpdating) {
        Log.i("tuna", "addHaftalikYemek " + yemek.getYemek1());
        Log.i("tuna", "yemek sayisi= " + getYemekSayisi());
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TARIH, yemek.getTarih());
        contentValues.put(COLUMN_YEMEK1, yemek.getYemek1());
        contentValues.put(COLUMN_YEMEK2, yemek.getYemek2());
        contentValues.put(COLUMN_YEMEK3, yemek.getYemek3());
        contentValues.put(COLUMN_YEMEK4, yemek.getYemek4());
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        if (isUpdating) {
            db.update(TABLE_YEMEK, contentValues, COLUMNN_ID + "=" + id, null);
        } else {
            db.insert(TABLE_YEMEK, null, contentValues);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public ArrayList<String> fetchMeMyFood(int day) { // 1 is Monday
        Log.i("tuna", "fetchMeMyFood");
        ArrayList<String> yemekListFromDB = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_YEMEK + " WHERE " + COLUMNN_ID + "=" + day;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            for (int i = 1; i < 6; i++) { // getColumnNumber returns 5
                yemekListFromDB.add(c.getString(i));
                //Log.i("tuna", "is this a loop");
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        //Log.i("tuna", yemekListFromDB.toString());
        //Log.i("tuna", "goodbye");
        // Log.i("tuna", "this is 0 " + yemekListFromDB.get(0));
        return yemekListFromDB;
    }


    public void clearYemekDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        db.delete(TABLE_YEMEK, null, null);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

    }

}