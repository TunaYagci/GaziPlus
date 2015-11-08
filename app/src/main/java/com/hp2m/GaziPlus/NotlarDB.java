package com.hp2m.GaziPlus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Tuna on 8/5/2015.
 */
public class NotlarDB extends SQLiteOpenHelper {

    public final static int DATABASE_VERSION = 1;
    public static final String TABLE_NOTLAR = "notlar2";
    public static final String COLUMNN_ID = "_id";
    public static final String COLUMN_DERSKODU = "ders_kodu";
    public static final String COLUMN_DERSADI = "ders_adi";
    public static final String COLUMN_VIZE = "vize";
    public static final String COLUMN_FINAL = "final";
    public static final String COLUMN_BUT = "but";
    public static final String COLUMN_BASARINOTU = "basari_notu";
    public static final String COLUMN_KREDI = "kredi";
    public static final String COLUMN_SINIFORT = "sinif_ort";

    private static final String DATABASE_NAME = "notlar2.db";

    private Context context;

    public NotlarDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("tuna", "onCreate DB");
        String query = "CREATE TABLE " + TABLE_NOTLAR + " (" +
                COLUMNN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DERSKODU + " TEXT, " +
                COLUMN_DERSADI + " TEXT, " +
                COLUMN_VIZE + " TEXT, " +
                COLUMN_FINAL + " TEXT, " +
                COLUMN_BUT + " TEXT, " +
                COLUMN_BASARINOTU + " TEXT, " +
                COLUMN_KREDI + " TEXT, " +
                COLUMN_SINIFORT + " TEXT" +
                ");";
        db.execSQL(query);

        Log.i("tuna", "db created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("tuna", "onUpgrade");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    public void addorUpdateNot(NotInformation not) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        ContentValues contentValues = new ContentValues();
        Log.i("tuna", "saving dersKodu " + not.dersKodu);
        contentValues.put(COLUMN_DERSKODU, not.dersKodu);
        contentValues.put(COLUMN_DERSADI, not.dersAdi);
        contentValues.put(COLUMN_VIZE, not.vizeNotu);
        contentValues.put(COLUMN_FINAL, not.finalNotu);
        contentValues.put(COLUMN_BUT, not.butNotu);
        contentValues.put(COLUMN_BASARINOTU, not.basariNotu);
        contentValues.put(COLUMN_KREDI, not.kredi);
        contentValues.put(COLUMN_SINIFORT, not.sinifOrt);

        db.replace(TABLE_NOTLAR, null, contentValues);

        db.setTransactionSuccessful();
        db.endTransaction();

        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTLAR, null, null);
        db.close();
    }

    public ArrayList<String> fetchMeDers(String dersKodu) {
        Log.i("tuna", "dersKodu= " + dersKodu);
        ArrayList<String> fetchMeDers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        //String query = "SELECT * FROM " + TABLE_NOTLAR + " WHERE " + COLUMN_DERSKODU + "=" + dersKodu;

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NOTLAR + " WHERE " + COLUMN_DERSKODU + "=?", new String[]{dersKodu});
        //Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            for (int i = 1; i < 9; i++) { // 0=_id, 1=dersKodu 2=dersAdi 3=vize 4=final 5=but 6=basariNotu 7=kredi 8=sinifOrt 9=ogrNo
                fetchMeDers.add(c.getString(i));
                Log.i("tuna", "c.getString= " + c.getString(i));
                //Log.i("tuna", "is this a loop");
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return fetchMeDers;
    }


}
