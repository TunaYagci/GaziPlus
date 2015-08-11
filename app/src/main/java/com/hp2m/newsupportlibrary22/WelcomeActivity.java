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
    private String defaultFakulteLink, defaultBolumLink;


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

    public void switcher(String ogrNo) {
        String fakulteNo = ogrNo.substring(2, 4);
        String bolumNo = ogrNo.substring(4, 6);

        switch (fakulteNo) {
            case "01":
                defaultFakulteLink = "http://dent.gazi.edu.tr/posts?type=news";
                break;
            case "02":
                defaultFakulteLink = "http://pharmacy.gazi.edu.tr/posts?type=news";
                break;
            case "03":
                defaultFakulteLink = "http://edebiyat.gazi.edu.tr/posts?type=news";
                break;
            case "04":
                defaultFakulteLink = "http://esef.gazi.edu.tr/posts?type=news";
                break;
            case "05":
                defaultFakulteLink = "http://fef.gazi.edu.tr/posts?type=news";
                break;
            case "06":
                defaultFakulteLink = "http://gef.gazi.edu.tr/posts?type=news";
                break;
            case "07":
                defaultFakulteLink = "http://gsf.gazi.edu.tr/posts?type=news";
                break;
            case "08":
                defaultFakulteLink = "http://hukuk.gazi.edu.tr/posts?type=news";
                break;
            case "9":
                defaultFakulteLink = "http://iibf.gazi.edu.tr/posts?type=news";
                break;
            case "10":
                defaultFakulteLink = "http://ilet.gazi.edu.tr/posts?type=news";
                break;
            case "11":
                defaultFakulteLink = "http://mf.gazi.edu.tr/posts?type=news";
                break;
            case "12":
                defaultFakulteLink = "http://mim.gazi.edu.tr/posts?type=news";
                break;
            case "13":
                defaultFakulteLink = "http://polatlifef.gazi.edu.tr/posts?type=news";
                break;
            case "14":
                defaultFakulteLink = "http://sbf.gazi.edu.tr/posts?type=news";
                break;
            case "15":
                defaultFakulteLink = "http://stf.gazi.edu.tr/posts?type=news";
                break;
            case "16":
                defaultFakulteLink = "http://mef.gazi.edu.tr/posts?type=news";
                break;
            case "17":
                defaultFakulteLink = "http://tef.gazi.edu.tr/posts?type=news";
                break;
            case "18":
                defaultFakulteLink = "http://tf.gazi.edu.tr/posts?type=news";
                break;
            case "19":
                defaultFakulteLink = "http://med.gazi.edu.tr/posts?type=news";
                break;
            case "20":
                defaultFakulteLink = "http://ttef.gazi.edu.tr/posts?type=news";
                break;
            case "21":
                defaultFakulteLink = "http://turizm.gazi.edu.tr/posts?type=news";
                break;
            // AKADEMÝK BÝRÝMLER SON
            // Devlet Konservatuvarý
            case "22":
                defaultFakulteLink = "http://konservatuvar.gazi.edu.tr/posts?type=news";
                break;
            // Devlet Konservatuvarý SON
            // Enstitüler
            case "23":
                defaultFakulteLink = "http://be.gazi.edu.tr/posts?type=news";
                break;
            case "24":
                defaultFakulteLink = "http://egtbil.gazi.edu.tr/posts?type=news";
                break;
            case "25":
                defaultFakulteLink = "http://fbe.gazi.edu.tr/posts?type=news";
                break;
            case "26":
                defaultFakulteLink = "http://kaza.gazi.edu.tr/posts?type=news";
                break;
            case "27":
                defaultFakulteLink = "http://saglikb.gazi.edu.tr/posts?type=news";
                break;
            case "28":
                defaultFakulteLink = "http://sbe.gazi.edu.tr/posts?type=news";
                break;
            case "29":
                defaultFakulteLink = "http://gse.gazi.edu.tr/posts?type=news";
                break;
            case "30":
                defaultFakulteLink = "http://konservatuvar.gazi.edu.tr/posts?type=news";
                break;
            // Enstitü SON

            // Yüksekokullar
            case "31":
                defaultFakulteLink = "http://besyo.gazi.edu.tr/posts?type=news";
                break;
            case "32":
                defaultFakulteLink = "http://ydyo.gazi.edu.tr/posts?type=news";
                break;
            case "33":
                defaultFakulteLink = "http://bankacilik.gazi.edu.tr/posts?type=news";
                break;
            case "34":
                defaultFakulteLink = "http://tkyo.gazi.edu.tr/posts?type=news";
                break;
            case "35":
                defaultFakulteLink = "http://konservatuvar.gazi.edu.tr/posts?type=news";
                break;
            // Yüksekokullar son

            // Meslek Yüksekokullarý atlýyorum
            /*case "49":
                defaultFakulteLink = "http://enformatik.gazi.edu.tr/"; MIN ITEM=2, EKLEYEMEM
                break;*/
            case "50":
                defaultFakulteLink = "http://ydyo.gazi.edu.tr/posts?type=news";
                break;
            /*case "51":
                defaultFakulteLink = "http://aiit.gazi.edu.tr/"; // no news
                break;*/
            /*case "52":
                defaultFakulteLink = "http://konservatuvar.gazi.edu.tr/posts?type=news";
                break;
            case "53":
                defaultFakulteLink = "http://konservatuvar.gazi.edu.tr/posts?type=news";
                break;
            case "54":
                defaultFakulteLink = "http://konservatuvar.gazi.edu.tr/posts?type=news";
                break;*/
        }
        switch (bolumNo) {
            case "01":
                defaultBolumLink = "http://ydyo.gazi.edu.tr/posts?type=news";
                break;
        }


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

                // ogrenci linkini çek

                // AKADEMÝK BÝRÝMLER BAÞ
                String ogrNo = ogrenciLoginEditText.getText().toString();
                switcher(ogrNo);

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
