package com.hp2m.newsupportlibrary22;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class YemekTask extends AsyncTask<Void, Void, Void> {


    final EventBus bus = EventBus.getDefault();
    private final String URL = "http://mediko.gazi.edu.tr/posts/view/title/yemek-listesi-20412";
    Fragment3 fragment;
    ProgressDialog progressDialog;
    boolean isUpdating;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<String> yemekListFromYemekTask;

    YemekTask(Fragment3 fragment, boolean isUpdating) {
        this.fragment = fragment;
        this.isUpdating = isUpdating;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            if (isUpdating) {
                fragment.swipeLayout.setRefreshing(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.i("tuna", "do in background");
        yemekListFromYemekTask = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(URL)
                    .timeout(0)
                    .get();
            Elements elements = doc.select("div.post-content td");
            // Haftalik veri indiriliyor
            // Burada listeye sadece o gunun verisi kaydediliyor
            // Aslinda hani bir db'ye kaydedip oradan ceksek guzel olurdu
            for (int i = 0; i < 26; i++) { // kalori için 31
                yemekListFromYemekTask.add(elements.get(i).text());
            }

            YemekGetSet yemekGetSet = new YemekGetSet(yemekListFromYemekTask.get(1), yemekListFromYemekTask.get(6),
                    yemekListFromYemekTask.get(11), yemekListFromYemekTask.get(16), yemekListFromYemekTask.get(21));
            YemekGetSet yemekGetSet2 = new YemekGetSet(yemekListFromYemekTask.get(2), yemekListFromYemekTask.get(7),
                    yemekListFromYemekTask.get(12), yemekListFromYemekTask.get(17), yemekListFromYemekTask.get(22));
            YemekGetSet yemekGetSet3 = new YemekGetSet(yemekListFromYemekTask.get(3), yemekListFromYemekTask.get(8),
                    yemekListFromYemekTask.get(13), yemekListFromYemekTask.get(18), yemekListFromYemekTask.get(23));
            YemekGetSet yemekGetSet4 = new YemekGetSet(yemekListFromYemekTask.get(4), yemekListFromYemekTask.get(8),
                    yemekListFromYemekTask.get(14), yemekListFromYemekTask.get(19), yemekListFromYemekTask.get(24));
            YemekGetSet yemekGetSet5 = new YemekGetSet(yemekListFromYemekTask.get(5), yemekListFromYemekTask.get(9),
                    yemekListFromYemekTask.get(15), yemekListFromYemekTask.get(20), yemekListFromYemekTask.get(25));
            ;
            SharedPreferences sharedPreferences = fragment.getActivity().getSharedPreferences("db", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            int i = sharedPreferences.getInt("version", 0) + 1; // there is no chance its being 0 coz of onCreate on Fragment3
            YemekDB dbHandler = new YemekDB(fragment.getActivity(), null, null, i);
            editor.putInt("version", i);
            editor.putInt("lastUpdatedTime", 1);
            editor.commit();
            Log.i("tuna", "about to add");
            dbHandler.addHaftalikYemek(yemekGetSet);
            dbHandler.addHaftalikYemek(yemekGetSet2);
            dbHandler.addHaftalikYemek(yemekGetSet3);
            dbHandler.addHaftalikYemek(yemekGetSet4);
            dbHandler.addHaftalikYemek(yemekGetSet5);

            /* AYRICA KALORÝYÝ ÇEKMEK ÝÇÝN
            YemekGetSet yemekGetSet = new YemekGetSet(yemekList.get(1), yemekList.get(6),
                    yemekList.get(11), yemekList.get(16), yemekList.get(21), yemekList.get(26));
            YemekGetSet yemekGetSet2 = new YemekGetSet(yemekList.get(2), yemekList.get(7),
                    yemekList.get(12), yemekList.get(17), yemekList.get(22), yemekList.get(27));
            YemekGetSet yemekGetSet3 = new YemekGetSet(yemekList.get(3), yemekList.get(8),
                    yemekList.get(13), yemekList.get(18), yemekList.get(23), yemekList.get(28));
            YemekGetSet yemekGetSet4 = new YemekGetSet(yemekList.get(4), yemekList.get(8),
                    yemekList.get(14), yemekList.get(19), yemekList.get(24), yemekList.get(29));
            YemekGetSet yemekGetSet5 = new YemekGetSet(yemekList.get(5), yemekList.get(9),
                    yemekList.get(15), yemekList.get(20), yemekList.get(25), yemekList.get(30));;
             */


        } catch (IOException e) {
            Log.i("tuna", "ioexception in yemektask " + e.toString());
        } catch (Exception e) {
            Log.i("tuna", "exception in yemektask " + e.toString());
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.i("tuna", "we are on postExecute");
        try {
            bus.post(new YemekDownloadComplated("goodToGo"));
            if (isUpdating) {
                fragment.swipeLayout.setRefreshing(false);
                lowerBrightness(fragment.motherLayout);
            }
        } catch (Exception e) {
            Log.i("tuna", "exception in yemektask post execute " + e.toString());
        }

    }

    public void lowerBrightness(FrameLayout layout) {
        AlphaAnimation alpha = new AlphaAnimation(0.05F, 1F);
        alpha.setDuration(2000); // Make animation instant
        //alpha.setFillAfter(true); // Tell it to persist after the animation ends
        // And then on your layout
        layout.startAnimation(alpha);
    }
}

class YemekDownloadComplated {
    public String message;

    public YemekDownloadComplated(String message) {
        this.message = message;
    }
}

