package com.hp2m.newsupportlibrary22;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Scanner;

import de.greenrobot.event.EventBus;


public class DuyuruDetailedActivity extends AppCompatActivity {

    final EventBus bus = EventBus.getDefault();
    TextView header, tarih, content;
    RelativeLayout layout;
    String originalLink;
    Activity activity;
    Button goToOriginalSourceButton;
    ProgressBar progressBar;
    private int POSITION;
    private DuyuruDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duyuru_detailed);

        activity = this;
        bus.register(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Duyurular");
        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            POSITION = extras.getInt("pos");
        }
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.loadingBar);
        goToOriginalSourceButton = (Button) findViewById(R.id.showOriginal);
        layout = (RelativeLayout) findViewById(R.id.weirdLayout);
        header = (TextView) findViewById(R.id.header);
        tarih = (TextView) findViewById(R.id.tarih);
        db = new DuyuruDB(this);
        header.setText(db.fetchMeMyDuyuru(POSITION).get(0));
        tarih.setText("Yayýnlanma tarihi: " + db.fetchMeMyDuyuru(POSITION).get(2));

        if (db.fetchMeMyDuyuru(POSITION).get(4).length() < 2) {
            Log.i("tuna", db.fetchMeMyDuyuru(POSITION).get(4));
            goToOriginalSourceButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            DuyuruExceptionDB db2 = new DuyuruExceptionDB(getApplicationContext());
            boolean isThisDuyuruOnExceptions = false;
            // check if its on exceptioner list
            for (int i = 0; i < db.getDuyuruSayisi(); i++) {
                for (int i2 = 0; i2 < db2.getExceptionedDuyuruSayisi(); i2++) {
                    if (db.fetchMeMyDuyuru(i).get(0) == db2.fetchFailedDuyuru(i2).get(0)) {
                        isThisDuyuruOnExceptions = true;
                    }
                }
            }
            if (isThisDuyuruOnExceptions) {
                onEvent(new StatusForDetailedActivity("exception"));
            }
        } else {
            Log.i("tuna", "" + db.fetchMeMyDuyuru(POSITION).get(1).length());
            startLoad();
        }

    }

    public void goToOriginalSource(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(originalLink));
        startActivity(browserIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    public void startLoad() {
        //goToOriginalSourceButton = (Button) findViewById(R.id.showOriginal);
        goToOriginalSourceButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);


        originalLink = db.fetchMeMyDuyuru(POSITION).get(4);
        final ArrayList<String> links = new ArrayList<>();
        ArrayList<String> linkWords = new ArrayList<>();
        String a2link = db.fetchMeMyDuyuru(POSITION).get(3);
        if (!a2link.equals("none")) {
            Scanner scanner = new Scanner(a2link);
            while (scanner.hasNextLine()) {
                String a = scanner.nextLine(); // first saved is link
                scanner.hasNext();
                String b = scanner.nextLine(); // second is the word
                links.add(a);
                linkWords.add(b);
            }
            scanner.close();
        }

        final String contentText = db.fetchMeMyDuyuru(POSITION).get(1);
        final SpannableString ss = new SpannableString(contentText);


        // POSSIBLE BUGS
            /*
            a) final for i3 and i2 may cause wrong links
            for pages which has more than 1 link

            b) do make spannable string bigger

             */
        for (int i = 0; i < linkWords.size(); i++) {
            final int start = contentText.indexOf(linkWords.get(i));
            final int end = start + linkWords.get(i).length();
            if (links.get(i).contains("download")) {
                final DuyuruDB db2 = db;

                final int i3 = i;
                ClickableSpan clickableSpan = new ClickableSpan() {

                    @Override
                    public void onClick(View textView) {
                        //Log.i("tuna", "on Click, i3 is " + i3);
                        //Log.i("tuna", links.get(i3));
                        //Log.i("tuna", db2.fetchMeMyDuyuru(POSITION).get(1));
                        new DuyuruDetailedPageTask(activity, links.get(i3))
                                .execute();

                        ss.setSpan(new ForegroundColorSpan(R.color.cardview_light_background), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ss.setSpan(new RelativeSizeSpan(0.8f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        content.setText(ss);
                    }
                };
                ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                final int i2 = i;
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                        Log.i("tuna", "on Click, i2 is " + i2);
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(links.get(i2)));
                        startActivity(browserIntent);
                    }
                };
                ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            ss.setSpan(new RelativeSizeSpan(1.4f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


        content = (TextView) findViewById(R.id.content);
        content.setText(ss);
        content.setMovementMethod(LinkMovementMethod.getInstance());


    }

    public void onEvent(StatusForDetailedActivity event) {
        Log.i("tuna", "onEvent StatusForDetailedActivity");
        try {
            int indexOfException = 0;
            if (event.message == "goodToGo") {
                startLoad();

            } else { // means an exception has occurred in loading
                DuyuruExceptionDB db2 = new DuyuruExceptionDB(this);
                for (int i = 0; i < db.getDuyuruSayisi(); i++) {
                    for (int i2 = 0; i2 < db2.getExceptionedDuyuruSayisi(); i2++) {
                        if (db.fetchMeMyDuyuru(i).get(0) == db2.fetchFailedDuyuru(i2).get(0)) {
                            indexOfException = i2;
                        }
                    }
                }
                Log.i("tuna", "found exception, it is= " + db2.fetchFailedDuyuru(indexOfException).get(0));
                progressBar.setVisibility(View.GONE);
                final int indexOfException2 = indexOfException;
                Snackbar.make(layout, "Sunucuya baðlanýlamýyor", Snackbar.LENGTH_LONG)
                        .setAction("Tekrar Dene", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                HandleIOExceptions io = new HandleIOExceptions(getApplicationContext());
                                io.main(indexOfException2);
                                progressBar.setVisibility(View.VISIBLE);
                            }
                        })
                        .show();

            }
        } catch (Exception e) {
            Log.i("tuna", "unnecessary exception in DetailedActivity " + e.toString());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_duyuru_detailed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }
}
