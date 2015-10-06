package com.hp2m.GaziPlus;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.goncalves.pugnotification.notification.Load;
import br.com.goncalves.pugnotification.notification.PugNotification;
import de.greenrobot.event.EventBus;


public class PlusNotificationService extends IntentService {

    public PowerManager.WakeLock wl;
    private int updatedBolumDuyuruCount = 0, updatedFakulteDuyuruCount = 0, updatedNotCount = 0;
    private int reportingBolumDuyuruCount = 0, reportingFakulteDuyuruCount = 0;
    private int eventedBolumDuyuruCount = 0, eventedFakulteDuyuruCount = 0;
    private EventBus bus = EventBus.getDefault();
    private List<String> bolumHeaderList, fakulteHeaderList;
    private boolean isBolumReadyToNotificate = false, isFakulteReadyToNotificate = false;
    private SharedPreferences sP;
    private Handler bolumHandler = new Handler(Looper.getMainLooper());
    private Runnable bolumRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Log.i("gazinotification", "bolumRunnable and updatedBolumDuyuruCount is " + updatedBolumDuyuruCount);
                if (updatedBolumDuyuruCount != 0) {
                    if (eventedBolumDuyuruCount == reportingBolumDuyuruCount && eventedBolumDuyuruCount != 0) {
                        if (isFakulteReadyToNotificate || updatedFakulteDuyuruCount == 0) {
                            // fakulte has already ended, or nothing to download
                            createNotification();
                        } else {
                            // fakulte is still downloading
                            isBolumReadyToNotificate = true;
                        }
                    } else {
                        bolumHandler.postDelayed(bolumRunnable, 500);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } // do nothing if no item has downloaded for bolum
    };
    private Handler fakulteHandler = new Handler(Looper.getMainLooper());
    private Runnable fakulteRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Log.i("gazinotification", "fakulteRunnable and updatedFakulteCont is " + updatedFakulteDuyuruCount);
                if (updatedFakulteDuyuruCount != 0) {
                    Log.i("gazinotification", "eventedFakulteDuyuruCount and reportingFakulteDuyuruCount and eventedFakulteDuyuruCount=  "
                            + eventedFakulteDuyuruCount
                            + " "
                    + eventedFakulteDuyuruCount
                            + " "
                    + eventedFakulteDuyuruCount );
                    if (eventedFakulteDuyuruCount == reportingFakulteDuyuruCount && eventedFakulteDuyuruCount != 0) {
                        if (isBolumReadyToNotificate || updatedBolumDuyuruCount == 0) {
                            // bolum has already downloaded, or nothing to download
                            createNotification();
                        } else {
                            // bolum hasn't downloaded yet
                            isFakulteReadyToNotificate = true;
                        }
                    } else {
                        fakulteHandler.postDelayed(fakulteRunnable, 500);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } // do nothing if no item has downloaded for fakulte
    };

    public PlusNotificationService() {
        super("notificationWorker");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bus.register(this);
        Log.i("gazinotification", "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
        Log.i("gazinotification", "onDestroy");
    }

    @Override
    public int onStartCommand(Intent yointent, int flags, int startId) {

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    /*DuyuruDB dbTEST = new DuyuruDB(getApplicationContext());
                    DuyuruGetSet duyuruTEST = new DuyuruGetSet("TEK DERS SINAVLARI",
                            " ",
                            "2015-09-10 15:40",
                            " ",
                            " ",
                            "firstTime",
                            " "
                    );
                    dbTEST.addDuyuru(duyuruTEST, "bolum");*/


                    updatedBolumDuyuruCount = 0;
                    updatedFakulteDuyuruCount = 0;
                    reportingBolumDuyuruCount = 0;
                    reportingFakulteDuyuruCount = 0;
                    eventedFakulteDuyuruCount = 0;
                    eventedBolumDuyuruCount = 0;
                    isBolumReadyToNotificate = false;
                    isFakulteReadyToNotificate = false;
                    bolumHeaderList = new ArrayList<>();
                    fakulteHeaderList = new ArrayList<>();


                    //Toast.makeText(getApplicationContext(), "onHandleIntent", Toast.LENGTH_SHORT).show();
                    if (isNetworkAvailable()) {
                        PowerManager pm = (PowerManager) getBaseContext().getSystemService(Context.POWER_SERVICE);
                        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
                        wl.acquire();
                        List<String> headerList, tarihList;
                        boolean hasAnythingDone = false;
                        int LOADED_ITEM_COUNT = 0;
                        sP = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                        String URL = null, generalMode = "nothingHasDone";
                        //final EventBus bus = EventBus.getDefault();
                        boolean isBolumNotificationsAllowed = sP.getBoolean("isBolumNotificationsAllowed", false);
                        boolean isFakulteNotificationsAllowed = sP.getBoolean("isFakulteNotificationsAllowed", false);
                        //boolean isNotNotificationsAllowed = sP.getBoolean("isNotNotificationsAllowed", false);

                        boolean fakulteUpdate = false;
                        for (int counter = 0; counter < 2; counter++) {
                            LOADED_ITEM_COUNT = 0;
                            if (counter == 0) { // check for bolum duyuru
                                if (isBolumNotificationsAllowed) {
                                    generalMode = "bolum";
                                    URL = sP.getString("defaultBolumLink", "");
                                } else {
                                    if (isFakulteNotificationsAllowed) {
                                        generalMode = "fakulte";
                                        URL = sP.getString("defaultFakulteLink", "");
                                        fakulteUpdate = true;
                                    } else
                                        break;
                                }
                            } else {
                                if (!fakulteUpdate) {
                                    if (isFakulteNotificationsAllowed) {
                                        generalMode = "fakulte";
                                        URL = sP.getString("defaultFakulteLink", "");
                                    } else
                                        break;
                                }
                            }
                            if (generalMode.equals("nothingHasDone"))
                                break;


                            DuyuruDB db2 = new DuyuruDB(getApplicationContext());
                            if (db2.getDuyuruSayisi(generalMode) > 0) {
                                Log.i("gazinotification", "generalMode is " + generalMode + " and count is " + db2.getDuyuruSayisi(generalMode));

                                //doTestDelete();


                                Document doc;
                                try {
                                    int timeout = 0;
                                    doc = Jsoup.connect(URL).timeout(timeout).get(); // biiiig timeoouuuut
                                } catch (IOException e) {
                                    Log.i("gazinotification", "timeout done");
                                    Log.i("gazinotification", e.toString());
                                    //wl.release();
                                    dropWakeLock();
                                    return;
                                }
                                Log.i("gazinotification", "doc downloaded");
                                Elements duyuruHeaderElements = doc.select("div.app-content li a[href]");

                                headerList = new ArrayList<>();
                                tarihList = new ArrayList<>();


                                final int DB_MAX_DUYURU = db2.getDuyuruSayisi(generalMode);
                                final int NET_MAX_DUYURU = duyuruHeaderElements.size() - 4;
                                final int MIN_ITEM_TO_LOAD = 4;

                               // final int DB_MAX_POSSIBLE_DUYURU = DB_MAX_DUYURU;
                               // Log.i("gazinotification", "DB_MAX_DUYURU = " + DB_MAX_DUYURU + " and DB_MAX_POSSIBLE_DUYURU = " + DB_MAX_POSSIBLE_DUYURU);

                                ArrayList<Integer> updateList = new ArrayList<>();
                                //checking db if this element is new or not
                                int updatedFirstRow = -1;
                                int updatedSecondRow = -1;
                                // check if user has loaded "any" new item
                                for (int j = DB_MAX_DUYURU; j >= 1; j--) {
                                    //Log.i("gazinotification", "last item is " + db2.fetchMeMyDuyuru(j, generalMode).get(0));
                                    // checking from last item for "new" duyuru
                                    if (db2.fetchMeMyDuyuru(j, generalMode).get(5).equals("new")) {
                                        if( updatedFirstRow!=-1) {
                                            updatedSecondRow = j;
                                            break;
                                        }
                                        updatedFirstRow = j;
                                    }
                                }
                                if (updatedFirstRow == -1) {
                                    Log.i("gazinotification", "user hasnt loaded any new item, check for firstTime items");
                                    // user hasnt loaded any new item, check for firstTime items
                                    for (int i = 0; i < NET_MAX_DUYURU; i++) {
                                        //Log.i("gazinotification", "duyuruHeaderElementsGet(i) = " + duyuruHeaderElements.get(i).text().substring(17));
                                        //Log.i("gazinotification", "db2.fetchMeMyDuyuru(1, generalMode).get(0) = " + db2.fetchMeMyDuyuru(1, generalMode).get(0));
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
                                    Log.i("gazinotification", "user HAS loaded new items, compare with new items");
                                    // user HAS loaded new items
                                    for (int i = 0; i < NET_MAX_DUYURU; i++) {
                                        // check if item is same or not
                                        Log.i("gazinotification", db2.fetchMeMyDuyuru(updatedFirstRow, generalMode).get(0));
                                        Log.i("gazinotification", duyuruHeaderElements.get(i).text().substring(17));
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
                                    Log.i("gazinotification", "no items to load");
                                    // wl.release();
                                    // return;
                                } else {
                                    hasAnythingDone = true;
                                    LOADED_ITEM_COUNT = updateList.size();
                                    //bus.post(new ReportingThreadNumbers(LOADED_ITEM_COUNT));
                                    if (generalMode.equals("bolum")) {
                                        reportingBolumDuyuruCount = LOADED_ITEM_COUNT;
                                    } else if (generalMode.equals("fakulte")) {
                                        reportingFakulteDuyuruCount = LOADED_ITEM_COUNT;
                                        Log.i("gazinotification", "reportingFakulteDuyuruCount= " + reportingFakulteDuyuruCount);
                                    }
                                    DuyuruDB db = new DuyuruDB(getApplicationContext());

                                    for (int i = updateList.size() - 1; i > -1; i--) {
                                        if (generalMode.equals("bolum")) {
                                            bolumHeaderList.add(duyuruHeaderElements.get(i).text().substring(17));
                                        } else {
                                            fakulteHeaderList.add(duyuruHeaderElements.get(i).text().substring(17));
                                        }
                                        headerList.add(duyuruHeaderElements.get(i).text().substring(17));
                                        tarihList.add(duyuruHeaderElements.get(i).text().substring(0, 16));
                                    }
                                    Log.i("gazinotification", "gonna add " + LOADED_ITEM_COUNT + " item as " + generalMode);
                                    for (int i2 = 0; i2 < LOADED_ITEM_COUNT; i2++) {
                                        Log.i("gazinotification", "adding " + headerList.get(i2) + " to DuyuruDB");
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
                                        Log.i("gazinotification", "in intentFirer");
                                        //headerList.add(duyuruHeaderElements.get(i).text().substring(17));
                                        //tarihList.add(duyuruHeaderElements.get(i).text().substring(0, 16));
                                        // look idk if loaded item count > 4 RIGHT NOW !!
                                        // its gonna downloaded in downloadservice1, 5th
                                        Intent intent = null;
                                        if (i == 0 || i == 4)
                                            intent = new Intent(getApplicationContext(), DownloadService1.class);
                                        else if (i == 1 || i == 5)
                                            intent = new Intent(getApplicationContext(), DownloadService2.class);
                                        else if (i == 2 || i == 6)
                                            intent = new Intent(getApplicationContext(), DownloadService3.class);
                                        else if (i == 3 || i == 7)
                                            intent = new Intent(getApplicationContext(), DownloadService4.class);
                                        intent.putExtra("header", duyuruHeaderElements.get(i).text().substring(17));
                                        intent.putExtra("generalModeForNotificationz", generalMode);
                                        intent.putExtra("link", duyuruHeaderElements.get(i).attr("abs:href"));
                                        getApplicationContext().startService(intent);
                                    }
                                }
                                if (generalMode.equals("bolum")) {
                                    updatedBolumDuyuruCount += LOADED_ITEM_COUNT;
                                    //bolumHandler.postDelayed(bolumRunnable, 300);
                                } else if (generalMode.equals("fakulte")) {
                                    updatedFakulteDuyuruCount += LOADED_ITEM_COUNT;
                                    //fakulteHandler.postDelayed(fakulteRunnable, 300);
                                }
                            } else {
                                Log.i("gazinotification", "db hasn't opened yet for " + generalMode);
                            }
                            generalMode = "nothingHasDone";

                        }
                        bolumHandler.postDelayed(bolumRunnable, 300);
                        fakulteHandler.postDelayed(fakulteRunnable, 300);
                        if (!hasAnythingDone) {
                            //wl.release();
                            dropWakeLock();
                        }
                    } else {
                        Log.i("gazinotification", "no internet connection, closing service");
                    }

                } catch (Exception e) {
                    Log.i("gazinotification", "exception in notification service " + e.toString());
                    try {
                        Toast.makeText(getApplicationContext(), "Hata kodu #003", Toast.LENGTH_SHORT).show();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        Log.i("gazinotification", e1.toString());
                    }
                    dropWakeLock();
                }
            }

        };
        Thread a = new Thread(r);
        a.start();


        return START_STICKY;
    }

    private void doTestDelete() {
        DuyuruDB db = new DuyuruDB(getApplicationContext());
        db.deleteForTestingUpdate("2015-2016 Eðitim-Öðretim Yýlý Akademik Takvimi");
    }

    @Override
    protected void onHandleIntent(Intent generalIntent) {

    }

    private void createNotification() {


        try {
            Log.i("gazinotification", "createNotification");
            NotificationzDB db = new NotificationzDB(getApplicationContext());
            for (int i = 0; i < bolumHeaderList.size(); i++) {
                db.addNotification(bolumHeaderList.get(i), "bolum");
            }
            for (int i = 0; i < fakulteHeaderList.size(); i++) {
                db.addNotification(fakulteHeaderList.get(i), "fakulte");
            }
            bolumHeaderList.clear();
            fakulteHeaderList.clear();

            for (int i = db.getBildirimSayisi("bolum"); i > 0; i--) {
                if (db.fetchMeNotificationz(i, "bolum").equals("none"))
                    break;
                bolumHeaderList.add(db.fetchMeNotificationz(i, "bolum"));
            }

            for (int i = db.getBildirimSayisi("fakulte"); i > 0; i--) {
                if (db.fetchMeNotificationz(i, "fakulte").equals("none"))
                    break;
                fakulteHeaderList.add(db.fetchMeNotificationz(i, "fakulte"));
            }
            Log.i("gazinotification", "bolumHeaderList size is " + bolumHeaderList.size());

            // then add them to DB to retrieve later
            // done. ---WE NEED TO DELETE ALL TABLES OF THIS DB WHENEVER MAINACTIVITY IS OPENED !!!


            int size = bolumHeaderList.size() + fakulteHeaderList.size();
            Log.i("gazinotification", "size is " + size);
            if (size > 1) {

                /*Intent resultIntent = new Intent(this, MainActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(MainActivity.class);
    // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );*/

                Load mLoad = PugNotification.with(this).load()
                        .smallIcon(R.drawable.ic_stat_testlogo2_foricon)
                        .autoCancel(true)
                        .click(MainActivity.class)
                        .largeIcon(R.mipmap.ic_launcher)
                        .title("Gazi+")
                        .message("Yeni duyurular")
                        .flags(Notification.DEFAULT_ALL);


                String[] hey = new String[size];
                List<String> notificationList = new ArrayList<>();

                for (int i = 0; i < bolumHeaderList.size(); i++) {
                    notificationList.add(bolumHeaderList.get(i));
                }
                for (int i = 0; i < fakulteHeaderList.size(); i++) {
                    notificationList.add(fakulteHeaderList.get(i));
                }

                // then rewrite here
                if (bolumHeaderList.size() == 0 && fakulteHeaderList.size() != 0) {
                    mLoad.inboxStyle(notificationList.toArray(hey),
                            "Gazi+",
                            fakulteHeaderList.size() + " yeni fakülte duyurusu")
                            .simple()
                            .build();
                } else if (fakulteHeaderList.size() == 0 && bolumHeaderList.size() != 0) {
                    mLoad.inboxStyle(notificationList.toArray(hey),
                            "Gazi+",
                            bolumHeaderList.size() + " yeni bölüm duyurusu")
                            .simple()
                            .build();
                } else if (fakulteHeaderList.size() != 0 && bolumHeaderList.size() != 0) {
                    mLoad.inboxStyle(notificationList.toArray(hey),
                            "Gazi+",
                            bolumHeaderList.size() + " yeni bölüm ve " +
                                    fakulteHeaderList.size() + " fakülte duyurusu")
                            .simple()
                            .build();
                }
            } else {
                String title = null, generalMode = null;
                if (bolumHeaderList.size() == 1) {
                    title = bolumHeaderList.get(0);
                    generalMode = "bolum";
                } else {
                    title = fakulteHeaderList.get(0);
                    generalMode = "fakulte";
                    SharedPreferences.Editor editor = sP.edit();
                    editor.putString("generalMode", "fakulte");
                    editor.commit();
                }
                DuyuruDB duyuruDB = new DuyuruDB(getApplicationContext());
                int position = duyuruDB.fetchMeDuyuruPosition(title, generalMode);
                StringBuilder stringBuilder = new StringBuilder();
                Log.i("gazinotification", "save generalMode is " + generalMode);
                stringBuilder.append(duyuruDB.fetchMeMyDuyuru(position, generalMode).get(1).trim());
                if (stringBuilder.toString().trim().isEmpty()) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Resmi görüntülemek için týklayýn");
                }

                Intent resultIntent = new Intent(this, DuyuruDetailedActivity.class);
                resultIntent.putExtra("title", title);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(MainActivity.class);
    // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                Load mLoad = PugNotification.with(this).load()
                        .smallIcon(R.drawable.ic_stat_testlogo2_foricon)
                        .autoCancel(true)
                        .click(resultPendingIntent)
                        .largeIcon(R.mipmap.ic_launcher)
                        .title("Gazi+")
                        .message("Yeni duyuru")
                        .flags(Notification.DEFAULT_ALL);

                mLoad.bigTextStyle(stringBuilder.toString(), title)
                        .simple()
                        .build();


            }

            // and, at last, save the list to retrieve later for updating
            //wl.release();
            dropWakeLock();
            // save those to db and retrieve for upcoming notifications
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void onEvent(ThreadResult event) {
        try {
            if (event.generalMode.equals("bolum")) {
                eventedBolumDuyuruCount++;
            } else if (event.generalMode.equals("fakulte")) {
                eventedFakulteDuyuruCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void dropWakeLock() {
        try {
            // sanity check for null as this is a public method
            if (wl != null) {
                Log.v("gazinotification", "Releasing wakelock");
                try {
                    wl.release();
                } catch (Throwable th) {
                    // ignoring this exception, probably wakeLock was already released
                }
            } else {
                // should never happen during normal workflow
                Log.e("gazinotification", "Wakelock reference is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
