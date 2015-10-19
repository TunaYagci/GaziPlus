package com.hp2m.GaziPlus;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import de.greenrobot.event.EventBus;

public class TimetableTask extends AsyncTask<Void, Void, Void> {

    final EventBus bus = EventBus.getDefault();
    private List<String> list;
    private String username, password;
    private Context context;

    public TimetableTask(Context context, String username, String password) {
        this.context = context;
        this.username = username;
        this.password = password;
    }

    public static void enableSSLSocket() throws KeyManagementException, NoSuchAlgorithmException {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new X509TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }}, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        // hi sadr, below code is to login to ogrenci.gazi.edu.tr
        // so just don't care those, i've copied them from NotTask
        // just change the url that i've written below !!!

        // There is our result list
        list = new ArrayList<>();
        // I've added it some things on try/catch
        // in case some exception fires
        // so it won't return null

        // starts here ---------------------------------------------
        try {
            enableSSLSocket();

            Log.i("tuna", "about to work");
            String url = "https://ogrenci.gazi.edu.tr/ogrenci/";
            Connection.Response res2 = Jsoup.connect(url)
                    .data("kullanici", username)
                    .data("sifre", password)
                    .data("login", "  Baðlan  ")
                            //.header("Content-Type", "windows-1254")
                    .followRedirects(true)
                    .method(Connection.Method.POST)
                            //.timeout(10000)
                    .timeout(0)
                    .execute();
            Log.i("tuna", "jsoup 2 passed");

            String response = new String(res2.body().getBytes("ISO-8859-9"), "UTF-8");
            int x = response.indexOf("myID") + 5;
            String myID = response.substring(x, x + 32);


            //Log.i("tuna", myID);



            // CHANGE THIS URL
            // !!
            // !!
            // !!
            // !!
            // note: don't copy the myID, we add it below programmatically
            String mightyURL = "https://ogrenci.gazi.edu.tr/ogrenci/htmlNavigate.php?ReqID=NOT_GOR_EKR&myID="
                    + myID;




            String referrer = "https://ogrenci.gazi.edu.tr/ogrenci/htmlAnaMenu.php?myID=" + myID;
            Connection.Response connection = Jsoup.connect(mightyURL)
                    .referrer(referrer)
                    .timeout(0)
                    .cookies(res2.cookies())
                    .method(Connection.Method.GET)
                    .execute();

            Document doc2 = Jsoup.parse(new ByteArrayInputStream(connection.bodyAsBytes()), "ISO-8859-9", mightyURL);
            Log.i("tuna", "jsoup 3 passed");

            // ends here ---------------------------------------------


            try {


                // actual code begins here for you
                // for css query, i highly suggest you to use
                // try.jsoup.org
                // magnificent thing it is. You can learn from the cookbook there,
                // like you can point a div like " div.'className' "
                // or like lets point a <p element on a div
                // div.post-content p
                Elements element = doc2.select(" CSS QUERY ");

                for (int i = 0; i < element.size(); i++) {
                    list.add(element.get(i).text());
                    Log.i("sadr", element.get(i).text());

                    // don't really focus here
                    // we should add them to a db and retrieve the data from the TimetableActivity later
                    // just test your results here on log or list
                    // NOTE: i suggest you to use Genymotion for emulator
                    // it is way much faster and stable -> no memory leaks
                }


                // If you did all just create a new DB and pass those to that DB
                // and if you use a db just don't pass the list with eventBus
                // send a message with eventBus like, if there was an error or not
                // show the corresponding data regarding to response
                // i did all those on NotTask or DuyuruTask, you can see there
                // tnaygc goes offline



            } catch (Exception e) {
                list.add("some jsoup error");
                return null;
            }


        }catch (Exception e){
            // lets not send null to event
            list.add(e.toString());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);


        // finally eventBus posts its event to all listeners
        // in this case, we have registered to listener list from TimetableActivity
        // with code: bus.register(this), it is there for that reason

        // here, eventBus is posting the list
        bus.post(new TimetableTaskCompleted(list));
    }
}


// define the event here
class TimetableTaskCompleted {
    // I've created a list for you
    public List<String> list;

    public TimetableTaskCompleted(List<String> list) {
        this.list = list;
    }
}
