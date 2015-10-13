package com.hp2m.GaziPlus;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.nostra13.universalimageloader.core.ImageLoader;

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
    public FloatingActionButton fab1, fab2, fab3;
    public FloatingActionMenu fabMenu;
    SwipeRefreshLayout swipeLayout;
    CoordinatorLayout coordinator;
    FrameLayout motherLayout;
    ProgressBar progressBar;
    private int failedThreadsSoFar;
    private SharedPreferences sP;
    private SharedPreferences.Editor editor;
    private FrameLayout dimLayout;
    private boolean onUpdating = false;

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
        // Inflate the layout for this fragment test test2
        // ooo
        // gg
        // uuu
        // --- jnj ed
        // yyy
        final View rootView = inflater.inflate(R.layout.fragment1, container, false);
        bus.register(this);

        dimLayout = (FrameLayout) rootView.findViewById(R.id.dimLayout);
        reload = (ImageButton) rootView.findViewById(R.id.reload);
        reloadText = (TextView) rootView.findViewById(R.id.reloadText);
        motherLayout = (FrameLayout) rootView.findViewById(R.id.fragment1_motherLayout);
        progressBar = (ProgressBar) rootView.findViewById(R.id.loadingBar);

        sP = this.getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = sP.edit();
        fab1 = (FloatingActionButton) rootView.findViewById(R.id.menu_item1);
        fab2 = (FloatingActionButton) rootView.findViewById(R.id.menu_item2);
        fab3 = (FloatingActionButton) rootView.findViewById(R.id.menu_item3);

        fabMenu = (FloatingActionMenu) rootView.findViewById(R.id.fabMenu);
        //fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        coordinator = (CoordinatorLayout) rootView.findViewById(R.id.coordinator2);
        /*if (!sP.getString("bolumHint", "nofab").equals("nofab")) {
            createFabMenu();
        } else {
            fabMenu.setVisibility(View.GONE);
        }*/
        createFabMenu();
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
                if (isNetworkAvailable()) {
                    DuyuruDB db = new DuyuruDB(getActivity());
                    if (db.getDuyuruSayisi(sP.getString("generalMode", "bolum")) > 0) {
                        duyuruGuncelle();
                    } else {
                        swipeLayout.setRefreshing(false);
                        LoadDuyuruForFirstTime();
                    }

                } else {
                    if (sP.getString("generalMode", "bolum").equals("bolum")) {
                        Snackbar.make(coordinator, "Akýþ yenilenemedi", Snackbar.LENGTH_SHORT)
                                .show();
                    } else {
                        Snackbar.make(coordinator, "Akýþ yenilenemedi", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                    swipeLayout.setRefreshing(false);
                }
            }
        });

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        // update duyuru on onCreate
        Log.i("tuna", "onCreate Fragment1");
        adapter = new DuyuruAdapter(getActivity(), getData(true), new DuyuruAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String title) {
                handleCardClicks(view, position, title);
            }
        });
        recyclerView.setAdapter(adapter);
        final LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        //if (!sP.getString("bolumHint", "nofab").equals("nofab")) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                boolean hideToolBar = false;

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (!onUpdating) {
                        if (hideToolBar) {
                            //fabMenu.animate().translationY(330);
                            fabMenu.hideMenuButton(true);
                        } else {
                            //fabMenu.animate().translationY(0);
                            fabMenu.showMenuButton(true);
                        }
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
       // }
        NotificationzDB notificationzDB = new NotificationzDB(getActivity());
        notificationzDB.deleteAllNotificationz();
        return rootView;
    }

    private void createFabMenu() {
        fabMenu.setMenuButtonHideAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_out_bottom));
        fabMenu.setMenuButtonShowAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_in_bottom));

        fabMenu.setClosedOnTouchOutside(true);
        fabMenu.setIconAnimated(true);
        fabMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean b) {
                if (b) {
                    dimLayout.setVisibility(View.VISIBLE);
                    if (sP.getString("generalMode", "bolum").equals("bolum")) {
                        if (sP.getString("bolumHint", "nofab").equals("nofab")) {
                            fab2.setLabelText("Fakülte bildirimleri");
                        }
                        else {
                            fab2.setLabelText("Bölüm bildirimleri");
                        }
                        if (sP.getBoolean("isBolumNotificationsAllowed", false)) {
                            fab2.setImageResource(R.drawable.ic_notifications_active_white_24dp);
                        } else {
                            fab2.setImageResource(R.mipmap.ic_notifications_off_white_24dp);
                        }
                    } else {
                        fab2.setLabelText("Fakülte bildirimleri");
                        if (sP.getBoolean("isFakulteNotificationsAllowed", false)) {
                            fab2.setImageResource(R.drawable.ic_notifications_active_white_24dp);
                        } else {
                            fab2.setImageResource(R.mipmap.ic_notifications_off_white_24dp);
                        }
                    }
                } else {
                    dimLayout.setVisibility(View.GONE);
                }
            }
        });
        if (sP.getString("generalMode", "bolum").equals("bolum")) {
            fab2.setLabelText("Bölüm bildirimleri");
            if (sP.getBoolean("isBolumNotificationsAllowed", false)) {
                fab2.setImageResource(R.drawable.ic_notifications_active_white_24dp);
            } else {
                fab2.setImageResource(R.mipmap.ic_notifications_off_white_24dp);
            }
        } else {
            fab2.setLabelText("Fakülte bildirimleri");
            if (sP.getBoolean("isFakulteNotificationsAllowed", false)) {
                fab2.setImageResource(R.drawable.ic_notifications_active_white_24dp);
            } else {
                fab2.setImageResource(R.mipmap.ic_notifications_off_white_24dp);
            }
        }

        if (sP.getString("bolumHint", "nofab").equals("nofab")) { // gazi iletiþim
                fabMenu.removeMenuButton(fab1);
            fab2.setLabelText("Fakülte bildirimleri");
        } else {
            fab1.setColorNormalResId(R.color.fab_menu_1);
            fab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleMenuFab1Click();
                }
            });
        }

        fab2.setColorNormalResId(R.color.fab_menu_2);
        fab2.setColorPressedResId(R.color.fab_menu_2_pressed);
        fab3.setColorNormalResId(R.color.fab_menu_3);
        fab3.setColorPressedResId(R.color.fab_menu_3_pressed);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleMenuFab2Click();
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleMenuFab3Click();
            }
        });
        fabMenu.hideMenuButton(false);
    }


    private void hideReloadThings() {
        if (reload.getVisibility() == View.VISIBLE) {
            reload.setVisibility(View.GONE);
            reloadText.setVisibility(View.GONE);
        }
    }

    private void handleMenuFab1Click() {
        hideReloadThings();
        fabMenu.close(false);
        handleFabClicks();
        if (sP.getString("generalMode", "bolum").equals("bolum")) {
            fab2.setLabelText(" Bölüm bildirimleri ");
            if (sP.getBoolean("isBolumNotificationsAllowed", false)) {
                fab2.setImageResource(R.drawable.ic_notifications_active_white_24dp);
            } else {
                fab2.setImageResource(R.mipmap.ic_notifications_off_white_24dp);
            }
        } else {
            fab2.setLabelText("Fakülte bildirimleri");
            if (sP.getBoolean("isFakulteNotificationsAllowed", false)) {
                fab2.setImageResource(R.drawable.ic_notifications_active_white_24dp);
            } else {
                fab2.setImageResource(R.mipmap.ic_notifications_off_white_24dp);
            }
        }
    }

    private void handleMenuFab2Click() {
        hideReloadThings();
        boolean wasBothClosed = !sP.getBoolean("isBolumNotificationsAllowed", false) && !sP.getBoolean("isFakulteNotificationsAllowed", false);
        if (sP.getString("generalMode", "bolum").equals("bolum")) {
            if (sP.getBoolean("isBolumNotificationsAllowed", false)) {
                fab2.setImageResource(R.mipmap.ic_notifications_off_white_24dp);
                editor.putBoolean("isBolumNotificationsAllowed", false);
                //editor.apply();
            } else {
                fab2.setImageResource(R.drawable.ic_notifications_active_white_24dp);
                // bildirimleri aç
                editor.putBoolean("isBolumNotificationsAllowed", true);
                //editor.apply();
            }
        } else {
            if (sP.getBoolean("isFakulteNotificationsAllowed", false)) {
                fab2.setImageResource(R.mipmap.ic_notifications_off_white_24dp);
                editor.putBoolean("isFakulteNotificationsAllowed", false);
                //editor.apply();
            } else {
                fab2.setImageResource(R.drawable.ic_notifications_active_white_24dp);
                // bildirimleri aç
                editor.putBoolean("isFakulteNotificationsAllowed", true);
                //editor.apply();
            }
        }
        editor.commit();
        alarmSetterCanceller(wasBothClosed);
    }

    private void alarmSetterCanceller(boolean wasBothClosed) {
        PlusMainReceiver receiver = new PlusMainReceiver();
        if (!sP.getBoolean("isFakulteNotificationsAllowed", false) && !sP.getBoolean("isBolumNotificationsAllowed", false)) {
            // and check if NOT sP is active or not
            receiver.CancelAlarm(getActivity());
            Log.i("gazinotification", "all alarms are cancelled");
        } else if (wasBothClosed) { // and check if NOT sP was online or not, so we don't set alarm twice.
            receiver.SetAlarm(getActivity());
            Log.i("gazinotification", "all alarms are opened");
        }

    }

    public void handleMenuFab3Click() {
        hideReloadThings();
        fabMenu.close(false);
        setFabToLoading();
        Log.i("fab", "onHandleMenu3Click");
        if (isNetworkAvailable()) {
            DuyuruDB db = new DuyuruDB(getActivity());
            db.clearTable(sP.getString("generalMode", "bolum"));
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    adapter = new DuyuruAdapter(getActivity(), getData(false), new DuyuruAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position, String title) {
                            handleCardClicks(view, position, title);
                        }
                    });
                    //adapter.notifyItemInserted(data2.size()-1);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }
            };
            getActivity().runOnUiThread(r);
            ImageLoader.getInstance().clearMemoryCache();
            ImageLoader.getInstance().clearDiskCache();
            //LoadDuyuruForFirstTime();
        } else {
            Snackbar.make(coordinator, "Ýnternete baðlanýlamýyor", Snackbar.LENGTH_LONG)
                    .setAction("Tekrar Dene", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            handleMenuFab3Click();
                        }
                    })
                    .show();
        }


    }


    public void setFabToLoading() {
        onUpdating = true;
        fabMenu.setClickable(false);
        //fabMenu.close(false);
        fabMenu.hideMenuButton(true);
        //fab.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.loading_notlar_avatar_2)); // or loading_fab...
    }

    public void setFabToReady() {
        String generalMode = sP.getString("generalMode", "");
        if (generalMode.equals("bolum")) {
            fab1.setImageResource(R.drawable.ic_arrow_forward_white_24dp);
            fab1.setLabelText("Fakülte duyurularý");
        } else {
            fab1.setImageResource(R.drawable.ic_arrow_back_white_24dp);
            fab1.setLabelText(" Bölüm duyurularý ");
        }
        editor.putBoolean("needListUpdate", false);
        editor.commit();
        fabMenu.setClickable(true);
        onUpdating=false;
        fabMenu.showMenuButton(true);
    }

    private void handleFabClicks() {
        DuyuruDB db = new DuyuruDB(getActivity());
        if (!(db.getDuyuruSayisi(sP.getString("generalMode", "")) > 0)) {
            setFabToLoading();
        }
        String defaultGeneralMode = sP.getString("generalMode", "bolum");
        editor.putBoolean("needListUpdate", true);
        if (sP.getString("generalMode", "bolum").equals("bolum")) { // fakülteye gidicez yani
            editor.putString("duyuruLink", sP.getString("defaultFakulteLink", ""));
            editor.putString("generalMode", "fakulte");
        } else { // fakülteden bölüme gidicez
            editor.putString("duyuruLink", sP.getString("defaultBolumLink", ""));
            editor.putString("generalMode", "bolum");
        }
        editor.commit();
        Log.i("tuna", "generalMode is " + sP.getString("generalMode", "bolum"));


        if (db.getDuyuruSayisi(sP.getString("generalMode", "")) > 0) { // not the first time
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    adapter = new DuyuruAdapter(getActivity(), getData(false), new DuyuruAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position, String title) {
                            handleCardClicks(view, position, title);
                        }
                    });
                    //adapter.notifyItemInserted(data2.size()-1);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }
            };
            getActivity().runOnUiThread(r);
            setFabToReady();
        } else {
            if (isNetworkAvailable()) {
                recyclerView.setAlpha(0.5F);
                LoadDuyuruForFirstTime();
            } else {
                if (defaultGeneralMode.equals("bolum")) {
                    editor.putString("duyuruLink", sP.getString("defaultBolumLink", ""));
                } else {
                    editor.putString("duyuruLink", sP.getString("defaultFakulteLink", ""));
                }
                editor.putString("generalMode", defaultGeneralMode);
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


    public List<DuyuruInformation> getData(boolean needUpdate) {
        boolean needUpdateForEmergency = false;
        String generalMode = sP.getString("generalMode", "");
        DuyuruDB db = new DuyuruDB(getActivity());
        // check if db exists and "up-to-date"
        // check if db up to date PLEASEEEE
        //if (doesTableExist(db.getReadableDatabase(), db.TABLE_DUYURU)) {
        //if (doesDatabaseExist(getActivity(), db.getDatabaseName())) { // LATER ON CHANGE THIS, thats very SLOWW
        if (db.getDuyuruSayisi(generalMode) > 0) {
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
                    current.body = "Resmi görüntülemek için dokunun";
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
                    current.body = "Resmi görüntülemek için dokunun";
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
                    current.body = "Resmi görüntülemek için dokunun";
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
                if (!DataHolder.alreadyShownFragment1ForBolum) {
                    if (needUpdateForEmergency) {
                        if (isNetworkAvailable()) {
                            data = Collections.emptyList(); // if there is a static duyuru, clear db and load from stratch
                            Log.i("tuna", "emergency first time method fired");
                            LoadDuyuruForFirstTime();
                        }
                    } else {
                        if(needUpdate)
                        duyuruGuncelle();
                    }
                } else
                    setFabToReady();
            } else {
                if (!DataHolder.alreadyShownFragment1ForFakulte) {
                    if (needUpdateForEmergency) {
                        if (isNetworkAvailable()) {
                            data = Collections.emptyList(); // if there is a static duyuru, clear db and load from stratch
                            Log.i("tuna", "emergency first time method fired");
                            LoadDuyuruForFirstTime();
                        }
                    } else {
                        if(needUpdate)
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
                    reload.setVisibility(View.GONE);
                    reloadText.setVisibility(View.GONE);
                    LoadDuyuruForFirstTime();

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
                current.body = "Resmi görüntülemek için dokunun";
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
                current.body = "Resmi görüntülemek için dokunun";
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
                current.body = "Resmi görüntülemek için dokunun";
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
        final List<DuyuruInformation> data2 = data;
        if (event.mode == "updating" || event.mode == "firstTime") {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    adapter = new DuyuruAdapter(getActivity(), data2, new DuyuruAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position, String title) {
                            handleCardClicks(view, position, title);
                        }
                    });
                    //adapter.notifyItemInserted(data2.size()-1);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                   // new DuyuruAdapter(getActivity(), null, null).update();
                }
            };
            getActivity().runOnUiThread(r);
        } else {
            // if user has downloaded history, scroll to down
            //final List<DuyuruInformation> data2 = data;
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    adapter = new DuyuruAdapter(getActivity(), data2, new DuyuruAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position, String title) {
                            handleCardClicks(view, position, title);
                        }
                    });
                    //adapter.notifyItemInserted(data2.size()-1);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                    //new DuyuruAdapter(getActivity(), null, null).update();
                    recyclerView.scrollToPosition(data2.size() - 6);
                }
            };
            getActivity().runOnUiThread(r);
        }
        setFabToReady();
    }

    public void handleCardClicks(View view, int position, String title) {
        Log.i("tuna", "generalMode in handleCardClick is " + sP.getString("generalMode", "bolum"));
        Intent i = new Intent(getActivity(), DuyuruDetailedActivity.class);
        i.putExtra("title", title);
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
            if (failedThreadsSoFar > 0) {
                Toast.makeText(getActivity(), "Hata kodu #01 --- bu mesajý görürseniz lütfen geliþtiriciye ulaþýn!", Toast.LENGTH_LONG).show();
                //bus.post(new StatusForDetailedActivity("exception"));
            } /*else {
                //bus.post(new StatusForDetailedActivity("goodToGo"));
            }*/
            //setFabToReady();
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



