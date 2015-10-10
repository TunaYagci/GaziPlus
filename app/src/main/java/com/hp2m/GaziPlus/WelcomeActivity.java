package com.hp2m.GaziPlus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;


public class WelcomeActivity extends AppCompatActivity {

    private RelativeLayout ogrenciLoginLayout, betaTesterLoginLayout, ogrenciLoginFinal;
    private TextView ogrenciLoginErrorText, ogrenciLoginAgreement;
    private EditText ogrenciLoginEditText;
    private ImageView logo;
    private boolean onGoBack = false;
    private SharedPreferences sP;
    private String defaultFakulteLink = "a", defaultBolumLink = "a";
    private String bolumAdi, fakulteAdi;
    private String bolumHint = "none";
    private int bolumImg, fakulteImg;
    private int min_item_to_load = -1;
    // test /// //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sP = getSharedPreferences("user", Context.MODE_PRIVATE);

        if(!(sP.getBoolean("isVersion2UpdateSuccessful",false))){

            SharedPreferences.Editor editor = sP.edit();
            editor.clear();

            if(sP.getBoolean("isLoginSuccessful", false)) {

                try {
                    new PlusMainReceiver().CancelAlarm(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                DuyuruDB db = null;
                try {
                    db = new DuyuruDB(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    db.clearForLogOut();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                editor.commit();
            }
        }
        else if (sP.getBoolean("isLoginSuccessful", false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ogrenciLoginAgreement = (TextView) findViewById(R.id.ogrenciLoginAgreement);
        ogrenciLoginLayout = (RelativeLayout) findViewById(R.id.ogrenciLoginLayout);
        betaTesterLoginLayout = (RelativeLayout) findViewById(R.id.betaTesterLayout);
        ogrenciLoginFinal = (RelativeLayout) findViewById(R.id.ogrenciLoginFinal);
        ogrenciLoginErrorText = (TextView) findViewById(R.id.ogrenciLoginErrorText);
        ogrenciLoginEditText = (EditText) findViewById(R.id.ogrenciLoginEditText);
        logo = (ImageView) findViewById(R.id.logo);
        YoYo.with(Techniques.DropOut)
                .duration(300)
                .playOn(logo);

        ogrenciLoginAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        SpannableString mySpannableString = new SpannableString(ogrenciLoginAgreement.getText());
        int index1 = ogrenciLoginAgreement.getText().toString().indexOf("þartlarý");
        mySpannableString.setSpan(new UnderlineSpan(), index1, index1 + 8, 0);
        //ogrenciLoginAgreement.setText(mySpannableString);
        ClickableSpan myClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(WelcomeActivity.this, Agreement.class);
                startActivity(i);
            }
        };
        mySpannableString.setSpan(myClickableSpan, index1, index1 + 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ogrenciLoginAgreement.setText(mySpannableString);

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
            YoYo.with(Techniques.BounceIn)
                    .duration(250)
                    .playOn(ogrenciLoginAgreement);

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
        YoYo.with(Techniques.SlideOutDown)
                .duration(250)
                .playOn(ogrenciLoginAgreement);

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
            case "01": // DÝÞ
                defaultFakulteLink = "none";
                defaultBolumLink = "http://dent.gazi.edu.tr/posts?type=news";
                bolumAdi = "Diþ Hekimliði Fakültesi";
                bolumHint = "nofab";
                bolumImg = R.drawable.gazi_dent;
                break;
            case "02": // ECZACILIK
                defaultFakulteLink = "none";
                defaultBolumLink = "http://pharmacy.gazi.edu.tr/posts?type=news";
                bolumAdi = "Eczacýlýk Fakültesi";
                bolumHint = "nofab";
                bolumImg = R.drawable.gazi_pharm;
                break;
            case "03":
                defaultFakulteLink = "http://edebiyat.gazi.edu.tr/posts?type=news";
                break;
            case "04":
                defaultFakulteLink = "http://esef.gazi.edu.tr/posts?type=news";
                break;
            case "05":
                //defaultFakulteLink = "http://fef.gazi.edu.tr/posts?type=news";
                defaultFakulteLink = "http://gef.gazi.edu.tr/posts?type=news";
                fakulteAdi = "EÐÝTÝM FAKÜLTESÝ";
                fakulteImg = R.drawable.gef_1_edited;
                switch (bolumNo) {
                    case "21":
                        defaultBolumLink = "http://gef-guzelsanatlar-muzik.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Müzik Eðitimi Anabilim Dalý";
                        bolumHint = "gef_muzik";
                        bolumImg = R.drawable.gef_muzik1;
                        min_item_to_load = 2;
                        break;
                    case "35":
                        defaultBolumLink = "http://gef-bote.gazi.edu.tr/posts?type=news";
                        bolumAdi = "BÖTE";
                        bolumHint = "gef_bote";
                        bolumImg = R.drawable.tf_bm1;
                        break;


                    // Tarih ----------------
                    case "44":
                        defaultBolumLink = "http://tarih.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Tarih";
                        bolumHint = "gef_tarih";
                        bolumImg = R.drawable.gef_tarih1;
                        min_item_to_load = 1;
                        break;
                    case "24":
                        defaultBolumLink = "http://tarih.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Tarih";
                        bolumHint = "gef_tarih";
                        bolumImg = R.drawable.gef_tarih1;
                        min_item_to_load = 1;
                        break;
                    // Tarih ----------------


                    // Coðrafya ----------------
                    case "45":
                        defaultBolumLink = "http://gefcografya.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Coðrafya";
                        bolumHint = "gef_cog";
                        bolumImg = R.drawable.gef_cografya1;
                        break;
                    case "25":
                        defaultBolumLink = "http://gefcografya.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Coðrafya";
                        bolumHint = "gef_cog";
                        bolumImg = R.drawable.gef_cografya1;
                        break;
                    // Coðrafya ----------------


                    // Felsefe ----------------
                    case "46":
                        defaultBolumLink = "http://geffelsefe.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Felsefe";
                        bolumHint = "gef_felsefe";
                        bolumImg = R.drawable.gef_felsefe1;
                        break;
                    case "26":
                        defaultBolumLink = "http://geffelsefe.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Felsefe";
                        bolumHint = "gef_felsefe";
                        bolumImg = R.drawable.gef_felsefe1;
                        break;
                    // Felsefe ----------------

                    // Türk Dili ----------------
                    case "47":
                        defaultBolumLink = "http://geftde.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Türk Dili";
                        bolumHint = "gef_turk_dili";
                        bolumImg = R.drawable.gef_td1;
                        min_item_to_load = 2;
                        break;
                    case "27":
                        defaultBolumLink = "http://geftde.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Türk Dili";
                        bolumHint = "gef_turk_dili";
                        bolumImg = R.drawable.gef_muzik1;
                        min_item_to_load = 2;
                        break;
                    // Türk Dili ----------------

                    case "65":
                        defaultBolumLink = "http://gef-yabancidiller-ingilizdili.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Ýngilizce";
                        bolumHint = "gef_ing";
                        bolumImg = R.drawable.gef_ing1;
                        break;

                    case "66":
                        defaultBolumLink = "http://gef-yabancidiller-almandili.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Almanca";
                        bolumHint = "gef_alm";
                        bolumImg = R.drawable.gef_alm1;
                        min_item_to_load = 1;
                        break;

                    case "67":
                        defaultBolumLink = "http://gef-yabancidiller-fransizdili.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Fransýzca";
                        bolumHint = "gef_french";
                        bolumImg = R.drawable.gef_fransizca1;
                        break;

                    case "68":
                        defaultBolumLink = "http://gef-yabancidiller-arapdili.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Arapça";
                        bolumHint = "gef_arabic";
                        bolumImg = R.drawable.gef_arapca1;
                        break;

                    case "75":
                        defaultBolumLink = "http://gef-egitimbilimleri-pdr.gazi.edu.tr/posts?type=news";
                        bolumAdi = "PDR";
                        bolumHint = "gef_pdr";
                        bolumImg = R.drawable.gef_pdr1;
                        break;

                    case "94":
                        defaultBolumLink = "http://gef-ozelegitim-zihinselengelliler.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Zihinsel Engelliler Eðitimi";
                        bolumHint = "gef_zihinsel";
                        bolumImg = R.drawable.gef_zihinsel_engelliler1;
                        break;

                    case "95":
                        defaultBolumLink = "http://gef-ozelegitim-gormeengelliler.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Görme Engelliler Eðitimi";
                        bolumHint = "gef_gorme";
                        bolumImg = R.drawable.gef_gorme_engelliler1;
                        break;

                    case "14":
                        defaultBolumLink = "http://gef-ilkogretim-matematikegitimi.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Ýlköðretim Matematik Eðitimi";
                        bolumHint = "gef_ilkogretim_mat";
                        bolumImg = R.drawable.gef_ilk_mat1;
                        break;

                    case "22":
                        defaultBolumLink = "http://gef-guzelsanatlar-resimis.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Resim-Ýþ Eðitimi";
                        bolumHint = "gef_resim_is";
                        bolumImg = R.drawable.gef_resim_is1;
                        break;

                    case "15":
                        defaultBolumLink = "http://gef-ilkogretim-sosyalbilgiler.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Sosyal Bilgiler";
                        bolumHint = "gef_sosyal_bilgiler";
                        bolumImg = R.drawable.gef_sosyal_bilgiler;
                        break;


                    // Kimya --------------------
                    case "30":
                        defaultBolumLink = "http://gef-ortaogretim-fenmatematik-kimya.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Kimya Öðretmenliði";
                        bolumHint = "gef_kimya";
                        bolumImg = R.drawable.gef_kimya1;
                        min_item_to_load = 2;
                        break;
                    case "58":
                        defaultBolumLink = "http://gef-ortaogretim-fenmatematik-kimya.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Kimya Öðretmenliði";
                        bolumHint = "gef_kimya";
                        bolumImg = R.drawable.gef_kimya1;
                        min_item_to_load = 2;
                        break;
                    // Kimya --------------------

                    case "13":
                        defaultBolumLink = "http://gef-fenbilgisi.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Fen Bilgisi Öðretmenliði";
                        bolumHint = "gef_fen_bilgisi";
                        bolumImg = R.drawable.gef_fen_bilgisi1;
                        break;

                    case "31":
                        defaultBolumLink = "http://gef-ortaogretim-fenmatematik-biyoloji.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Biyoloji Öðretmenliði";
                        bolumHint = "gef_fen_bilgisi";
                        bolumImg = R.drawable.gef_biyoloji1;
                        min_item_to_load = 1;
                        break;


                    case "12":
                        defaultBolumLink = "http://gef-ilkogretim-sinifogretmenligi.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Sýnýf Öðretmenliði";
                        bolumHint = "gef_sinif_ogr";
                        bolumImg = R.drawable.gef_sinif_ogrt1;
                        break;

                    case "57":
                        defaultBolumLink = "http://gef-ortaogretim-fenmatematik-fizik.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Fizik Öðretmenliði";
                        bolumHint = "gef_fizik";
                        bolumImg = R.drawable.gef_fizik1;
                        min_item_to_load = 1;
                        break;

                    // BUNUN KODU YOK !!
                    /*case "57":
                        defaultBolumLink = "http://gef-ortaogretim-fenmatematik-fizik.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Fizik";
                        bolumHint = "gef_fizik";
                        bolumImg = R.drawable.okuloncesi_1_edited;
                        min_item_to_load = 1;
                        break;*/

                    // Okulöncesi
                    case "11":
                        defaultBolumLink = "http://gef-ilkogretim-okuloncesi.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Okulöncesi Eðitim";
                        bolumHint = "okuloncesi_egitim";
                        bolumImg = R.drawable.okuloncesi_1_edited;
                        break;
                    case "20":
                        defaultBolumLink = "http://gef-ilkogretim-okuloncesi.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Okulöncesi Eðitim";
                        bolumHint = "okuloncesi_egitim";
                        bolumImg = R.drawable.okuloncesi_1_edited;
                        break;
                    // ----------


                    default:
                        break;
                }
                break;
            case "06":
                defaultFakulteLink = "none"; // gazi hukuk
                defaultBolumLink = "http://hukuk.gazi.edu.tr/posts?type=news";
                bolumAdi = "Hukuk Fakültesi";
                bolumHint = "nofab";
                bolumImg = R.drawable.gazi_hukuk1;
                break;
            case "07":
                defaultFakulteLink = "http://gsf.gazi.edu.tr/posts?type=news";
                break;
            case "08":
                defaultFakulteLink = "none"; // gazi iletiþim
                defaultBolumLink = "http://ilet.gazi.edu.tr/posts?type=news";
                bolumAdi = "ÝLETÝÞÝM FAKÜLTESÝ";
                bolumHint = "nofab";
                bolumImg = R.drawable.gazi_iletisim_2;
                break;
            case "09":
                defaultFakulteLink = "http://iibf.gazi.edu.tr/posts?type=news";
                break;
            case "10": // mimarlýk
                defaultFakulteLink = "http://mim.gazi.edu.tr/posts?type=news";
                fakulteAdi = "MÝMARLIK FAKÜLTESÝ";
                fakulteImg = R.drawable.mf_fakulte3;
                switch (bolumNo) {
                    case "60": // mimarlýk
                        defaultBolumLink = "http://mim-mim.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Mimarlýk Bölümü";
                        bolumHint = "mimar_sinan";
                        bolumImg = R.drawable.gazi_mimarlik1;
                        break;
                    case "70": //þbp
                        defaultBolumLink = "http://mim-sbp.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Þehir ve Bölge Planlama";
                        bolumHint = "mimar_sbp";
                        bolumImg = R.drawable.gazi_sbp1;
                        break;
                }
                break;
            case "11":
                // web link http://mf.gazi.edu.tr/posts?type=news
                // home server ip: http://192.168.1.8/gazi/mfgazi.htm
                defaultFakulteLink = "http://mf.gazi.edu.tr/posts?type=news";
                fakulteAdi = "MÜHENDÝSLÝK FAKÜLTESÝ";
                fakulteImg = R.drawable.mf_fakulte3;
                switch (bolumNo.substring(0, 1)) {
                    case "8":
                        // web link http://mf-bm.gazi.edu.tr/posts?type=news
                        // home server ip: http://192.168.1.8/gazi/index.htm
                        defaultBolumLink = "http://mf-bm.gazi.edu.tr/posts?type=news";
                        bolumAdi = "CENGAZÝ";
                        bolumHint = "cengazi";
                        bolumImg = R.drawable.lowres2_2;
                        break;
                    case "5":
                        defaultBolumLink = "http://mf-mm.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Makina Mühendisliði";
                        bolumHint = "muh_mak";
                        bolumImg = R.drawable.muh_makina3;
                        break;
                    case "4":
                        defaultBolumLink = "http://mf-km.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Kimya Mühendisliði";
                        bolumHint = "muh_kim";
                        bolumImg = R.drawable.muh_kimya3;
                        break;
                    case "3":
                        defaultBolumLink = "http://mf-im.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Ýnþaat Mühendisliði";
                        bolumHint = "muh_im";
                        bolumImg = R.drawable.gazi_im_2;
                        break;
                    case "2":
                        defaultBolumLink = "http://mf-em.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Endüstri Mühendisliði";
                        bolumHint = "muh_ent";
                        bolumImg = R.drawable.gazi_ent_1;
                        break;
                    case "1":
                        defaultBolumLink = "http://mf-eem.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Elektrik-Elektronik Mühendisliði";
                        bolumHint = "muh_eem";
                        bolumImg = R.drawable.muh_eem_1;
                        break;

                    default:
                        break;
                }
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
                fakulteAdi = "TEKNOLOJÝ FAKÜLTESÝ";
                fakulteImg = R.drawable.tf_fakulte_1;
                switch (bolumNo) {
                    case "02":
                        defaultBolumLink = "http://tf-eem.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Elektrik-Elektronik Mühendisliði";
                        bolumHint = "tf-eem";
                        bolumImg = R.drawable.tf_eem;
                        break;
                    case "04":
                        defaultBolumLink = "http://tf-esm.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Enerji Sistemleri Mühendisliði";
                        bolumHint = "tf-esm";
                        bolumImg = R.drawable.tf_esm1;
                        break;
                    case "06":
                        defaultBolumLink = "http://tf-imalat.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Ýmalat Mühendisliði";
                        bolumHint = "tf-imalat";
                        bolumImg = R.drawable.tf_imalat_1;
                        break;
                    case "08":
                        defaultBolumLink = "http://tf-insaat.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Ýnþaat Mühendisliði";
                        bolumHint = "tf-ins";
                        bolumImg = R.drawable.gazi_im_2;
                        break;
                    case "10":
                        defaultBolumLink = "http://tf-metalurji.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Metalurji ve Malzeme Mühendisliði";
                        bolumHint = "tf-metmal";
                        bolumImg = R.drawable.tf_metmal;
                        break;
                    case "12":
                        defaultBolumLink = "http://tf-metalurji.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Otomativ Mühendisliði";
                        bolumHint = "tf-otomativ";
                        bolumImg = R.drawable.tf_otomativ;
                        break;
                    case "14":
                        defaultBolumLink = "http://tf-aiem.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Aðaçiþleri Endüstri Mühendisliði";
                        bolumHint = "tf-aiem";
                        bolumImg = R.drawable.tf_aiem1;
                        break;
                    case "16":
                        defaultBolumLink = "http://tf-bm.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Bilgisayar Mühendisliði";
                        bolumHint = "tf-bm";
                        bolumImg = R.drawable.tf_bm1;
                        break;
                    case "18":
                        defaultBolumLink = "http://tf-etm.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Endüstriyel Tasarým Mühendisliði";
                        bolumHint = "tf-etm";
                        bolumImg = R.drawable.tf_etm1;
                        break;
                    default:
                        break;
                }
                break;
            case "19":
                defaultFakulteLink = "none"; // gazi týp
                defaultBolumLink = "http://med.gazi.edu.tr/posts?type=news";
                bolumAdi = "Týp Fakültesi";
                bolumHint = "nofab";
                bolumImg = R.drawable.gazi_med;
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
                defaultFakulteLink = "none"; // gazi adalet
                defaultBolumLink = "http://adalet.gazi.edu.tr/posts?type=news";
                bolumAdi = "Adalet Meslek Yüksekokulu";
                bolumHint = "nofab";
                bolumImg = R.drawable.gazi_adalet1;
                break;
            case "32":
                defaultFakulteLink = "http://ydyo.gazi.edu.tr/posts?type=news";
                break;
            case "33": // EDEBÝYAT
                defaultFakulteLink = "http://edebiyat.gazi.edu.tr/";
                fakulteAdi = "Edebiyat Fakültesi";
                fakulteImg = R.drawable.gazi_edebiyat;
                switch (bolumNo) {
                    case "70":
                        defaultBolumLink = "http://tarih.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Tarih";
                        bolumHint = "edb-tarih";
                        bolumImg = R.drawable.edeb_tarih;
                        break;
                    case "75":
                        defaultBolumLink = "http://tarih.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Tarih";
                        bolumHint = "edb-tarih";
                        bolumImg = R.drawable.edeb_tarih;
                        break;
                    case "90":
                        defaultBolumLink = "http://felsefe.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Felsefe";
                        bolumHint = "edb-felsefe";
                        bolumImg = R.drawable.gazi_felsefe;
                        break;
                    case "93":
                        defaultBolumLink = "http://sosyoloji.gazi.edu.tr/posts?type=news";
                        bolumAdi = "Sosyoloji";
                        bolumHint = "edb-sosyoloji";
                        bolumImg = R.drawable.edeb_sosyoloji;
                        break;

                    default:
                        break;
                }
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

                bolumAdi = "CENGAZÝ";
                bolumImg = R.drawable.lowres2_2;
                bolumHint = "cengazi";
                fakulteAdi = "MÜHENDÝSLÝK FAKÜLTESÝ";
                fakulteImg = R.drawable.mf_fakulte3;
                defaultFakulteLink = "http://mf.gazi.edu.tr/posts?type=news";
                defaultBolumLink = "http://mf-bm.gazi.edu.tr/posts?type=news";

                setupNotificationService();
                editor.putString("bolumHint", bolumHint);
                editor.putString("bolumAdi", bolumAdi);
                editor.putInt("bolumImg", bolumImg);
                editor.putString("fakulteAdi", fakulteAdi);
                editor.putInt("fakulteImg", fakulteImg);
                editor.putString("defaultFakulteLink", defaultFakulteLink);
                editor.putString("defaultBolumLink", defaultBolumLink);
                editor.putString("duyuruLink", defaultBolumLink);
                editor.putString("generalMode", "bolum");
                editor.putBoolean("isLoginSuccessful", true);
                //editor.commit();

                //extra
                // sP = getSharedPreferences("user", Context.MODE_PRIVATE);
                //editor = sP.edit();
                //editor.putString("checkbox", "checked");
                //editor.putString("ogrNo", "141180068");
                //editor.putString("parola", "tuna124");

                PackageInfo pInfo = null;
                int verCode = 1;
                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                verCode = pInfo.versionCode;

                editor.putBoolean("isVersion2UpdateSuccessful", true);
                editor.putInt("versionCode", verCode);



                editor.commit();

                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 250);
    }


    public void ogrenciLoginFinal(View v) {
        if (ogrenciLoginEditText.getText().toString().length() < 9) {
            ogrenciLoginErrorText.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.BounceIn)
                    .duration(250)
                    .playOn(ogrenciLoginErrorText);
            YoYo.with(Techniques.Wobble)
                    .duration(250)
                    .playOn(ogrenciLoginEditText);
        } else {
            ogrenciLoginErrorText.setVisibility(View.GONE);
            final String ogrNo = ogrenciLoginEditText.getText().toString();
            switcher(ogrNo);

            if (bolumHint.equals("none")) {
                Toast.makeText(this, "Bölümün henüz desteklenmiyor, lütfen öðrenci numaraný bize yolla", Toast.LENGTH_LONG).show();
                return;
            }


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

                    setupNotificationService();
                    SharedPreferences.Editor editor = sP.edit();
                    editor.putString("bolumHint", bolumHint);
                    editor.putString("bolumAdi", bolumAdi);
                    editor.putInt("bolumImg", bolumImg);
                    editor.putString("fakulteAdi", fakulteAdi);
                    editor.putInt("fakulteImg", fakulteImg);
                    editor.putString("defaultFakulteLink", defaultFakulteLink);
                    editor.putString("defaultBolumLink", defaultBolumLink);
                    editor.putString("duyuruLink", defaultBolumLink);
                    editor.putString("generalMode", "bolum");
                    editor.putBoolean("isLoginSuccessful", true);
                    // new
                    editor.putString("ogrNo", ogrNo);
                    editor.putString("checkbox", "checked");
                    editor.putString("defaultOgrNo", ogrNo);
                    // ---

                    if (min_item_to_load != -1) {
                        editor.putInt(DataHolder.MIN_ITEM_TO_LOAD, min_item_to_load);
                    }


                    PackageInfo pInfo = null;
                    int verCode = 1;
                    try {
                        pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    verCode = pInfo.versionCode;

                    editor.putBoolean("isVersion2UpdateSuccessful", true);
                    editor.putInt("versionCode", verCode);

                    editor.commit();
                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 250);
        }
    }

    public void setupNotificationService() {
        // okay so here, we will start duyuru notifications first, fakulte too maybe?
        // later on we will add not notifications when user wrote his pass

        PlusMainReceiver receiver = new PlusMainReceiver();
        receiver.SetAlarm(this);
        SharedPreferences.Editor editor = sP.edit();
        editor.putBoolean("isNotificationServiceOnline", true);
        editor.putBoolean("isDuyuruServiceOnline", true);
        editor.putBoolean("isBolumNotificationsAllowed", true);
        if (!bolumHint.equals("nofab")) {
            editor.putBoolean("isFakulteNotificationsAllowed", true);
        } else {
            editor.putBoolean("isFakulteNotificationsAllowed", false);
        }
        //editor.putBoolean("isNotNotificationsAllowed", false);
        editor.apply();

    }

}
