package com.hp2m.newsupportlibrary22;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Tuna on 8/17/2015.
 */
public class PlusBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("tuna", "device booted, rescheduling alarms");
        // reschedule alarms here
    }
}
