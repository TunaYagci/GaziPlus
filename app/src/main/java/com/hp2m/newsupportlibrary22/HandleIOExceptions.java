package com.hp2m.newsupportlibrary22;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Tuna on 7/31/2015.
 */
public class HandleIOExceptions {

    private Context context;

    public HandleIOExceptions(Context context) {
        this.context = context;
    }

    public int main(int row) {

        for (int i = 0; i < 4; i++) {
            Intent intent = null;
            if (i == 0)
                intent = new Intent(context, DownloadService1.class);
            else if (i == 1)
                intent = new Intent(context, DownloadService2.class);
            else if (i == 2)
                intent = new Intent(context, DownloadService3.class);
            else if (i == 3)
                intent = new Intent(context, DownloadService4.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
            if (pendingIntent == null) {
                DuyuruExceptionDB db2 = new DuyuruExceptionDB(context);
                Intent intent2 = null;
                if (i == 0)
                    intent2 = new Intent(context, DownloadService1.class);
                else if (i == 1)
                    intent2 = new Intent(context, DownloadService2.class);
                else if (i == 2)
                    intent2 = new Intent(context, DownloadService3.class);
                else if (i == 3)
                    intent2 = new Intent(context, DownloadService4.class);
                intent2.putExtra("header", db2.fetchFailedDuyuru(row).get(0));
                intent2.putExtra("link", db2.fetchFailedDuyuru(row).get(1));
                intent2.putExtra("exceptioner", true);
                context.startService(intent2);
                return i;
            }
        }
        // if no intentService is available, let 3 work;
        DuyuruExceptionDB db2 = new DuyuruExceptionDB(context);
        Intent intent2 = new Intent(context, DownloadService4.class);
        intent2.putExtra("header", db2.fetchFailedDuyuru(row).get(0));
        intent2.putExtra("link", db2.fetchFailedDuyuru(row).get(1));
        intent2.putExtra("exceptioner", true);
        context.startService(intent2);
        return 3;
    }
}
