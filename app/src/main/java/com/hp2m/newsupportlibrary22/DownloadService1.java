package com.hp2m.newsupportlibrary22;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import de.greenrobot.event.EventBus;

public class DownloadService1 extends IntentService {


    final EventBus bus = EventBus.getDefault();

    public DownloadService1() {
        super("hello");
    }

    public DownloadService1(String name) {
        super(name);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("tuna", "service1 started");

        SharedPreferences sP = this.getBaseContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String generalMode = sP.getString("generalMode", "");
        String header = intent.getStringExtra("header");
        //int iStart=intent.getIntExtra("iStart", 0);
        //int iSize=intent.getIntExtra("iSize", 0);
        String link = intent.getStringExtra("link");
        boolean exceptioner = intent.getBooleanExtra("exceptioner", false);

        String icerik = "", icerikLink = "";

        Log.i("tuna", "inner side parsing 1");
        //headerList.add(duyuruHeaderElements.get(i).text().substring(17));
        //String a = "";
        //String b = "";
        //String link = duyuruHeaderElements.get(i).attr("abs:href");
        Document insideDocs = null;
        try {
            insideDocs = Jsoup.connect(link).timeout(0).get();

            StringBuilder builder = new StringBuilder();

            Elements duyuruH3Elements = insideDocs.select("div.post-content h3");
            //builder = new StringBuilder();
            for (int i2 = 0; i2 < duyuruH3Elements.size(); i2++) {
                if (!duyuruH3Elements.get(i2).text().isEmpty()) {
                    //a += duyuruElements.get(i2).text();
                    //a += "\n\n";
                    builder.append(duyuruH3Elements.get(i2).text());
                    builder.append("\n\n");
                }
            }

            Elements duyuruH4Elements = insideDocs.select("div.post-content h4");
            //builder = new StringBuilder();
            for (int i2 = 0; i2 < duyuruH4Elements.size(); i2++) {
                if (!duyuruH4Elements.get(i2).text().isEmpty()) {
                    //a += duyuruElements.get(i2).text();
                    //a += "\n\n";
                    builder.append(duyuruH4Elements.get(i2).text());
                    builder.append("\n\n");
                }
            }

            Elements duyuruElements = insideDocs.select("div.post-content p");
            for (int i2 = 0; i2 < duyuruElements.size(); i2++) {
                if (!duyuruElements.get(i2).text().isEmpty()) {
                    //a += duyuruElements.get(i2).text();
                    //a += "\n\n";
                    builder.append(duyuruElements.get(i2).text());
                    builder.append("\n\n");
                }
            }
            /*StringBuilder builder2 = new StringBuilder();
            if (!builder.toString().matches(".*[a-z].*")){
                // Do something
                Elements duyuruElements2 = insideDocs.select("div.post-content");
                builder2 = new StringBuilder();
                for (int i2 = 0; i2 < duyuruElements2.size(); i2++) {
                    Log.i("tuna", "inner side parsing 2");
                    if (!duyuruElements2.get(i2).text().isEmpty()) {
                        //a += duyuruElements.get(i2).text();
                        //a += "\n\n";
                        builder2.append(duyuruElements2.get(i2).text());
                        builder2.append("\n\n");
                    }
                }
            } else {
                Log.i("gazient", builder.toString());
            }*/
                Elements duyuruInsafsizElement = insideDocs.select("div.post-content div");
                for (int i2 = 0; i2 < duyuruInsafsizElement.size(); i2++) {
                    if (!duyuruInsafsizElement.get(i2).text().isEmpty()) {
                        //a += duyuruElements.get(i2).text();
                        //a += "\n\n";
                        builder.append(duyuruInsafsizElement.get(i2).text());
                        builder.append("\n\n");
                    }
                }

            Elements duyuruLiElement = insideDocs.select("div.post-content li");
            for (int i2 = 0; i2 < duyuruInsafsizElement.size(); i2++) {
                if (!duyuruInsafsizElement.get(i2).text().isEmpty()) {
                    //a += duyuruElements.get(i2).text();
                    //a += "\n\n";
                    builder.append(duyuruInsafsizElement.get(i2).text());
                    builder.append("\n\n");
                }
            }
            icerik = builder.toString();
            builder = new StringBuilder();
            //Log.i("tuna", "gonna parse links");
            Elements duyuruLinkElements = insideDocs.select("div.post-content a[href]");
            // Log.i("tuna", "link parsing ended");
            for (int i3 = 0; i3 < duyuruLinkElements.size(); i3++) {
                //Log.i("tuna", "inner side parsing 3");
                if (i3 != 0)
                    builder.append("\n");
                //b += duyuruLinkElements.get(i3).attr("abs:href");
                //b += "\n";
                //b += duyuruLinkElements.get(i3).text();
                builder.append(duyuruLinkElements.get(i3).attr("abs:href"));
                builder.append("\n");
                builder.append(duyuruLinkElements.get(i3).text());

            }
            if (duyuruLinkElements.size() == 0) {
                //b = "none";
                builder.append("none");
            }
            //icerikLink.add(b);
            icerikLink = builder.toString();


            //Log.i("tuna", "gonna look for image links");
            builder = new StringBuilder();
            Elements duyuruImageLinks = insideDocs.select("div.post-content img[src]");
            for (int i = 0; i < duyuruImageLinks.size(); i++) {
                builder.append(duyuruImageLinks.get(i).attr("abs:src"));
                Log.i("tuna", "appended image is " + duyuruImageLinks.get(i).attr("abs:src"));
                builder.append("\n");
            }
            String imageLinks = builder.toString();

            DuyuruDB db = new DuyuruDB(getBaseContext());
            DuyuruGetSet duyuru = new DuyuruGetSet(null,
                    icerik,
                    null,
                    icerikLink,
                    link,
                    null,
                    imageLinks
            );
            db.updateDuyuru(duyuru, header, generalMode);


            if (exceptioner) {
                bus.post(new ExceptionerResult("goodToGo"));
                DuyuruExceptionDB db2 = new DuyuruExceptionDB(getApplicationContext());
                db2.deleteFailedDuyuru(header, generalMode);
            } else {
                Log.i("tuna", "service2 completed job, reporting back");
                bus.post(new ThreadResult("goodToGo"));
            }

        } catch (IOException e) {
            Log.i("tuna", e.toString());
            if (exceptioner) {
                bus.post(new ExceptionerResult("exception"));
            } else {
                bus.post(new ThreadResult("ioException"));
                DuyuruExceptionDB db2 = new DuyuruExceptionDB(getApplicationContext());
                DuyuruExceptionGetSet fetcher = new DuyuruExceptionGetSet(
                        header,
                        link
                );
                db2.addFailedDuyuru(fetcher, generalMode);
            }
        }
    }
}

class ThreadResult {
    public String message;

    public ThreadResult(String message) {
        this.message = message;
    }
}

class ExceptionerResult {
    public String message;

    public ExceptionerResult(String message) {
        this.message = message;
    }
}
