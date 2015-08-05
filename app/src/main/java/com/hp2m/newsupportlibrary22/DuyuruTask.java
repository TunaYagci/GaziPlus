package com.hp2m.newsupportlibrary22;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class DuyuruTask extends AsyncTask<Void, Void, Void> {

    final EventBus bus = EventBus.getDefault();
    Fragment1 fragment;
    String mode;
    int LOADED_ITEM_COUNT = 0;
    boolean hasAnythingDone = false;
    boolean ioException = false;

    public DuyuruTask(Fragment1 fragment, String mode) {
        this.fragment = fragment;
        this.mode = mode;
        /* MODES: (we have 3 modes)
        updating
        firstTime
        old
         */
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            if (mode == "updating") {
                fragment.swipeLayout.setRefreshing(true);
            } else if (mode == "firstTime") {
                fragment.progressBar.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.i("tuna", e.toString());
        }
    }


    @Override
    protected Void doInBackground(Void... params) {
        // List<String> icerikList, icerikLinkList, newsLinkList, headerList;
        List<String> headerList, tarihList;
        final String URL = "http://mf-bm.gazi.edu.tr/posts?type=news";
        Log.i("tuna", "do in background");

        // go to main page, fetch links and "duyuru" headers
        // addHeaders to DB directly
        // while updating, check if new item has added
        // like, if duyuruHeaderElement.get(0).text() is on DB or not
        Document doc = null;
        try {
            int timeout = 5000;
            if (mode == "firstTime")
                timeout = 2000;
            doc = Jsoup.connect(URL).timeout(timeout).get(); // biiiig timeoouuuut
        } catch (IOException e) {
            Log.i("tuna", "timeout done");
            ioException = true;
            Log.i("tuna", e.toString());
            return null;
        }
        try {
            Log.i("tuna", "doc downloaded");
            Elements duyuruHeaderElements = doc.select("div.app-content li a[href]");

            headerList = new ArrayList<>();
            tarihList = new ArrayList<>();

            DuyuruDB db2 = new DuyuruDB(fragment.getActivity());

            final int DB_MAX_DUYURU = db2.getDuyuruSayisi();
            final int NET_MAX_DUYURU = duyuruHeaderElements.size() - 4;
            final int MIN_ITEM_TO_LOAD = 4;


            if (mode == "update") {
                ArrayList<Integer> updateList = new ArrayList<>();
                //checking db if this element is new or not
                for (int i = 0; i < NET_MAX_DUYURU; i++) {
                    if (db2.fetchMeMyDuyuru(i + 1).get(0) == duyuruHeaderElements.get(i).text().substring(17))
                        break;
                    else {
                        updateList.add(i);
                    }
                }
                if (updateList.size() == 0) {
                    Toast.makeText(fragment.getActivity(), "Duyurular güncel", Toast.LENGTH_SHORT).show();
                    return null;
                } else {
                    hasAnythingDone = true;
                    LOADED_ITEM_COUNT = updateList.size();
                    bus.post(new ReportingThreadNumbers(LOADED_ITEM_COUNT));
                    for (int i = 0; i < updateList.size(); i++) {
                        headerList.add(duyuruHeaderElements.get(i).text().substring(17));
                        tarihList.add(duyuruHeaderElements.get(i).text().substring(0, 16));
                        // look idk if loaded item count > 4 RIGHT NOW !!
                        // its probably gonna downloaded in downloadservice4
                        Intent intent = null;
                        if (i == 0 || i == 4)
                            intent = new Intent(fragment.getActivity(), DownloadService1.class);
                        else if (i == 1 || i == 5)
                            intent = new Intent(fragment.getActivity(), DownloadService2.class);
                        else if (i == 2 || i == 6)
                            intent = new Intent(fragment.getActivity(), DownloadService3.class);
                        else if (i == 3 || i == 7)
                            intent = new Intent(fragment.getActivity(), DownloadService4.class);
                        intent.putExtra("header", duyuruHeaderElements.get(i).text().substring(17));

                        intent.putExtra("link", duyuruHeaderElements.get(i).attr("abs:href"));
                        fragment.getActivity().startService(intent);
                    }
                }
            } else if (mode == "old" || mode == "firstTime") {
                hasAnythingDone = true;
                int x;
                if (NET_MAX_DUYURU > DB_MAX_DUYURU + MIN_ITEM_TO_LOAD) // check if website has enough element to fetch
                    x = DB_MAX_DUYURU + MIN_ITEM_TO_LOAD;
                else
                    x = NET_MAX_DUYURU; // website only have x duyuru left, not MIN_ITEM_TO_LOAD
                LOADED_ITEM_COUNT = x - DB_MAX_DUYURU;
                bus.post(new ReportingThreadNumbers(LOADED_ITEM_COUNT));
                Log.i("tuna", "DB_MAX_DUYURU = " + DB_MAX_DUYURU);
                Log.i("tuna", "NET_MAX_DUYURU = " + NET_MAX_DUYURU);
                Log.i("tuna", "LOADED_ITEM_COUNT = " + LOADED_ITEM_COUNT);

                for (int i = DB_MAX_DUYURU; i < x; i++) {
                    headerList.add(duyuruHeaderElements.get(i).text().substring(17));
                    tarihList.add(duyuruHeaderElements.get(i).text().substring(0, 16));
                    // look idk if loaded item count > 4 RIGHT NOW !!
                    Intent intent;
                    if (x - i == 4) // thats not loaded_item_count, this is: loading order of those items. like 3rd one
                        intent = new Intent(fragment.getActivity(), DownloadService4.class);
                    else if (x - i == 3)
                        intent = new Intent(fragment.getActivity(), DownloadService3.class);
                    else if (x - i == 2)
                        intent = new Intent(fragment.getActivity(), DownloadService2.class);
                    else
                        intent = new Intent(fragment.getActivity(), DownloadService1.class);
                    intent.putExtra("header", duyuruHeaderElements.get(i).text().substring(17));
                    //intent.putExtra("iSize", x);
                    //intent.putExtra("iStart", DB_MAX_DUYURU);
                    intent.putExtra("link", duyuruHeaderElements.get(i).attr("abs:href"));
                    fragment.getActivity().startService(intent);
                    Log.i("tuna", "service fired, old duyuru loading");
                }


            }


            if (hasAnythingDone) {
                DuyuruDB db = new DuyuruDB(fragment.getActivity());
                // we basically clearing all previous data before adding
                // fix that on DuyuruDB
                Log.i("tuna", "about to add");
                if (mode == "old") {
                    for (int i2 = 0; i2 < LOADED_ITEM_COUNT; i2++) {
                        DuyuruGetSet duyuru = new DuyuruGetSet(headerList.get(i2),
                                " ",
                                tarihList.get(i2),
                                " ",
                                " ",
                                "old"
                        );
                        db.addDuyuru(duyuru);
                    }
                } else if (mode == "firstTime" || mode == "update") {
                    for (int i2 = 0; i2 < LOADED_ITEM_COUNT; i2++) {
                        DuyuruGetSet duyuru = new DuyuruGetSet(headerList.get(i2),
                                " ",
                                tarihList.get(i2),
                                " ",
                                " ",
                                "new"
                        );
                        db.addDuyuru(duyuru);
                    }
                }
            }


        } catch (NullPointerException e) {
            Log.i("tuna", "NULL_PONTER_EXCEPTION " + e.toString());
        } catch (Exception e) {
            Log.i("tuna", "General Exception " + e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (mode == "firstTime") {
            fragment.progressBar.setVisibility(View.GONE);
            if (LOADED_ITEM_COUNT == 0) { // demek ki bir hata oldu, ioException vs.
                fragment.reload.setVisibility(View.VISIBLE);
                fragment.reloadText.setVisibility(View.VISIBLE);
                fragment.reload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fragment.LoadDuyuruForFirstTime();
                        fragment.reload.setVisibility(View.INVISIBLE);
                        fragment.reloadText.setVisibility(View.INVISIBLE);
                    }
                });
            }
            bus.post(new DuyuruDownloadComplated("letsGo"));
            return;
        }

        // mod old veya updating
        if (LOADED_ITEM_COUNT > 0) {
            bus.post(new DuyuruDownloadComplated("letsGo"));
            if (mode == "updating") {
                // updating'de yeni item varsa flash efekti
                lowerBrightness(fragment.motherLayout);
            }

        }
        // updating ise swipeLayout'u durdur
        if (mode == "updating") {
            fragment.swipeLayout.setRefreshing(false);
            if (LOADED_ITEM_COUNT == 0) {
                Snackbar.make(fragment.motherLayout, "Duyurular güncel", Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
        //hata varsa
        if (ioException) {
            if (mode == "old") {
                // adapter set to get rid of progress dialog
                fragment.recyclerView.setAdapter(fragment.adapter);
                fragment.recyclerView.scrollToPosition(fragment.dataSize);
            }
            Snackbar.make(fragment.motherLayout, "Sunucuya eriþilemiyor", Snackbar.LENGTH_LONG)
                    .setAction("Tekrar Dene", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new DuyuruTask(fragment, mode).execute();
                        }
                    })
                    .show();

        }
        if (mode == "old") { // limit duyuru reached
            if (LOADED_ITEM_COUNT == 0) {
                fragment.recyclerView.setAdapter(fragment.adapter);
                fragment.recyclerView.scrollToPosition(fragment.dataSize);
                Snackbar.make(fragment.motherLayout, "Duyuru bulunamadý", Snackbar.LENGTH_LONG)
                        .show();
            }
        }

        DataHolder dataHolder = new DataHolder();
        dataHolder.alreadyShownFragment1 = true;

    }

    public void lowerBrightness(FrameLayout layout) {
        AlphaAnimation alpha = new AlphaAnimation(0.05F, 1F);
        alpha.setDuration(2000); // Make animation instant
        //alpha.setFillAfter(true); // Tell it to persist after the animation ends
        // And then on your layout
        layout.startAnimation(alpha);
    }
}


class DuyuruDownloadComplated {
    public String message;

    public DuyuruDownloadComplated(String message) {
        this.message = message;
    }
}

class ReportingThreadNumbers {
    public int loaded_item_count;

    public ReportingThreadNumbers(int loaded_item_count) {
        this.loaded_item_count = loaded_item_count;
    }
}