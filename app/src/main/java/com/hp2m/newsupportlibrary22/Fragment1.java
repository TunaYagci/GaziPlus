package com.hp2m.newsupportlibrary22;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment1 extends Fragment {

    final EventBus bus = EventBus.getDefault();
    public int countOfItemsToBeReported;
    public int reportedThreadsSoFar;
    public DataHolder dataHolder = new DataHolder();
    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    public int dataSize;
    public ImageButton reload;
    public TextView reloadText;
    public FloatingActionButton fab;
    SwipeRefreshLayout swipeLayout;
    CoordinatorLayout coordinator;
    FrameLayout motherLayout;
    ProgressBar progressBar;
    private int failedThreadsSoFar;
    private SharedPreferences sP;
    private SharedPreferences.Editor editor;

    public Fragment1() {

    }


    /*private static boolean doesDatabaseExist(ContextWrapper context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }*/

    private static String getTimeDifference(String old, String now) {
        // taking "now" as parameter coz SimpleDataFormat may cause lag
        // a=yýl farký, b=ay farký c=gun farký
        int b, c, d, e;
        int a = (Integer.parseInt(now.substring(0, 4)) -
                Integer.parseInt(old.substring(0, 4)));
        b = Integer.parseInt(now.substring(5, 7)) -
                Integer.parseInt(old.substring(5, 7));
        if (a != 0) {
            if ((b + 12) < 12)
                return Integer.toString(b + 12) + " ay önce";
            else
                return Integer.toString(a) + " yýl önce";
        } else { // on same year
            c = Integer.parseInt(now.substring(8, 10)) -
                    Integer.parseInt(old.substring(8, 10));
            if (b != 0) { // not on same month
                if (b == 1 && (c + 30) < 30)
                    return Integer.toString(c + 30) + " gün önce";
                else
                    return Integer.toString(b) + " ay önce";
            } else {
                if (c != 0) // not on same day
                    return Integer.toString(c) + " gün önce";
                else { // on same day
                    d = Integer.parseInt(now.substring(11, 13)) -
                            Integer.parseInt(old.substring(11, 13));
                    e = Integer.parseInt(now.substring(14, 16)) -
                            Integer.parseInt(old.substring(14, 16));
                    if (d != 0) {
                        if (d == 1 && (e + 60) < 60)
                            return Integer.toString(e + 30) + " dakika önce";
                        else
                            return Integer.toString(d) + " saat önce";
                    } else
                        return Integer.toString(e) + " dakika önce";
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment1, container, false);
        bus.register(this);

        reload = (ImageButton) rootView.findViewById(R.id.reload);
        reloadText = (TextView) rootView.findViewById(R.id.reloadText);
        motherLayout = (FrameLayout) rootView.findViewById(R.id.fragment1_motherLayout);
        progressBar = (ProgressBar) rootView.findViewById(R.id.loadingBar);

        sP = this.getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = sP.edit();
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        coordinator = (CoordinatorLayout) rootView.findViewById(R.id.coordinator2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("tuna", "fab click");
                handleFabClicks();
            }
        });
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeResources(R.color.card_color_1,
                R.color.card_color_2,
                R.color.card_color_4,
                R.color.card_color_5);
        /*swipeLayout.setProgressBackgroundColorSchemeColor(
                getResources().getColor(R.color.shadow_end_color)*
        );*/
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("tuna", "onRefresh");
                duyuruGuncelle();

            }
        });

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        adapter = new DuyuruAdapter(getActivity(), getData(), new DuyuruAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                handleCardClicks(view, position);
            }
        });
        recyclerView.setAdapter(adapter);
        final LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean hideToolBar = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (hideToolBar) {
                    fab.animate().translationY(300);
                } else {
                    fab.animate().translationY(0);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 20) {
                    hideToolBar = true;

                } else if (dy < -5) {
                    hideToolBar = false;
                }
            }
        });

        return rootView;
    }

    public void setFabToLoading() {
        fab.setClickable(false);
        fab.setImageResource(R.drawable.loading_notlar_avatar_2); // or loading_fab...
    }

    public void setFabToReady() {
        String generalMode = sP.getString("generalMode", "");
        if (generalMode.equals("bolum")) {
            fab.setImageResource(R.drawable.ic_arrow_forward_white_36dp);
        } else {
            fab.setImageResource(R.drawable.ic_arrow_back_white_36dp);
        }
        editor.putBoolean("needListUpdate", false);
        editor.commit();
        fab.setClickable(true);
    }

    private void handleFabClicks() {
        setFabToLoading();
        editor.putBoolean("needListUpdate", true);
        if (sP.getString("generalMode", "bolum").equals("bolum")) { // fakülteye gidicez yani
            editor.putString("duyuruLink", sP.getString("defaultFakulteLink", ""));
            editor.putString("generalMode", "fakulte");
        } else { // fakülteden bölüme gidicez
            editor.putString("duyuruLink", sP.getString("defaultBolumLink", ""));
            editor.putString("generalMode", "bolum");
        }
        editor.commit();

        DuyuruDB db = new DuyuruDB(getActivity());
        if (db.getDuyuruSayisi(sP.getString("generalMode", "")) != 0) { // not the first time
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    adapter = new DuyuruAdapter(getActivity(), getData(), new DuyuruAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            handleCardClicks(view, position);
                        }
                    });
                    //adapter.notifyItemInserted(data2.size()-1);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }
            };
            getActivity().runOnUiThread(r);
            //setFabToReady();
        } else {
            if (isNetworkAvailable()) {
                recyclerView.setAlpha(0.5F);
                LoadDuyuruForFirstTime();
            } else {
                editor.putString("duyuruLink", sP.getString("defaultBolumLink", ""));
                editor.putString("generalMode", "bolum");
                editor.commit();
                setFabToReady();
                Snackbar.make(coordinator, "Ýnternete baðlanýlamýyor", Snackbar.LENGTH_LONG)
                        .setAction("Tekrar Dene", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handleFabClicks();
                            }
                        })
                        .show();
            }
        }

    }

    public List<DuyuruInformation> getData() {
        boolean needUpdateForEmergency = false;
        String generalMode = sP.getString("generalMode", "");
        DuyuruDB db = new DuyuruDB(getActivity());
        // check if db exists and "up-to-date"
        // check if db up to date PLEASEEEE
        //if (doesTableExist(db.getReadableDatabase(), db.TABLE_DUYURU)) {
        //if (doesDatabaseExist(getActivity(), db.getDatabaseName())) { // LATER ON CHANGE THIS, thats very SLOWW
        if (db.getDuyuruSayisi(generalMode) != 0) {
            ArrayList<Integer> oldList = new ArrayList<>();
            ArrayList<Integer> firstTimeList = new ArrayList<>();
            ArrayList<Integer> newList = new ArrayList<>();
            ArrayList<String> duyuruList;
            Log.i("tuna", "we are on getData Fragment1");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String currentTime = sdf.format(new Date());
            //DuyuruDB db = new DuyuruDB(getActivity(), null, null, sharedPreferences.getInt("version", 1));
            List<DuyuruInformation> data = new ArrayList<>();
            DuyuruInformation current = new DuyuruInformation();
            if (generalMode.equals("bolum")) {
                current.imageID = sP.getInt("bolumImg", 0);
                current.bolum = sP.getString("bolumAdi", "");
            } else {
                current.imageID = sP.getInt("fakulteImg", 0);
                current.bolum = sP.getString("fakulteAdi", "");
            }
            data.add(current); // FIRST, ADD HEADER*/
            for (int i = 1; i < db.getDuyuruSayisi(generalMode) + 1; i++) { // ADDING NEW DUYURU IF THERE IS
                duyuruList = new ArrayList<>();
                duyuruList.addAll(db.fetchMeMyDuyuru(i, generalMode)); // coz db reads _id as normal people, i starts from 1, BECAREFUL!!
                if (duyuruList.get(5).equals("firstTime")) {
                    Log.i("tuna", "this is a firstTime post = " + duyuruList.get(0));
                    firstTimeList.add(i);
                    continue;
                } else if (duyuruList.get(5).equals("old")) {
                    Log.i("tuna", "this is an old post = " + duyuruList.get(0));
                    oldList.add(i);
                    continue;
                }
                // so what we add here is basically the "new" items
                Log.i("tuna", "this is a new post = " + duyuruList.get(0));
                newList.add(i);
                /*current = new DuyuruInformation();
                current.header = duyuruList.get(0);
                current.body = duyuruList.get(1);
                current.dateDiff = getTimeDifference(
                        duyuruList.get(2),
                        currentTime
                );
                data.add(current);*/
            }
            for (int i = newList.size() - 1; i >= 0; i--) { // ADDING NEW DUYURU IF THERE IS
                duyuruList = new ArrayList<>();
                duyuruList.addAll(db.fetchMeMyDuyuru(newList.get(i), generalMode)); // coz db reads _id as normal people, i starts from 1, BECAREFUL!!
                current = new DuyuruInformation();
                //Log.i("tuna", "a new post is pulling " + duyuruList.get(0));
                current.header = duyuruList.get(0);
                if (duyuruList.get(1).isEmpty())
                    current.body = "Resmi görüntülemek için týklayýn";
                else
                    current.body = duyuruList.get(1);
                current.dateDiff = getTimeDifference(
                        duyuruList.get(2),
                        currentTime
                );
                if (i == 0 && current.dateDiff.startsWith("-")) // CHECK FOR STATIC DUYURU
                    needUpdateForEmergency = true;
                data.add(current);
            }

            for (int i = 0; i < firstTimeList.size(); i++) { // ADDING FIRSTTIME DUYURU
                duyuruList = new ArrayList<>();
                duyuruList.addAll(db.fetchMeMyDuyuru(firstTimeList.get(i), generalMode)); // coz db reads _id as normal people, i starts from 1, BECAREFUL!!
                current = new DuyuruInformation();
                // Log.i("tuna", "a firstTime post is pulling " + duyuruList.get(0));
                current.header = duyuruList.get(0);
                if (duyuruList.get(1).isEmpty())
                    current.body = "Resmi görüntülemek için týklayýn";
                else
                    current.body = duyuruList.get(1);
                current.dateDiff = getTimeDifference(
                        duyuruList.get(2),
                        currentTime
                );

                if (i == 0 && !needUpdateForEmergency && current.dateDiff.startsWith("-")) // CHECK FOR STATIC DUYURU
                    needUpdateForEmergency = true;
                data.add(current);
            }


            for (int i = 0; i < oldList.size(); i++) { // ADDING OLD DUYURU IF THERE IS
                duyuruList = new ArrayList<>();
                duyuruList.addAll(db.fetchMeMyDuyuru(oldList.get(i), generalMode)); // coz db reads _id as normal people, i starts from 1, BECAREFUL!!
                current = new DuyuruInformation();
                // Log.i("tuna", "an old post is pulling " + duyuruList.get(0));
                current.header = duyuruList.get(0);
                if (duyuruList.get(1).isEmpty())
                    current.body = "Resmi görüntülemek için týklayýn";
                else
                    current.body = duyuruList.get(1);
                current.dateDiff = getTimeDifference(
                        duyuruList.get(2),
                        currentTime
                );
                data.add(current);
            }

            dataSize = data.size();
            if (generalMode.equals("bolum")) {
                if (!dataHolder.alreadyShownFragment1ForBolum) {
                    if (needUpdateForEmergency) {
                        if (isNetworkAvailable()) {
                            data = Collections.emptyList(); // if there is a static duyuru, clear db and load from stratch
                            Log.i("tuna", "emergency first time method fired");
                            LoadDuyuruForFirstTime();
                        }
                    } else {
                        duyuruGuncelle();
                    }
                } else
                    setFabToReady();
            } else {
                if (!dataHolder.alreadyShownFragment1ForFakulte) {
                    if (needUpdateForEmergency) {
                        if (isNetworkAvailable()) {
                            data = Collections.emptyList(); // if there is a static duyuru, clear db and load from stratch
                            Log.i("tuna", "emergency first time method fired");
                            LoadDuyuruForFirstTime();
                        }
                    } else {
                        duyuruGuncelle();
                    }
                } else
                    setFabToReady();
            }
            return data;
        } else {
            Log.i("tuna", "db doesn't exist");
            //List<DuyuruInformation> data = Collections.emptyList();
            LoadDuyuruForFirstTime();
            return Collections.emptyList();
        }
    }

    public void eskiDuyuruYukle() {
        if (isNetworkAvailable()) {
            setFabToLoading();
            new DuyuruTask(this, "old").execute();
        } else {
            recyclerView.setAdapter(adapter);
            recyclerView.scrollToPosition(dataSize);
            Snackbar.make(coordinator, "Ýnternete baðlanýlamýyor", Snackbar.LENGTH_LONG)
                    .setAction("Tekrar Dene", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eskiDuyuruYukle();
                        }
                    })
                    .show();
        }
    }

    public void duyuruGuncelle() {
        if (isNetworkAvailable()) {
            setFabToLoading();
            new DuyuruTask(this, "updating").execute();
        } else {
            swipeLayout.setRefreshing(false);
            /*Snackbar.make(coordinator, "Ýnternete baðlanýlamýyor", Snackbar.LENGTH_LONG)
                    .setAction("Tekrar Dene", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eskiDuyuruYukle();
                        }
                    })
                    .show();*/
        }
    }

    public void LoadDuyuruForFirstTime() { // ilk giriþte çaðýr
        if (isNetworkAvailable()) {
            setFabToLoading();
            new DuyuruTask(this, "firstTime").execute(); // bu toast'larý snackbar yapalým
        } else {
            reload.setVisibility(View.VISIBLE);
            reloadText.setVisibility(View.VISIBLE);
            reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoadDuyuruForFirstTime();
                    reload.setVisibility(View.INVISIBLE);
                    reloadText.setVisibility(View.INVISIBLE);
                }
            });

            Snackbar.make(coordinator, "Ýnternete baðlanýlamýyor", Snackbar.LENGTH_LONG)
                    .setAction("Tekrar Dene", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LoadDuyuruForFirstTime();
                        }
                    })
                    .show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);

        // go default on ondestroy
        editor.putString("generalMode", "bolum");
        editor.putString("duyuruLink", sP.getString("defaultBolumLink", ""));
        editor.commit();
    }

    public void onEvent(DuyuruDownloadComplated event) {

        String generalMode = sP.getString("generalMode", "");
        ArrayList<Integer> oldList = new ArrayList<>();
        ArrayList<String> duyuruList;
        ArrayList<Integer> firstTimeList = new ArrayList<>();
        ArrayList<Integer> newList = new ArrayList<>();
        Log.i("tuna", "we are on Event Fragment1");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentTime = sdf.format(new Date());
        DuyuruDB db = new DuyuruDB(getActivity());
        List<DuyuruInformation> data = new ArrayList<>();
        DuyuruInformation current = new DuyuruInformation();
        if (generalMode.equals("bolum")) {
            current.imageID = sP.getInt("bolumImg", 0);
            current.bolum = sP.getString("bolumAdi", "");
        } else {
            current.imageID = sP.getInt("fakulteImg", 0);
            current.bolum = sP.getString("fakulteAdi", "");
        }
        data.add(current); // FIRST ADD HEADER*/
        for (int i = 1; i < db.getDuyuruSayisi(generalMode) + 1; i++) { // ADDING NEW DUYURU IF THERE IS
            duyuruList = new ArrayList<>();
            duyuruList.addAll(db.fetchMeMyDuyuru(i, generalMode)); // coz db reads _id as normal people, i starts from 1, BECAREFUL!!
            if (duyuruList.get(5).equals("firstTime")) {
                Log.i("tuna", "this is a firstTime post = " + duyuruList.get(0));
                firstTimeList.add(i);
                continue;
            } else if (duyuruList.get(5).equals("old")) {
                Log.i("tuna", "this is an old post = " + duyuruList.get(0));
                oldList.add(i);
                continue;
            }
            // so what we add here is basically the "new" items
            Log.i("tuna", "this is a new post = " + duyuruList.get(0));
            newList.add(i);
                /*current = new DuyuruInformation();
                current.header = duyuruList.get(0);
                current.body = duyuruList.get(1);
                current.dateDiff = getTimeDifference(
                        duyuruList.get(2),
                        currentTime
                );
                data.add(current);*/
        }
        for (int i = newList.size() - 1; i >= 0; i--) { // ADDING OLD DUYURU IF THERE IS
            duyuruList = new ArrayList<>();
            duyuruList.addAll(db.fetchMeMyDuyuru(newList.get(i), generalMode)); // coz db reads _id as normal people, i starts from 1, BECAREFUL!!
            current = new DuyuruInformation();
            Log.i("tuna", "a new post is pulling " + duyuruList.get(0));
            current.header = duyuruList.get(0);
            if (duyuruList.get(1).isEmpty())
                current.body = "Resmi görüntülemek için týklayýn";
            else
                current.body = duyuruList.get(1);
            current.dateDiff = getTimeDifference(
                    duyuruList.get(2),
                    currentTime
            );
            data.add(current);
        }

        for (int i = 0; i < firstTimeList.size(); i++) { // ADDING OLD DUYURU IF THERE IS
            duyuruList = new ArrayList<>();
            duyuruList.addAll(db.fetchMeMyDuyuru(firstTimeList.get(i), generalMode)); // coz db reads _id as normal people, i starts from 1, BECAREFUL!!
            current = new DuyuruInformation();
            Log.i("tuna", "a firstTime post is pulling " + duyuruList.get(0));
            current.header = duyuruList.get(0);
            if (duyuruList.get(1).isEmpty())
                current.body = "Resmi görüntülemek için týklayýn";
            else
                current.body = duyuruList.get(1);
            current.dateDiff = getTimeDifference(
                    duyuruList.get(2),
                    currentTime
            );
            data.add(current);
        }


        for (int i = 0; i < oldList.size(); i++) { // ADDING OLD DUYURU IF THERE IS
            duyuruList = new ArrayList<>();
            duyuruList.addAll(db.fetchMeMyDuyuru(oldList.get(i), generalMode)); // coz db reads _id as normal people, i starts from 1, BECAREFUL!!
            current = new DuyuruInformation();
            Log.i("tuna", "an old post is pulling " + duyuruList.get(0));
            current.header = duyuruList.get(0);
            if (duyuruList.get(1).isEmpty())
                current.body = "Resmi görüntülemek için týklayýn";
            else
                current.body = duyuruList.get(1);
            current.dateDiff = getTimeDifference(
                    duyuruList.get(2),
                    currentTime
            );
            data.add(current);
        }

        swipeLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        dataSize = data.size();
        if (event.mode == "updating" || event.mode == "firstTime") {
            final List<DuyuruInformation> data2 = data;
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    adapter = new DuyuruAdapter(getActivity(), data2, new DuyuruAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            handleCardClicks(view, position);
                        }
                    });
                    //adapter.notifyItemInserted(data2.size()-1);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }
            };
            getActivity().runOnUiThread(r);
        } else {
            // if user has downloaded history, scroll to down
            final List<DuyuruInformation> data2 = data;
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    adapter = new DuyuruAdapter(getActivity(), data2, new DuyuruAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            handleCardClicks(view, position);
                        }
                    });
                    //adapter.notifyItemInserted(data2.size()-1);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                    recyclerView.scrollToPosition(data2.size() - 6);
                }
            };
            getActivity().runOnUiThread(r);
        }
        setFabToReady();
    }

    public void handleCardClicks(View view, int position) {
        Intent i = new Intent(getActivity(), DuyuruDetailedActivity.class);
        i.putExtra("pos", position);
        /*Runnable r = new Runnable() {
            @Override
            public void run() {
                adapter = new DuyuruAdapter(getActivity(), getData(), new DuyuruAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        handleCardClicks(view, position);
                    }
                });
                //adapter.notifyItemInserted(data2.size()-1);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }
        };
        getActivity().runOnUiThread(r);
        */
        startActivity(i);

    }

    public void onEvent(LoadOldDuyuru a) {
        eskiDuyuruYukle();
    }

    public void onEvent(ReportingThreadNumbers event) {
        countOfItemsToBeReported = event.loaded_item_count;
        Log.i("tuna", "countOfItemsToBeReported= " + countOfItemsToBeReported);
    }


    public void onEvent(ThreadResult event) {
        if (event.message.equals("ioException")) {
            failedThreadsSoFar++;
            Log.i("tuna", "failedThreadsSoFar= " + failedThreadsSoFar);
        }
        reportedThreadsSoFar++;
        Log.i("tuna", "reportedThreadsSoFar= " + reportedThreadsSoFar);
        if (reportedThreadsSoFar == countOfItemsToBeReported) {
            countOfItemsToBeReported = 0;
            reportedThreadsSoFar = 0;
            failedThreadsSoFar = 0;
            Log.i("tuna", "all loading is completed");
            Log.i("tuna", "user is on loading screen, start load is fired");
            if (failedThreadsSoFar > 0) {
                Toast.makeText(getActivity(), "Hata kodu #01 --- bu mesajý görürseniz lütfen geliþtiriciye ulaþýn!", Toast.LENGTH_LONG).show();
                //bus.post(new StatusForDetailedActivity("exception"));
            } else {
                //bus.post(new StatusForDetailedActivity("goodToGo"));
            }
            setFabToReady();
        }
    }

    public void onEvent(ExceptionerResult event) {
        if (event.message == "goodToGo") {
            bus.post(new StatusForDetailedActivity("goodToGo"));
        } else {
            bus.post(new StatusForDetailedActivity("exception"));
        }
    }


    public void onEvent(IOExceptionInDuyuruTaskForFirstTime event) {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        swipeLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        reload.setVisibility(View.VISIBLE);
        reloadText.setVisibility(View.VISIBLE);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTryAgainToConnect();
            }
        });
    }

    private void handleTryAgainToConnect() {
        LoadDuyuruForFirstTime();
        swipeLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        reload.setVisibility(View.GONE);
        reloadText.setVisibility(View.GONE);
    }

}

class StatusForDetailedActivity {
    public String message;

    public StatusForDetailedActivity(String message) {
        this.message = message;
    }
}



