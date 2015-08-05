package com.hp2m.newsupportlibrary22;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.File;
import java.util.ArrayList;
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

    public Fragment3() {

    }

    private static boolean doesDatabaseExist(ContextWrapper context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //bus.register(this);
        View rootView = (View) inflater.inflate(
                R.layout.fragment3, container, false);
        bus.register(this);

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
        swipeLayout.setColorSchemeColors(R.color.my_primary_dark);

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
        YemekDB db = new YemekDB(getActivity(), null, null, sharedPreferences.getInt("version", 1));
        //if(doesTableExist(db.getReadableDatabase(), db.TABLE_YEMEK)) { // check if DB is old or not, created or not
        if (doesDatabaseExist(getActivity(), db.getDatabaseName())) {
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
                current.header = yemekList.get(0);
                current.yemek1 = yemekList.get(1);
                current.yemek2 = yemekList.get(2);
                current.yemek3 = yemekList.get(3);
                current.yemek4 = yemekList.get(4);
                current.cardColor = cardColors[i];
                data.add(current);
            }
            return data;
        } else {
            List<YemekInformation> data = Collections.emptyList();
            if (isNetworkAvailable()) {
                yemekYukle();
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
        YemekDB db = new YemekDB(getActivity(), null, null, sharedPreferences.getInt("version", 1));
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
            current.header = yemekList.get(0);
            current.yemek1 = yemekList.get(1);
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
            }
        };
        getActivity().runOnUiThread(r);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    private void yemekGuncelle() {
        new YemekTask(this, true).execute();
    }

    private void yemekYukle() {
        new YemekTask(this, false).execute();
    }

}

