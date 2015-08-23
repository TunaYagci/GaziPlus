package com.hp2m.newsupportlibrary22;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;


public class NotlarDetailed extends AppCompatActivity {

    private String dersKoduString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notlar_detailed);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dersKoduString = extras.getString("dersKodu");
            Log.i("tuna", "dersKoduString= " + dersKoduString);
        }
        TextView dersKodu = (TextView) findViewById(R.id.dersKodu);
        TextView dersAdi = (TextView) findViewById(R.id.dersAdi);
        TextView vize = (TextView) findViewById(R.id.vizeNotu);
        TextView finalNotJava = (TextView) findViewById(R.id.finalNotu);
        TextView but = (TextView) findViewById(R.id.butNotu);
        TextView harfNotu = (TextView) findViewById(R.id.harfNotNot);
        TextView kredi = (TextView) findViewById(R.id.krediNot);
        TextView sinifOrt = (TextView) findViewById(R.id.sinifOrtNot);

        NotlarDB db = new NotlarDB(this);
        ArrayList<String> dersObj;
        dersObj = db.fetchMeDers(dersKoduString);
        dersKodu.setText(dersObj.get(0));
        dersAdi.setText(dersObj.get(1));
        vize.setText(dersObj.get(2));
        finalNotJava.setText(dersObj.get(3));
        but.setText(dersObj.get(4));
        harfNotu.setText(dersObj.get(5));
        kredi.setText(dersObj.get(6));
        sinifOrt.setText(dersObj.get(7));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Notlar");
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }
}
