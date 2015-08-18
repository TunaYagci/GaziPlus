package com.hp2m.newsupportlibrary22;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * Created by Tuna on 8/17/2015.
 */
public class PlusNotificationService extends IntentService {

    public PlusNotificationService() {
        super("notificationWorker");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PowerManager pm = (PowerManager) getBaseContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        // code here


        wl.release();
    }
}
