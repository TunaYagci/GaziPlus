package com.hp2m.GaziPlus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;


public class About extends AppCompatActivity {

    private ImageView logo;
    private TextView header;
    private ImageView twitterLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Hakkımızda");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

        logo = (ImageView) findViewById(R.id.logo);
        header = (TextView) findViewById(R.id.header);
        YoYo.with(Techniques.FadeIn)
                .duration(2000)
                .playOn(header);
        YoYo.with(Techniques.SlideInLeft)
                .duration(2000)
                .playOn(logo);
        twitterLogo = (ImageView) findViewById(R.id.twitterLogo);
        twitterLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/GaziPlus"));
                startActivity(browserIntent);
            }
        });
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.Shake)
                        .duration(500)
                        .playOn(logo);
            }
        });
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.Shake)
                        .duration(500)
                        .playOn(header);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
