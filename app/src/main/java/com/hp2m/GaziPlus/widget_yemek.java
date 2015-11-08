package com.hp2m.GaziPlus;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Calendar;

import de.greenrobot.event.EventBus;

/**
 * Implementation of App Widget functionality.
 */
public class widget_yemek extends AppWidgetProvider {
    private static final String SYNC_CLICK = "myOnClickTag";
    private static final String MAIN_CLICK = "onGlick";
    private static Context context;
    final EventBus bus = EventBus.getDefault();

    private static int giveMeTheDay() {
        // scroll to day
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.MONDAY:
                return 1;
            case Calendar.TUESDAY:
                return 2;
            case Calendar.WEDNESDAY:
                return 3;
            case Calendar.THURSDAY:
                return 4;
            case Calendar.FRIDAY:
                return 5;
            default:
                return 6;

        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        this.context = context;
        if (SYNC_CLICK.equals(intent.getAction())) {
            //your onClick action is here
            if (isNetworkAvailable(context)) {
                Toast.makeText(context, "Güncelleniyor...", Toast.LENGTH_SHORT).show();
                // update here
                YemekDB db = new YemekDB(context);
                if (db.getYemekSayisi() != 0) {
                    // yemek güncelle
                    this.context = context;
                    bus.register(this);
                    new YemekTask(context, true).execute();
                } else {
                    Toast.makeText(context, "Ýlk baþta uygulamaya girip yemek listesini yükleyin", Toast.LENGTH_SHORT).show();
                }


            } else {
                Toast.makeText(context, "Ýnternete eriþilemedi", Toast.LENGTH_SHORT).show();
                String gun, yemek1, yemek2, yemek3, yemek4;

                YemekDB db = new YemekDB(context);
                if (db.getYemekSayisi() != 0) {
                    int day = giveMeTheDay();
                    if (day != 6) { // the value "6" returns the weekends
                        gun = db.fetchMeMyFood(day).get(0);
                        yemek1 = db.fetchMeMyFood(day).get(1);
                        yemek2 = db.fetchMeMyFood(day).get(2);
                        yemek3 = db.fetchMeMyFood(day).get(3);
                        yemek4 = db.fetchMeMyFood(day).get(4);
                    } else {
                        gun = db.fetchMeMyFood(5).get(0);
                        yemek1 = db.fetchMeMyFood(5).get(1);
                        yemek2 = db.fetchMeMyFood(5).get(2);
                        yemek3 = db.fetchMeMyFood(5).get(3);
                        yemek4 = db.fetchMeMyFood(5).get(4);
                    }

                } else {
                    gun = "Yemek listesi bulunamadý";
                    yemek1 = "-";
                    yemek2 = "-";
                    yemek3 = "-";
                    yemek4 = "-";
                }

                // Construct the RemoteViews object
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_yemek);
                views.setTextViewText(R.id.widgetyemek_gun, gun);
                views.setTextViewText(R.id.widgetyemek_1, yemek1);
                views.setTextViewText(R.id.widgetyemek_2, yemek2);
                views.setTextViewText(R.id.widgetyemek_3, yemek3);
                views.setTextViewText(R.id.widgetyemek_4, yemek4);
            }
        } else if (MAIN_CLICK.equals(intent.getAction())) {

            try {
                Intent mainIntent = new Intent(context, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mainIntent.putExtra("widgetYemek", true);
                context.startActivity(mainIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateFromDB(Context context) {
        String gun, yemek1, yemek2, yemek3, yemek4;
        YemekDB db = new YemekDB(context);
        if (db.getYemekSayisi() != 0) {
            int day = giveMeTheDay();
            if (day != 6) { // the value "6" returns the weekends
                gun = db.fetchMeMyFood(day).get(0);
                yemek1 = db.fetchMeMyFood(day).get(1);
                yemek2 = db.fetchMeMyFood(day).get(2);
                yemek3 = db.fetchMeMyFood(day).get(3);
                yemek4 = db.fetchMeMyFood(day).get(4);
            } else {
                gun = db.fetchMeMyFood(5).get(0);
                yemek1 = db.fetchMeMyFood(5).get(1);
                yemek2 = db.fetchMeMyFood(5).get(2);
                yemek3 = db.fetchMeMyFood(5).get(3);
                yemek4 = db.fetchMeMyFood(5).get(4);
            }

        } else {
            gun = "Yemek listesi bulunamadý";
            yemek1 = "-";
            yemek2 = "-";
            yemek3 = "-";
            yemek4 = "-";
        }

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_yemek);
        views.setTextViewText(R.id.widgetyemek_gun, gun);
        views.setTextViewText(R.id.widgetyemek_1, yemek1);
        views.setTextViewText(R.id.widgetyemek_2, yemek2);
        views.setTextViewText(R.id.widgetyemek_3, yemek3);
        views.setTextViewText(R.id.widgetyemek_4, yemek4);

        ComponentName thisWidget = new ComponentName(context, widget_yemek.class);
        AppWidgetManager.getInstance(context).updateAppWidget(thisWidget, views);

    }

    public void onEvent(YemekDownloadComplated event) {
        if (event.message.equals("goodToGo")) {
            updateFromDB(context);
            Toast.makeText(context, "Yemek listesi güncel", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Güncelleme baþarýsýz :(", Toast.LENGTH_SHORT).show();
        }
        bus.unregister(this);

    }


    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        this.context = context;
        String gun, yemek1, yemek2, yemek3, yemek4;

        YemekDB db = new YemekDB(context);
        if (db.getYemekSayisi() != 0) {
            int day = giveMeTheDay();
            if (day != 6) { // the value "6" returns the weekends
                gun = db.fetchMeMyFood(day).get(0);
                yemek1 = db.fetchMeMyFood(day).get(1);
                yemek2 = db.fetchMeMyFood(day).get(2);
                yemek3 = db.fetchMeMyFood(day).get(3);
                yemek4 = db.fetchMeMyFood(day).get(4);
            } else {
                gun = db.fetchMeMyFood(5).get(0);
                yemek1 = db.fetchMeMyFood(5).get(1);
                yemek2 = db.fetchMeMyFood(5).get(2);
                yemek3 = db.fetchMeMyFood(5).get(3);
                yemek4 = db.fetchMeMyFood(5).get(4);
            }

        } else {
            gun = "Yemek listesi bulunamadý";
            yemek1 = "-";
            yemek2 = "-";
            yemek3 = "-";
            yemek4 = "-";
        }

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_yemek);
        views.setTextViewText(R.id.widgetyemek_gun, gun);
        views.setTextViewText(R.id.widgetyemek_1, yemek1);
        views.setTextViewText(R.id.widgetyemek_2, yemek2);
        views.setTextViewText(R.id.widgetyemek_3, yemek3);
        views.setTextViewText(R.id.widgetyemek_4, yemek4);

        // sync click
        views.setOnClickPendingIntent(R.id.sync_button, getPendingSelfIntent(context, SYNC_CLICK));

        views.setOnClickPendingIntent(R.id.imageView3, getPendingSelfIntent(context, MAIN_CLICK));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            int minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            int maxWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
            int minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
            int maxHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);


            Log.i("tuna", minWidth + " " + maxWidth + " " + minHeight + " " + maxHeight);

            int unit = 3;
            float size = 8f;

            if (minWidth < 200) {
                unit = 3;
                size = 6f;
            } else if (minWidth < 250) {
                unit = 3;
                size = 8f;
            } else if (minWidth < 320) {
                unit = 3;
                size = 9f;
            } else if (minWidth < 400) {
                unit = 3;
                size = 9f;
            }


            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_yemek);

            views.setTextViewTextSize(R.id.widgetyemek_gun, unit, size);
            views.setTextViewTextSize(R.id.widgetyemek_1, unit, size);
            views.setTextViewTextSize(R.id.widgetyemek_2, unit, size);
            views.setTextViewTextSize(R.id.widgetyemek_3, unit, size);
            views.setTextViewTextSize(R.id.widgetyemek_4, unit, size);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }


    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        // Not'u güncellerken buna dikkat edelim
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.i("tuna", "onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

