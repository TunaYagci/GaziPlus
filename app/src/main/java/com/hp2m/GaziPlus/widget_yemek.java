package com.hp2m.GaziPlus;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import java.util.Calendar;

import de.greenrobot.event.EventBus;

/**
 * Implementation of App Widget functionality.
 */
public class widget_yemek extends AppWidgetProvider {
    final EventBus bus = EventBus.getDefault();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        String gun, yemek1, yemek2, yemek3, yemek4;

        YemekDB db = new YemekDB(context);
        if (db.getYemekSayisi() != 0) {
            int day = giveMeTheDay();
            if (day != 6){ // the value "6" returns the weekends
                gun = db.fetchMeMyFood(day).get(0);
                yemek1 = db.fetchMeMyFood(day).get(1);
                yemek2 = db.fetchMeMyFood(day).get(2);
                yemek3 = db.fetchMeMyFood(day).get(3);
                yemek4 = db.fetchMeMyFood(day).get(4);
            }
            else {
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

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

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

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

        int minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        /*int maxWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int maxHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
*/

        //Log.i("tuna", minWidth + " " + maxWidth + " " + minHeight + " " + maxHeight);

        int unit = 3;
        float size = 8f;

        switch (minWidth){
            case 128:
                unit = 3;
                size = 7f;
                break;
            case 200:
                unit = 3;
                break;
            case 272:
                unit = 3;
                size = 9f;
                break;
            case 344:
                unit = 3;
                size = 9f;
                break;
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
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

