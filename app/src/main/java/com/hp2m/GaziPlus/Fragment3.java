package com.hp2m.GaziPlus;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;


public class Fragment3 extends Fragment {

    final EventBus bus = EventBus.getDefault();
    SharedPreferences sharedPreferences;
    FrameLayout motherLayout;
    SwipeRefreshLayout swipeLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<String> yemekList;
    private DataHolder dataHolder = new DataHolder();
    private ImageButton reload;
    private TextView reloadText;

    public Fragment3() {

    }

    private static boolean doesDatabaseExist(ContextWrapper context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(
                R.layout.fragment3, container, false);
        bus.register(this);

        reload = (ImageButton) rootView.findViewById(R.id.reload);
        reloadText = (TextView) rootView.findViewById(R.id.reloadText);

        motherLayout = (FrameLayout) rootView.findViewById(R.id.fragment3_motherLayout);
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        /*swipeLayout.setProgressBackgroundColorSchemeColor(
                getResources().getColor(R.color.shadow_end_color)*
        );*/
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                yemekGuncelle();

            }
        });
        swipeLayout.setColorSchemeResources(R.color.card_color_1,
                R.color.card_color_2,
                R.color.card_color_4,
                R.color.card_color_5);
        sharedPreferences = getActivity().getSharedPreferences("db", Context.MODE_PRIVATE);
        //YemekDB db = new YemekDB(getActivity(), null, null, sharedPreferences.getInt("version", 1));
        //Log.i("tuna", "this is " + isDBExists(db.getDatabaseName()));
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        adapter = new YemekAdapter(getActivity(), getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        return rootView;
    }

    private List<YemekInformation> getData() { // I MADE THIS PRIVATE
        Log.i("tuna", "yemek getData");
        YemekDB db = new YemekDB(getActivity());
        //if(doesTableExist(db.getReadableDatabase(), db.TABLE_YEMEK)) { // check if DB is old or not, created or not
        if (db.getYemekSayisi() != 0) {
            // CHECK IF DB IS OLD OR NOT
            List<YemekInformation> data = new ArrayList<>();
            int[] cardColors = {getResources().getColor(R.color.card_color_1),
                    getResources().getColor(R.color.card_color_2),
                    getResources().getColor(R.color.card_color_3),
                    getResources().getColor(R.color.card_color_4),
                    getResources().getColor(R.color.card_color_5)};
            for (int i = 0; i < cardColors.length; i++) {
                yemekList = new ArrayList<>();
                YemekInformation current = new YemekInformation();
                yemekList.addAll(db.fetchMeMyFood(i + 1));
                // above code returns one "day" for food list
                current.header = yemekList.get(0);
                current.yemek1 = yemekList.get(1);
                current.yemek2 = yemekList.get(2);
                current.yemek3 = yemekList.get(3);
                current.yemek4 = yemekList.get(4);
                current.cardColor = cardColors[i];
                data.add(current);
            }
            if (!dataHolder.alreadyShownFragment3) {
                yemekGuncelle();
                dataHolder.alreadyShownFragment3 = true;
            }
            else{
                scrollToDay(recyclerView);
            }
            return data;
        } else {
            List<YemekInformation> data = Collections.emptyList();
            if (isNetworkAvailable()) {
                dataHolder.alreadyShownFragment3 = true;
                yemekYukle();
            } else {
                reload.setVisibility(View.VISIBLE);
                reloadText.setVisibility(View.VISIBLE);
                reload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reload.setVisibility(View.GONE);
                        reloadText.setVisibility(View.GONE);
                        getData();
                    }
                });
                Snackbar.make(motherLayout, "İnternete erişilemiyor", Snackbar.LENGTH_SHORT)
                        .setAction("Yeniden dene", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getData();
                            }
                        });
            }
            return data;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void onEvent(YemekDownloadComplated event) {
        Log.i("tuna", "we are on Event");
        YemekDB db = new YemekDB(getActivity());
        if (event.message.equals("goodToGo")) {
            List<YemekInformation> data = new ArrayList<>();
            int[] cardColors = {getResources().getColor(R.color.card_color_1),
                    getResources().getColor(R.color.card_color_2),
                    getResources().getColor(R.color.card_color_3),
                    getResources().getColor(R.color.card_color_4),
                    getResources().getColor(R.color.card_color_5)};
            for (int i = 0; i < cardColors.length; i++) {
                yemekList = new ArrayList<>();
                YemekInformation current = new YemekInformation();
                yemekList.addAll(db.fetchMeMyFood(i + 1));
                Log.i("tuna", "yemek sayisi= " + db.getYemekSayisi());
                current.header = yemekList.get(0);
                current.yemek1 = yemekList.get(1);
                Log.i("tuna","yemekList.get(1)= "  +yemekList.get(1));

                current.yemek2 = yemekList.get(2);
                current.yemek3 = yemekList.get(3);
                current.yemek4 = yemekList.get(4);
                current.cardColor = cardColors[i];
                data.add(current);
            }
            final List<YemekInformation> data2 = data;
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    adapter = new YemekAdapter(getActivity(), data2);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    scrollToDay(recyclerView);
                }
            };
            getActivity().runOnUiThread(r);
        } else {
            if (db.getYemekSayisi() == 0) {
                reload.setVisibility(View.VISIBLE);
                reloadText.setVisibility(View.VISIBLE);
                reload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reload.setVisibility(View.GONE);
                        reloadText.setVisibility(View.GONE);
                        yemekYukle();
                    }
                });
            } else {
                Log.i("tuna", "on Show Snackbar");
                motherLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(motherLayout, "Sunucuya erişilemedi", Snackbar.LENGTH_LONG)
                                .setAction("Tekrar dene", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        yemekGuncelle();
                                    }
                                });
                    }
                });

            }
        }

    }

    private void scrollToDay(RecyclerView recyclerView) {
        // scroll to day
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.MONDAY:
                recyclerView.scrollToPosition(0);
                break;
            case Calendar.TUESDAY:
                recyclerView.scrollToPosition(1);
                break;
            case Calendar.WEDNESDAY:
                recyclerView.scrollToPosition(2);
                break;
            case Calendar.THURSDAY:
                recyclerView.scrollToPosition(3);
                break;
            case Calendar.FRIDAY:
                recyclerView.scrollToPosition(4);
                break;
            default:
                break;

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    private void yemekGuncelle() {
        if (isNetworkAvailable()) {
            swipeLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeLayout.setRefreshing(true);
                }
            });
            new YemekTask(this, true).execute();
        }
        else{
            swipeLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeLayout.setRefreshing(false);
                }
            });
            scrollToDay(recyclerView);
            if(DataHolder.alreadyShownFragment3){
                Snackbar.make(motherLayout, "İnternete erişilemiyor", Snackbar.LENGTH_SHORT);
            }
        }
    }

    private void yemekYukle() {
        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(true);
            }
        });
        new YemekTask(this, false).execute();
    }

}

