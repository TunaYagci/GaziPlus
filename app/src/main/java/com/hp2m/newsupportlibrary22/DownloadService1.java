package com.hp2m.newsupportlibrary22;

import android.app.IntentService;
import android.content.Intent;
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

            Elements duyuruElements = insideDocs.select("div.post-content p");
            StringBuilder builder = new StringBuilder();
            for (int i2 = 0; i2 < duyuruElements.size(); i2++) {
                Log.i("tuna", "inner side parsing 2");
                if (!duyuruElements.get(i2).text().isEmpty()) {
                    //a += duyuruElements.get(i2).text();
                    //a += "\n\n";
                    builder.append(duyuruElements.get(i2).text());
                    builder.append("\n\n");
                }
            }

            try {
                Elements duyuruInsafsizElement = insideDocs.select("div.post-content div");
                for (int i2 = 0; i2 < duyuruInsafsizElement.size(); i2++) {
                    Log.i("tuna", "inner side parsing 2");
                    if (!duyuruInsafsizElement.get(i2).text().isEmpty()) {
                        //a += duyuruElements.get(i2).text();
                        //a += "\n\n";
                        builder.append(duyuruInsafsizElement.get(i2).text());
                        builder.append("\n\n");
                    }
                }
            } catch (Exception e) {
                Log.i("tuna", "insafsiz element exception " + e.toString());
            }
            icerik = builder.toString();
            builder = new StringBuilder();
            Log.i("tuna", "gonna parse links");
            Elements duyuruLinkElements = insideDocs.select("div.post-content a[href]");
            Log.i("tuna", "link parsing ended");
            for (int i3 = 0; i3 < duyuruLinkElements.size(); i3++) {
                Log.i("tuna", "inner side parsing 3");
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


            Log.i("tuna", "gonna look for image links");
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
            db.updateDuyuru(duyuru, header);


            if (exceptioner) {
                bus.post(new ExceptionerResult("goodToGo"));
                DuyuruExceptionDB db2 = new DuyuruExceptionDB(getApplicationContext());
                db2.deleteFailedDuyuru(header);
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
                db2.addFailedDuyuru(fetcher);
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
