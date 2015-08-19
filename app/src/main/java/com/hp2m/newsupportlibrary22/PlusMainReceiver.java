package com.hp2m.newsupportlibrary22;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Tuna on 8/17/2015.
 */
public class PlusMainReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("TAG", "onReceive's if and intent is " + intent.toString());
        Intent i = new Intent(context, PlusNotificationService.class);
        context.startService(i);
    }

    public void SetAlarm(Context context) {
        Intent i = new Intent(context, PlusMainReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, 0, 2 * 60 * 1000, pi); // Millisec * Second * Minute
        Log.i("TAG", "alarm is set");


    }

    public void CancelAlarm(Context context) {
        Intent intent = new Intent(context, PlusMainReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        Log.i("TAG", "alarm is canceled");
        Intent i = new Intent(context, PlusMainReceiver.class);
        context.stopService(i);
    }
}
