package com.hp2m.newsupportlibrary22;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.Scanner;

import de.greenrobot.event.EventBus;


public class DuyuruDetailedActivity extends AppCompatActivity {

    final EventBus bus = EventBus.getDefault();
    ImageView image1, image2, image3;
    TextView header, tarih, content;
    RelativeLayout layout;
    String originalLink;
    Activity activity;
    Button goToOriginalSourceButton;
    ProgressBar progressBar;
    private int circleCount = 0;
    private int POSITION;
    private DuyuruDB db;
    private String generalMode;
    private ImageButton reload;
    private TextView reloadText;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (db.fetchMeMyDuyuru(POSITION, generalMode).get(4).length() < 2) {
                circleCount++;
                if (circleCount > 20) {
                    Toast.makeText(getApplicationContext(), "Sunucu yanýt vermiyor", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                handler.postDelayed(runnable, 300);
            } else {
                circleCount = 0;
                startLoad();
            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duyuru_detailed);

        reload = (ImageButton) findViewById(R.id.reload);
        reloadText = (TextView) findViewById(R.id.reloadText);
        SharedPreferences sP = getSharedPreferences("user", Context.MODE_PRIVATE);
        generalMode = sP.getString("generalMode", "");
        activity = this;
        bus.register(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Duyurular");
        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();
        db = new DuyuruDB(this);



        if (extras != null) {
            String title = extras.getString("title");
            POSITION = db.fetchMeDuyuruPosition(title, generalMode);
        }
        getSupportActionBar().setHomeButtonEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.loadingBar);
        goToOriginalSourceButton = (Button) findViewById(R.id.showOriginal);
        layout = (RelativeLayout) findViewById(R.id.weirdLayout);
        header = (TextView) findViewById(R.id.header);
        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        image3 = (ImageView) findViewById(R.id.image3);
        tarih = (TextView) findViewById(R.id.tarih);

        header.setText(db.fetchMeMyDuyuru(POSITION, generalMode).get(0));
        tarih.setText("Yayýnlanma tarihi: " + db.fetchMeMyDuyuru(POSITION, generalMode).get(2));

        Log.i("tuna", "db.fetchMeMyDuyuru(POSITION).get(4)= " + db.fetchMeMyDuyuru(POSITION, generalMode).get(4));
        if (db.fetchMeMyDuyuru(POSITION, generalMode).get(4).length() < 2) {
            Log.i("tuna", db.fetchMeMyDuyuru(POSITION, generalMode).get(4));
            goToOriginalSourceButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            /*DuyuruExceptionDB db2 = new DuyuruExceptionDB(getApplicationContext());
            boolean isThisDuyuruOnExceptions = false;
            // check if its on exceptioner list
            for (int i = 0; i < db.getDuyuruSayisi(generalMode); i++) {
                for (int i2 = 0; i2 < db2.getExceptionedDuyuruSayisi(generalMode); i2++) {
                    if (db.fetchMeMyDuyuru(i, generalMode).get(0).equals(db2.fetchFailedDuyuru(i2, generalMode).get(0))) {
                        isThisDuyuruOnExceptions = true;
                    }
                }
            }
            if (isThisDuyuruOnExceptions) {
                Log.i("tuna", "This is on Duyuru Exceptions");
                //onEvent(new StatusForDetailedActivity("exception"));
            } else { }*/
                handler.postDelayed(runnable, 300);
        } else {
            Log.i("tuna", "gonna load duyuru detailed");
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
        Log.i("tuna", "onStartLoad");
        //goToOriginalSourceButton = (Button) findViewById(R.id.showOriginal);
        goToOriginalSourceButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);


        //Log.i("tuna", "lets download images");
        String cx = db.fetchMeMyDuyuru(POSITION, generalMode).get(6);
        final ArrayList<String> imageLinks = new ArrayList<>();
        if (!cx.isEmpty()) {
            Scanner scanner = new Scanner(cx);
            while (scanner.hasNextLine()) {
                String a = scanner.nextLine();
                imageLinks.add(a);
            }
            scanner.close();
            //Log.i("tuna", "there is actually some image, see= " + cx + " or see this= " + imageLinks.get(0));


            //  ImageLoader imageLoader = ImageLoader.getInstance();
            //  imageLoader.clearMemoryCache();

            for (int i = 0; i < imageLinks.size(); i++) {
                if (i == 0) {
                    image1.setVisibility(View.VISIBLE);
                    ImageLoader.getInstance().displayImage(imageLinks.get(i), image1, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            final Uri uri = Uri.parse(imageUri);
                            image1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setDataAndType(uri, "image/jpeg");
                                    startActivity(i);
                                }
                            });

                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
                } else if (i == 1) {
                    image2.setVisibility(View.VISIBLE);
                    ImageLoader.getInstance().displayImage(imageLinks.get(i), image2);
                } else if (i == 2) {
                    image3.setVisibility(View.VISIBLE);
                    ImageLoader.getInstance().displayImage(imageLinks.get(i), image3);
                } else
                    break;
            }
        }
        originalLink = db.fetchMeMyDuyuru(POSITION, generalMode).get(4);
        final ArrayList<String> links = new ArrayList<>();
        ArrayList<String> linkWords = new ArrayList<>();
        String a2link = db.fetchMeMyDuyuru(POSITION, generalMode).get(3);
        //Log.i("tuna", "a2link is = " + a2link);
        if (!a2link.equals("none")) {
            Scanner scanner = new Scanner(a2link);
            while (scanner.hasNextLine()) {
                String a = scanner.nextLine(); // first saved is link
                scanner.hasNext();
                String b = scanner.nextLine(); // second is the word
                //Log.i("tuna", "first a and first b= " + a + " " + b);
                links.add(a);
                linkWords.add(b);
            }
            scanner.close();
        }

        final String contentText = db.fetchMeMyDuyuru(POSITION, generalMode).get(1);
        //Log.i("tuna", "contentText is " + contentText);
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
            //Log.i("tuna", "linkWord is =" + linkWords.get(i) + "and start is " + start + " and end is " + end);
            if (links.get(i).contains("download")) {
                final int i3 = i;
                ClickableSpan clickableSpan = new ClickableSpan() {

                    @Override
                    public void onClick(View textView) {
                        //Log.i("tuna", "on Click, i3 is " + i3);
                        //Log.i("tuna", links.get(i3));
                        //Log.i("tuna", db2.fetchMeMyDuyuru(POSITION).get(1));
                        new DuyuruDetailedPageTask(activity, links.get(i3))
                                .execute();

                        ss.setSpan(new RelativeSizeSpan(0.8f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.cardview_light_background)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        content.setText(ss);
                    } // once link is clicked, it'll be normal
                };
                try {
                    ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } catch (Exception e) {
                    Log.i("tuna", e.toString());
                }
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
            try {
                ss.setSpan(new RelativeSizeSpan(1.4f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {
                Log.i("tuna", e.toString());
            }
        }


        content = (TextView) findViewById(R.id.content);
        content.setText(ss);
        content.setMovementMethod(LinkMovementMethod.getInstance());


    }

    public void onEvent(StatusForDetailedActivity event) {
        Log.i("tuna", "onEvent StatusForDetailedActivity");
        /*try {
            int indexOfException = 0;
            if (event.message.equals("goodToGo")) {
                startLoad();

            } else { // means an exception has occurred in loading
                DuyuruExceptionDB db2 = new DuyuruExceptionDB(this);
                for (int i = 0; i < db.getDuyuruSayisi(generalMode); i++) {
                    for (int i2 = 0; i2 < db2.getExceptionedDuyuruSayisi(generalMode); i2++) {
                        if (db.fetchMeMyDuyuru(i, generalMode).get(0).equals(db2.fetchFailedDuyuru(i2, generalMode).get(0))) {
                            indexOfException = i2;
                        }
                    }
                }
                Log.i("tuna", "found exception, it is= " + db2.fetchFailedDuyuru(indexOfException, generalMode).get(0));
                progressBar.setVisibility(View.GONE);
                final int indexOfException2 = indexOfException;
                reload.setVisibility(View.VISIBLE);
                reloadText.setVisibility(View.VISIBLE);
                reload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reload.setVisibility(View.INVISIBLE);
                        reloadText.setVisibility(View.INVISIBLE);
                        HandleIOExceptions io = new HandleIOExceptions(getApplicationContext());
                        io.main(indexOfException2);
                    }
                });
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
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }


}
