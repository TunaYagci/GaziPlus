package com.hp2m.GaziPlus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Tuna on 8/17/2015.
 */
public class PlusBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("gazinotification", "device booted, rescheduling alarms");
        // reschedule alarms here

        SharedPreferences sP = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        if (sP.getBoolean("isBolumNotificationsAllowed", false) ||
                sP.getBoolean("isFakulteNotificationsAllowed", false)
            // sP.getBoolean("isBolumNotificationsAllowed", false) put not notifications sP here
                ) {
            PlusMainReceiver receiver = new PlusMainReceiver();
            receiver.SetAlarm(context);
        }
    }
}
