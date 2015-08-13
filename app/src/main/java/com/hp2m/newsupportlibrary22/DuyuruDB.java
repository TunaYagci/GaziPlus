package com.hp2m.newsupportlibrary22;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class DuyuruDB extends SQLiteOpenHelper {

    public final static int DATABASE_VERSION = 1;
    public static final String TABLE_FAKULTE = "fakulte";
    public static final String TABLE_BOLUM = "bolum";
    public static final String COLUMNN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_TARIH = "tarih";
    public static final String COLUMN_CONTENTLINKS = "contentLinks";
    public static final String COLUMN_NEWSLINKS = "newsLinks";
    public static final String COLUMN_NEWOROLD = "newORold";
    public static final String COLUMN_IMAGELINKS = "img_link";
    private static final String DATABASE_NAME = "duyurular2.db";


    public DuyuruDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public int getDuyuruSayisi(String generalMode) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query;
        if (generalMode.equals("bolum"))
            query = "SELECT MAX(" + COLUMNN_ID + ") FROM " + TABLE_BOLUM;
        else
            query = "SELECT MAX(" + COLUMNN_ID + ") FROM " + TABLE_FAKULTE;
        Cursor c = db.rawQuery(query, null);
        int idMax = 0;
        if (c.moveToFirst())
            do {
                idMax = c.getInt((0));
            } while (c.moveToNext());

        //return idMax; // niye 1 fazla veriyor bilmiyorum
        return idMax;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("tuna", "onCreate DB");
        String query = "CREATE TABLE " + TABLE_BOLUM + " (" +
                COLUMNN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_TARIH + " TEXT, " +
                COLUMN_CONTENTLINKS + " TEXT, " +
                COLUMN_NEWSLINKS + " TEXT, " +
                COLUMN_NEWOROLD + " TEXT, " +
                COLUMN_IMAGELINKS + " TEXT" +
                ");";
        db.execSQL(query);

        String query2 = "CREATE TABLE " + TABLE_FAKULTE + " (" +
                COLUMNN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_TARIH + " TEXT, " +
                COLUMN_CONTENTLINKS + " TEXT, " +
                COLUMN_NEWSLINKS + " TEXT, " +
                COLUMN_NEWOROLD + " TEXT, " +
                COLUMN_IMAGELINKS + " TEXT" +
                ");";
        db.execSQL(query2);



        Log.i("tuna", "db created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("tuna", "onUpgrade");
        /*if (newVersion != 2) { // CAREFUL THERE, 2 is for first time? DB
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DUYURU);
            onCreate(db);
        }*/
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    public int updateDuyuru(DuyuruGetSet duyuru, String header, String generalMode) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(COLUMN_TITLE, duyuru.getTitle());
        contentValues.put(COLUMN_CONTENT, duyuru.getContent());
        contentValues.put(COLUMN_CONTENTLINKS, duyuru.getContentLinks());
        contentValues.put(COLUMN_NEWSLINKS, duyuru.getNewsLinks());
        //contentValues.put(COLUMN_NEWOROLD, duyuru.getNewORold());
        contentValues.put(COLUMN_IMAGELINKS, duyuru.getImageLinks());
        Log.i("tuna", "updated body is = " + duyuru.getContent());
        int i;
        if (generalMode.equals("bolum"))
            i = db.update(TABLE_BOLUM, contentValues, COLUMN_TITLE + "=?", new String[]{header});
        else
            i = db.update(TABLE_FAKULTE, contentValues, COLUMN_TITLE + "=?", new String[]{header});

        Log.i("tuna", "is update succesful? " + i);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return i;

        //db.setTransactionSuccessful();
        //String query="UPDATE "+TABLE_DUYURU+" SET columnname="+var+ "where columnid="+var2;
        //db.endTransaction();
        //db.close();
    }


    // Tarihe gore yemek ekle
    public void addDuyuru(DuyuruGetSet duyuru, String generalMode) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, duyuru.getTitle());
        contentValues.put(COLUMN_CONTENT, duyuru.getContent());
        contentValues.put(COLUMN_TARIH, duyuru.getTarih());
        contentValues.put(COLUMN_CONTENTLINKS, duyuru.getContentLinks());
        contentValues.put(COLUMN_NEWSLINKS, duyuru.getNewsLinks());
        contentValues.put(COLUMN_NEWOROLD, duyuru.getNewORold());
        contentValues.put(COLUMN_IMAGELINKS, duyuru.getImageLinks());
        SQLiteDatabase db = getWritableDatabase();
        if (generalMode.equals("bolum"))
            db.insert(TABLE_BOLUM, null, contentValues);
        else
            db.insert(TABLE_FAKULTE, null, contentValues);
        //db.update(TABLE_DUYURU, contentValues, COLUMNN_ID + "=" + 5, null);
        db.close();
    }

    public ArrayList<String> fetchMeMyDuyuru(int row, String generalMode) { // 1 is title, 2 is content
        Log.i("tuna", "fetchMeMyDuyuru");
        ArrayList<String> fetchMeMyDuyuru = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query;
        if (generalMode.equals("bolum"))
            query = "SELECT * FROM " + TABLE_BOLUM + " WHERE " + COLUMNN_ID + "=" + row;
        else
            query = "SELECT * FROM " + TABLE_FAKULTE + " WHERE " + COLUMNN_ID + "=" + row;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            for (int i = 1; i < 8; i++) { // 0=_id, 1=title 2=content 3=tarih, 4=contentLinks, 5=newsList 6=newORold 7=imageLinks
                fetchMeMyDuyuru.add(c.getString(i));
                //Log.i("tuna", "is this a loop");
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return fetchMeMyDuyuru;
    }


    public void clearTable(String generalMode) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (generalMode.equals("bolum")) {
            db.execSQL("DROP TABLE " + TABLE_BOLUM);
            String query = "CREATE TABLE " + TABLE_BOLUM + " (" +
                    COLUMNN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_CONTENT + " TEXT, " +
                    COLUMN_TARIH + " TEXT, " +
                    COLUMN_CONTENTLINKS + " TEXT, " +
                    COLUMN_NEWSLINKS + " TEXT, " +
                    COLUMN_NEWOROLD + " TEXT, " +
                    COLUMN_IMAGELINKS + " TEXT" +
                    ");";
            db.execSQL(query);
        } else {
            db.execSQL("DROP TABLE " + TABLE_FAKULTE);
            String query2 = "CREATE TABLE " + TABLE_FAKULTE + " (" +
                    COLUMNN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_CONTENT + " TEXT, " +
                    COLUMN_TARIH + " TEXT, " +
                    COLUMN_CONTENTLINKS + " TEXT, " +
                    COLUMN_NEWSLINKS + " TEXT, " +
                    COLUMN_NEWOROLD + " TEXT, " +
                    COLUMN_IMAGELINKS + " TEXT" +
                    ");";
            db.execSQL(query2);
        }
    }

    public void clearForLogOut() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_BOLUM, null, null);
            db.delete(TABLE_FAKULTE, null, null);
        } catch (Exception e) {
            Log.i("tuna", e.toString());
        }

    }

}