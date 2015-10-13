package com.hp2m.GaziPlus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

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
    int LOADED_ITEM_COUNT = 0;
    boolean hasAnythingDone = false;
    boolean ioException = false;
    private String mode;
    private String URL;
    private String generalMode;
    private SharedPreferences sharedPreferences;


    public DuyuruTask(Fragment1 fragment, String mode) {
        this.fragment = fragment;
        this.mode = mode;
        /* MODES: (we have 3 modes)
        new(updating)
        firstTime
        old
         */
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {

            sharedPreferences = this.fragment.getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
            URL = sharedPreferences.getString("duyuruLink", "");
            generalMode = sharedPreferences.getString("generalMode", "");

            if (mode.equals("updating")) {
                fragment.swipeLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        fragment.swipeLayout.setRefreshing(true);
                    }
                });
            } else if (mode.equals("firstTime")) {
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
        //final String URL = "http://mf-bm.gazi.edu.tr/posts?type=news";
        Log.i("tuna", "do in background");

        // go to main page, fetch links and "duyuru" headers
        // addHeaders to DB directly
        // while updating, check if new item has added
        // like, if duyuruHeaderElement.get(0).text() is on DB or not
        Document doc;
        try {
            int timeout = 10000; // 10 seconds
            if (mode.equals("firstTime"))
                timeout = 0;
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

            final int DB_MAX_DUYURU = db2.getDuyuruSayisi(generalMode);

            // finding max news number on the web page
            // checking index of '<'
            int NET_MAX_DUYURU=4;
            for(int i=0; i<duyuruHeaderElements.size(); i++){
                if(duyuruHeaderElements.get(i).text().charAt(0) == '<'){
                    NET_MAX_DUYURU = i;
                    break;
                }
            }
            // over -------------------------------

            int MIN_ITEM_TO_LOAD;
            //final int MIN_ITEM_TO_LOAD = 4;
            if(generalMode.equals("bolum")) {
                MIN_ITEM_TO_LOAD = sharedPreferences.getInt(DataHolder.MIN_ITEM_TO_LOAD, 4);
            }
            else{
                // coz no fakulte has items below 4
                MIN_ITEM_TO_LOAD = 4;
            }

            // Log.i("tuna", "Net Max Duyuru = "+ NET_MAX_DUYURU);
            // Log.i("tuna", "DB Max Duyuru = "+ DB_MAX_DUYURU);

            //final int DB_MAX_POSSIBLE_DUYURU = DB_MAX_DUYURU - 1;
           // Log.i("tuna", "DB_MAX_DUYURU = " + DB_MAX_DUYURU + " and DB_MAX_POSSIBLE_DUYURU = " + DB_MAX_POSSIBLE_DUYURU);

            if (mode.equals("updating")) {
                Log.i("tuna", "on Update DuyuruTask");
                ArrayList<Integer> updateList = new ArrayList<>();
                //checking db if this element is new or not
                int updatedFirstRow = -1;
                int updatedSecondRow = -1;
                // check if user has loaded "any" new item
                for (int j = DB_MAX_DUYURU; j >= 1; j--) {
                    Log.i("tuna", "last item is " + db2.fetchMeMyDuyuru(j, generalMode).get(0));
                    if (db2.fetchMeMyDuyuru(j, generalMode).get(5).equals("new")) { //db max duyuru+1??
                        if( updatedFirstRow!=-1) {
                            updatedSecondRow = j;
                            break;
                        }
                        updatedFirstRow = j;
                    }
                }
                if (updatedFirstRow == -1) {
                    Log.i("tuna", "user hasnt loaded any new item, check for firstTime items");
                    // user hasnt loaded any new item, check for firstTime items
                    for (int i = 0; i < NET_MAX_DUYURU; i++) {
                        Log.i("tuna", "duyuruHeaderElementsGet(i) = " + duyuruHeaderElements.get(i).text().substring(17));
                        Log.i("tuna", "db2.fetchMeMyDuyuru(1, generalMode).get(0)= " + db2.fetchMeMyDuyuru(1, generalMode).get(0));
                        if (db2.fetchMeMyDuyuru(1, generalMode).get(0).equals(duyuruHeaderElements.get(i).text().substring(17)))
                            break;
                        else {
                            // ------------
                            // DELETE CHECK
                            if (db2.fetchMeMyDuyuru(2, generalMode).get(0).equals(duyuruHeaderElements.get(i).text().substring(17)))
                                break;
                            // DELETE CHECK
                            // ------------
                            updateList.add(i);
                        }
                    }
                } else {
                    Log.i("tuna", "user HAS loaded new items, compare with new items");
                    // user HAS loaded new items
                    for (int i = 0; i < NET_MAX_DUYURU; i++) {
                        Log.i("gazinotification", db2.fetchMeMyDuyuru(updatedFirstRow, generalMode).get(0));
                        Log.i("gazinotification", duyuruHeaderElements.get(i).text().substring(17));
                        // check if item is same or not
                        if (db2.fetchMeMyDuyuru(updatedFirstRow, generalMode).get(0).equals(duyuruHeaderElements.get(i).text().substring(17)))
                            break;
                        else {
                            // add it coz it is new
                            // ------------
                            // DELETE CHECK
                            if(updatedSecondRow!=-1) {
                                if (db2.fetchMeMyDuyuru(updatedSecondRow, generalMode).get(0).equals(duyuruHeaderElements.get(i).text().substring(17)))
                                    break;
                            }
                            // DELETE CHECK
                            // ------------
                            updateList.add(i);
                        }

                    }
                }
                if (updateList.size() == 0) {
                    Log.i("tuna", "no items to load");
                    return null;
                } else {
                    hasAnythingDone = true;
                    LOADED_ITEM_COUNT = updateList.size();
                    bus.post(new ReportingThreadNumbers(LOADED_ITEM_COUNT));
                    DuyuruDB db = new DuyuruDB(fragment.getActivity());

                    for (int i = updateList.size() - 1; i > -1; i--) {
                        headerList.add(duyuruHeaderElements.get(i).text().substring(17));
                        tarihList.add(duyuruHeaderElements.get(i).text().substring(0, 16));
                    }
                    Log.i("tuna", "gonna add " + LOADED_ITEM_COUNT + " item as " + mode);
                    for (int i2 = 0; i2 < LOADED_ITEM_COUNT; i2++) {
                        Log.i("tuna", "adding " + headerList.get(i2) + " to DuyuruDB");
                        DuyuruGetSet duyuru = new DuyuruGetSet(headerList.get(i2),
                                " ",
                                tarihList.get(i2),
                                " ",
                                " ",
                                "new",
                                " "
                        );
                        db.addDuyuru(duyuru, generalMode);
                    }


                    for (int i = 0; i < updateList.size(); i++) {
                        //headerList.add(duyuruHeaderElements.get(i).text().substring(17));
                        //tarihList.add(duyuruHeaderElements.get(i).text().substring(0, 16));
                        // look idk if loaded item count > 4 RIGHT NOW !!
                        // its gonna downloaded in downloadservice1, 5th
                        Intent intent = null;
                        if (i == 0 || i == 4)
                            intent = new Intent(fragment.getActivity(), DownloadService1.class);
                        else if (i == 1)
                            intent = new Intent(fragment.getActivity(), DownloadService2.class);
                        else if (i == 2)
                            intent = new Intent(fragment.getActivity(), DownloadService3.class);
                        else if (i == 3)
                            intent = new Intent(fragment.getActivity(), DownloadService4.class);
                        intent.putExtra("header", duyuruHeaderElements.get(i).text().substring(17));

                        intent.putExtra("link", duyuruHeaderElements.get(i).attr("abs:href"));
                        fragment.getActivity().startService(intent);
                    }
                }
            } else if (mode.equals("old") || mode.equals("firstTime")) {
                hasAnythingDone = true;
                int x;
                if (mode.equals("firstTime")) {
                    if (NET_MAX_DUYURU > 0 + MIN_ITEM_TO_LOAD) // check if website has enough element to fetch
                        x = 0 + MIN_ITEM_TO_LOAD;
                    else
                        x = NET_MAX_DUYURU; // website only have x duyuru left, not MIN_ITEM_TO_LOAD
                    LOADED_ITEM_COUNT = x - 0;

                } else {
                    if (NET_MAX_DUYURU > DB_MAX_DUYURU + MIN_ITEM_TO_LOAD) // check if website has enough element to fetch
                        x = DB_MAX_DUYURU + MIN_ITEM_TO_LOAD;
                    else
                        x = NET_MAX_DUYURU; // website only have x duyuru left, not MIN_ITEM_TO_LOAD
                    LOADED_ITEM_COUNT = x - DB_MAX_DUYURU;

                }
                bus.post(new ReportingThreadNumbers(LOADED_ITEM_COUNT));
                Log.i("tuna", "DB_MAX_DUYURU = " + DB_MAX_DUYURU);
                Log.i("tuna", "NET_MAX_DUYURU = " + NET_MAX_DUYURU);
                Log.i("tuna", "LOADED_ITEM_COUNT = " + LOADED_ITEM_COUNT);


                DuyuruDB db = new DuyuruDB(fragment.getActivity());
                Log.i("tuna", "about to add");

                if (mode.equals("old")) {
                    for (int i = DB_MAX_DUYURU; i < x; i++) {
                        headerList.add(duyuruHeaderElements.get(i).text().substring(17));
                        tarihList.add(duyuruHeaderElements.get(i).text().substring(0, 16));
                    }
                    for (int i2 = 0; i2 < LOADED_ITEM_COUNT; i2++) {
                        DuyuruGetSet duyuru = new DuyuruGetSet(headerList.get(i2),
                                " ",
                                tarihList.get(i2),
                                " ",
                                " ",
                                "old",
                                " "
                        );
                        db.addDuyuru(duyuru, generalMode);
                    }

                    for (int i = DB_MAX_DUYURU; i < x; i++) {
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


                } else if (mode.equals("firstTime")) {
                    db.clearTable(generalMode);
                    int DB_MAX_DUYURU_2 = db.getDuyuruSayisi(generalMode);
                    for (int i = DB_MAX_DUYURU_2; i < x; i++) {
                        headerList.add(duyuruHeaderElements.get(i).text().substring(17));
                        tarihList.add(duyuruHeaderElements.get(i).text().substring(0, 16));
                    }
                    Log.i("tuna", "gonna add " + LOADED_ITEM_COUNT + " item as " + mode);
                    for (int i2 = 0; i2 < LOADED_ITEM_COUNT; i2++) {
                        Log.i("tuna", "adding " + headerList.get(i2) + " to DuyuruDB");
                        Log.i("headerist", "DB header is= " + duyuruHeaderElements.get(i2).text().substring(17));
                        DuyuruGetSet duyuru = new DuyuruGetSet(headerList.get(i2),
                                " ",
                                tarihList.get(i2),
                                " ",
                                " ",
                                "firstTime",
                                " "
                        );
                        db.addDuyuru(duyuru, generalMode);
                    }

                    for (int i = DB_MAX_DUYURU_2; i < x; i++) {
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
                        Log.i("headerist", "header is= " + duyuruHeaderElements.get(i).text().substring(17));
                        intent.putExtra("header", duyuruHeaderElements.get(i).text().substring(17));
                        //intent.putExtra("iSize", x);
                        //intent.putExtra("iStart", DB_MAX_DUYURU);
                        intent.putExtra("link", duyuruHeaderElements.get(i).attr("abs:href"));
                        fragment.getActivity().startService(intent);
                        Log.i("tuna", "service fired, firstTime duyuru loading");
                    }
                }


            }


        } catch (NullPointerException e) {
            Log.i("tuna", "NULL_PONTER_EXCEPTION " + e.toString());
        } /*catch (Exception e) {
            Log.i("tuna", "General Exception " + e.toString());
        }*/
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (ioException) {
            if (mode.equals("old")) {
                // adapter set to get rid of progress dialog
                fragment.recyclerView.setAdapter(fragment.adapter);
                fragment.recyclerView.scrollToPosition(fragment.dataSize);
            } else if (mode.equals("firstTime")) {
                /*fragment.progressBar.setVisibility(View.GONE);
                fragment.recyclerView.setVisibility(View.GONE);
                fragment.reload.setVisibility(View.VISIBLE);
                fragment.reloadText.setVisibility(View.VISIBLE);
                fragment.reload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fragment.LoadDuyuruForFirstTime();
                        fragment.recyclerView.setVisibility(View.VISIBLE);
                        fragment.reload.setVisibility(View.GONE);
                        fragment.reloadText.setVisibility(View.GONE);
                    }
                });*/
                bus.post(new IOExceptionInDuyuruTaskForFirstTime());
            }
            if (mode.equals("updating")) {
                fragment.swipeLayout.setRefreshing(false);
            }
            Snackbar.make(fragment.coordinator, "Sunucuya eriþilemiyor", Snackbar.LENGTH_LONG)
                    .setAction("Tekrar Dene", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new DuyuruTask(fragment, mode).execute();
                        }
                    });

            return;
        }


        if (generalMode.equals("bolum"))
            DataHolder.alreadyShownFragment1ForBolum = true;
        else
            DataHolder.alreadyShownFragment1ForFakulte = true;


        if (mode.equals("firstTime")) {
            fragment.progressBar.setVisibility(View.GONE);
            if (LOADED_ITEM_COUNT == 0) {
                // demek ki bir hata oldu, ioException vs.
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
            if (sharedPreferences.getBoolean("needListUpdate", false))
                fragment.recyclerView.setAlpha(1F);

            bus.post(new DuyuruDownloadComplated("letsGo", mode));
            return;
        }

        // mod old veya updating
        if (LOADED_ITEM_COUNT > 0) {
            bus.post(new DuyuruDownloadComplated("letsGo", mode));
            if (mode.equals("updating")) {
                // updating'de yeni item varsa flash efekti
                lowerBrightness(fragment.motherLayout);
            }

        } else {
            if (sharedPreferences.getBoolean("needListUpdate", false))
                bus.post(new DuyuruDownloadComplated("letsGo", mode));
            else
                fragment.setFabToReady();

        }
        // updating ise swipeLayout'u durdur
        if (mode.equals("updating")) {
            Log.i("tuna", "setRefreshing to --> false");
           fragment.swipeLayout.post(new Runnable() {
               @Override
               public void run() {
                   fragment.swipeLayout.setRefreshing(false);
               }
           });

           /* if (LOADED_ITEM_COUNT == 0) {
                Snackbar.make(fragment.motherLayout, "Duyurular güncel", Snackbar.LENGTH_SHORT)
                        .show();
            }*/
        }
        //hata varsa

        if (mode.equals("old")) { // limit duyuru reached
            if (LOADED_ITEM_COUNT == 0) {
                fragment.recyclerView.setAdapter(fragment.adapter);
                fragment.recyclerView.scrollToPosition(fragment.dataSize);
                Snackbar.make(fragment.coordinator, "Duyuru bulunamadý", Snackbar.LENGTH_LONG)
                        .show();
            }
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

class IOExceptionInDuyuruTaskForFirstTime {

}

class DuyuruDownloadComplated {
    public String message, mode;

    public DuyuruDownloadComplated(String message, String mode) {
        this.message = message;
        this.mode = mode;
    }
}

class ReportingThreadNumbers {
    public int loaded_item_count;

    public ReportingThreadNumbers(int loaded_item_count) {
        this.loaded_item_count = loaded_item_count;
    }
}