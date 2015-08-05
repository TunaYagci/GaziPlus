package com.hp2m.newsupportlibrary22;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
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
    SwipeRefreshLayout swipeLayout;
    FrameLayout motherLayout;
    ProgressBar progressBar;
    private int failedThreadsSoFar;

    public Fragment1() {

    }

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

    private static boolean doesDatabaseExist(ContextWrapper context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
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

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
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
        swipeLayout.setColorSchemeColors(R.color.my_primary_dark);

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


        /*
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean hideToolBar = false;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (hideToolBar) {
                    ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
                } else {
                    ((AppCompatActivity)getActivity()).getSupportActionBar().show();
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
*/
        return rootView;
    }

    public List<DuyuruInformation> getData() {
        DuyuruDB db = new DuyuruDB(getActivity());
        // check if db exists and "up-to-date"
        // check if db up to date PLEASEEEE
        //if (doesTableExist(db.getReadableDatabase(), db.TABLE_DUYURU)) {
        if (doesDatabaseExist(getActivity(), db.getDatabaseName())) { // LATER ON CHANGE THIS, thats very SLOWW
            ArrayList<Integer> oldList = new ArrayList<>();
            ArrayList<String> duyuruList = new ArrayList<>();
            Log.i("tuna", "we are on getData Fragment1");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String currentTime = sdf.format(new Date());
            //DuyuruDB db = new DuyuruDB(getActivity(), null, null, sharedPreferences.getInt("version", 1));
            List<DuyuruInformation> data = new ArrayList<>();
            DuyuruInformation current = new DuyuruInformation();
            current.imageID = R.drawable.lowres2;
            current.bolum = "CENGAZÝ";
            data.add(current); // FIRST, ADD HEADER
            for (int i = 1; i < db.getDuyuruSayisi(); i++) { // ADDING NEW DUYURU IF THERE IS
                duyuruList = new ArrayList<>();
                duyuruList.addAll(db.fetchMeMyDuyuru(i)); // coz db reads _id as normal people, i starts from 1, BECAREFUL!!
                if (duyuruList.get(5) == "old") {
                    oldList.add(i);
                    continue;
                }
                current = new DuyuruInformation();
                current.header = duyuruList.get(0);
                current.body = duyuruList.get(1);
                current.dateDiff = getTimeDifference(
                        duyuruList.get(2),
                        currentTime
                );
                data.add(current);
            }
            dataSize = data.size();
            for (int i = 0; i < oldList.size(); i++) { // ADDING OLD DUYURU IF THERE IS
                duyuruList = new ArrayList<>();
                duyuruList.addAll(db.fetchMeMyDuyuru(i)); // coz db reads _id as normal people, i starts from 1, BECAREFUL!!
                current = new DuyuruInformation();
                current.header = duyuruList.get(0);
                current.body = duyuruList.get(1);
                current.dateDiff = getTimeDifference(
                        duyuruList.get(2),
                        currentTime
                );
                data.add(current);
            }
            if (!dataHolder.alreadyShownFragment1) {
                Log.i("tuna", "update method fired");
                duyuruGuncelle();
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
            new DuyuruTask(this, "old").execute();
        } else {
            recyclerView.setAdapter(adapter);
            recyclerView.scrollToPosition(dataSize);
            Snackbar.make(motherLayout, "Ýnternete baðlanýlamýyor", Snackbar.LENGTH_LONG)
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
            new DuyuruTask(this, "updating").execute();
        } else {
            swipeLayout.setRefreshing(false);
            dataHolder.alreadyShownFragment1 = true;
            Snackbar.make(motherLayout, "Ýnternete baðlanýlamýyor", Snackbar.LENGTH_LONG)
                    .setAction("Tekrar Dene", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eskiDuyuruYukle();
                        }
                    })
                    .show();
        }
    }

    public void LoadDuyuruForFirstTime() { // ilk giriþte çaðýr
        if (isNetworkAvailable()) {
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

            Snackbar.make(motherLayout, "Ýnternete baðlanýlamýyor", Snackbar.LENGTH_LONG)
                    .setAction("Tekrar Dene", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eskiDuyuruYukle();
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

    }

    public void onEvent(DuyuruDownloadComplated event) {
        ArrayList<Integer> oldList = new ArrayList<>();
        ArrayList<String> duyuruList = new ArrayList<>();
        Log.i("tuna", "we are on Event Fragment1");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentTime = sdf.format(new Date());
        DuyuruDB db = new DuyuruDB(getActivity());
        List<DuyuruInformation> data = new ArrayList<>();
        DuyuruInformation current = new DuyuruInformation();
        current.imageID = R.drawable.lowres2;
        current.bolum = "CENGAZÝ";
        data.add(current); // FIRST ADD HEADER
        for (int i = 1; i < db.getDuyuruSayisi(); i++) { // ADDING NEW DUYURU IF THERE IS
            duyuruList = new ArrayList<>();
            duyuruList.addAll(db.fetchMeMyDuyuru(i)); // coz db reads _id as normal people, i starts from 1, BECAREFUL!!
            if (duyuruList.get(5) == "old") {
                oldList.add(i);
                continue;
            }
            current = new DuyuruInformation();
            current.header = duyuruList.get(0);
            current.body = duyuruList.get(1);
            current.dateDiff = getTimeDifference(
                    duyuruList.get(2),
                    currentTime
            );
            data.add(current);
        }

        for (int i = 0; i < oldList.size(); i++) { // ADDING OLD DUYURU IF THERE IS
            duyuruList = new ArrayList<>();
            duyuruList.addAll(db.fetchMeMyDuyuru(i)); // coz db reads _id as normal people, i starts from 1, BECAREFUL!!
            current = new DuyuruInformation();
            current.header = duyuruList.get(0);
            current.body = duyuruList.get(1);
            current.dateDiff = getTimeDifference(
                    duyuruList.get(2),
                    currentTime
            );
            data.add(current);
        }
        dataSize = data.size();

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
        if (event.message == "ioException") {
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
            if (failedThreadsSoFar > 0)
                bus.post(new StatusForDetailedActivity("exception"));
            else
                bus.post(new StatusForDetailedActivity("goodToGo"));
        }
    }


    public void onEvent(ExceptionerResult event) {
        if (event.message == "goodToGo") {
            bus.post(new StatusForDetailedActivity("goodToGo"));
        } else {
            bus.post(new StatusForDetailedActivity("exception"));
        }
    }
}

class StatusForDetailedActivity {
    public String message;

    public StatusForDetailedActivity(String message) {
        this.message = message;
    }
}



