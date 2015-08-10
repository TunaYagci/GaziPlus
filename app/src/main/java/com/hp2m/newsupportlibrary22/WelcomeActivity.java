package com.hp2m.newsupportlibrary22;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;


public class WelcomeActivity extends AppCompatActivity {

    private RelativeLayout ogrenciLoginLayout, betaTesterLoginLayout, ogrenciLoginFinal;
    private TextView ogrenciLoginErrorText;
    private EditText ogrenciLoginEditText;
    private ImageView logo;
    private boolean onGoBack = false;
    private SharedPreferences sP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sP = getSharedPreferences("user", Context.MODE_PRIVATE);
        if (sP.getBoolean("isLoginSuccessful", false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ogrenciLoginLayout = (RelativeLayout) findViewById(R.id.ogrenciLoginLayout);
        betaTesterLoginLayout = (RelativeLayout) findViewById(R.id.betaTesterLayout);
        ogrenciLoginFinal = (RelativeLayout) findViewById(R.id.ogrenciLoginFinal);
        ogrenciLoginErrorText = (TextView) findViewById(R.id.ogrenciLoginErrorText);
        ogrenciLoginEditText = (EditText) findViewById(R.id.ogrenciLoginEditText);
        logo = (ImageView) findViewById(R.id.logo);
        YoYo.with(Techniques.DropOut)
                .duration(300)
                .playOn(logo);

    }


    @Override
    public void onBackPressed() {
        if (onGoBack) {

            YoYo.with(Techniques.SlideOutRight)
                    .duration(250)
                    .playOn(ogrenciLoginEditText);
            YoYo.with(Techniques.SlideOutLeft)
                    .duration(250)
                    .playOn(ogrenciLoginFinal);
            if (ogrenciLoginErrorText.getVisibility() == View.VISIBLE)
                YoYo.with(Techniques.FadeOut)
                        .duration(250)
                        .playOn(ogrenciLoginErrorText);
            ogrenciLoginErrorText.postDelayed((new Runnable() {
                @Override
                public void run() {
                    ogrenciLoginErrorText.setVisibility(View.GONE);
                }
            }), 250);

            YoYo.with(Techniques.BounceIn)
                    .duration(250)
                    .playOn(ogrenciLoginLayout);
            YoYo.with(Techniques.BounceIn)
                    .duration(250)
                    .playOn(betaTesterLoginLayout);


            onGoBack = false;
            return;
        } else
            super.onBackPressed();
    }

    public void ogrenciLogin(View v) {
        onGoBack = true;
        YoYo.with(Techniques.Wave)
                .duration(250)
                .playOn(logo);

        YoYo.with(Techniques.SlideOutDown)
                .duration(250)
                .playOn(ogrenciLoginLayout);
        YoYo.with(Techniques.SlideOutDown)
                .duration(250)
                .playOn(betaTesterLoginLayout);

        ogrenciLoginEditText.setVisibility(View.VISIBLE);
        ogrenciLoginFinal.setVisibility(View.VISIBLE);

        YoYo.with(Techniques.BounceIn)
                .duration(250)
                .playOn(ogrenciLoginEditText);
        YoYo.with(Techniques.BounceIn)
                .duration(250)
                .playOn(ogrenciLoginFinal);


    }

    public void betaTesterLogin(View v) {
        YoYo.with(Techniques.RollOut)
                .duration(250)
                .playOn(logo);
        YoYo.with(Techniques.RollOut)
                .duration(250)
                .playOn(betaTesterLoginLayout);
        YoYo.with(Techniques.RollOut)
                .duration(250)
                .playOn(ogrenciLoginLayout);
        logo.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences.Editor editor = sP.edit();
                editor.putString("defaultFakulteLink", "http://mf.gazi.edu.tr/posts?type=news");
                editor.putString("defaultBolumLink", "http://mf-bm.gazi.edu.tr/posts?type=news");
                editor.putString("duyuruLink", "http://mf-bm.gazi.edu.tr/posts?type=news");
                editor.putString("generalMode", "bolum");
                editor.putBoolean("isLoginSuccessful", true);
                editor.commit();

                //extra
                sP = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                editor = sP.edit();
                editor.putString("checkbox", "checked");
                editor.putString("ogrNo", "141180068");
                editor.putString("parola", "tuna124");
                editor.commit();

                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 250);
    }


    public void ogrenciLoginFinal(View v) {
        ogrenciLoginErrorText.setVisibility(View.VISIBLE);
        if (ogrenciLoginEditText.getText().toString().length() < 9) {
            YoYo.with(Techniques.BounceIn)
                    .duration(250)
                    .playOn(ogrenciLoginErrorText);
            YoYo.with(Techniques.Wobble)
                    .duration(250)
                    .playOn(ogrenciLoginEditText);
        } else {
            // Login successful
            YoYo.with(Techniques.RollOut)
                    .duration(250)
                    .playOn(logo);
            YoYo.with(Techniques.RollOut)
                    .duration(250)
                    .playOn(ogrenciLoginEditText);
            YoYo.with(Techniques.RollOut)
                    .duration(250)
                    .playOn(ogrenciLoginFinal);
            ogrenciLoginErrorText.setVisibility(View.GONE);

            logo.postDelayed(new Runnable() {
                @Override
                public void run() {

                    SharedPreferences.Editor editor = sP.edit();
                    editor.putString("defaultFakulteLink", "http://mf.gazi.edu.tr/posts?type=news");
                    editor.putString("defaultBolumLink", "http://mf-bm.gazi.edu.tr/posts?type=news");
                    editor.putString("duyuruLink", "http://mf-bm.gazi.edu.tr/posts?type=news");
                    editor.putString("generalMode", "bolum");
                    editor.putBoolean("isLoginSuccessful", true);
                    editor.commit();
                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 250);
        }
    }

}
