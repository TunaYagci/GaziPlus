package com.hp2m.newsupportlibrary22;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;


public class DuyuruDetailedPageTask extends AsyncTask<Void, Void, Void> {

    Activity activity;
    String url;
    String cookie;

    public DuyuruDetailedPageTask(Activity activity, String url) {
        this.activity = activity;
        this.url = url;
    }


    @Override
    protected Void doInBackground(Void... params) {
        // Log.i("tuna", "on doInBackground");
        try {

            Uri source = Uri.parse(url);
            //Log.i("tuna", "uri parsed");
            DownloadManager.Request request = new DownloadManager.Request(source);
            // Log.i("tuna", "request sourced");


            //Log.i("tuna", "hasCookies? "+ CookieManager.getInstance().hasCookies());
            //cookie = getCookie(url1);
            //cookie = CookieManager.getInstance().getCookie(url1);
            // Log.i("tuna", "cookie get");

            //request.addRequestHeader("Set-Cookie", cookie);
            /*request.addRequestHeader("User-Agent", ((DuyuruDetailedActivity)activity).header.getSettings().getUserAgentString());
            request.addRequestHeader("Accept", "text/html, application/xhtml+xml, *" + "/" + "*");
            request.addRequestHeader("Accept-Language", "en-US,en;q=0.7,he;q=0.3");
            request.addRequestHeader("Referer", url2);
            */

// Use the same file name for the destination
            /*final File destinationDir = new File(Environment.getExternalStorageDirectory(), activity.getPackageName());

            if (!destinationDir.exists()) {
                destinationDir.mkdir(); // Don't forget to make the directory if it's not there
            }

            File destinationFile = new File(destinationDir, "Gazi+ indirmesi");
            Log.i("tuna ", Uri.fromFile(destinationFile).toString());
            */
            // Log.i("tuna", "cookie set");
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            dir.mkdirs();
            ///Log.i("tuna", "adding file");
            //request.setDestinationUri(Uri.fromFile(dir));
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "GaziPlus_Download");
            //Log.i("tuna", "request set destination folder");
            request.setVisibleInDownloadsUi(true);
            // Log.i("tuna", "visibility set");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                request.setShowRunningNotification(true);
            } else {
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }

            // Add it to the manager
            // Log.i("tuna", "about to add to manager");
            ((DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
            // Log.i("tuna", "added");
        } catch (Exception e) {
            //    Log.i("tuna", e.toString());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(activity, "Ýndirme hazýrlanýyor", Toast.LENGTH_SHORT).show();

      /*  Intent i = new Intent();
        i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        activity.startActivity(i);
        */
        /*Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url1));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url2));
                activity.startActivity(browserIntent);
            }
        }, 3000);
        activity.startActivity(i);
*/

    }
}
