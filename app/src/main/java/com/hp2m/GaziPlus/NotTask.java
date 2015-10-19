package com.hp2m.GaziPlus;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import de.greenrobot.event.EventBus;

public class NotTask extends AsyncTask<Void, Void, Void> {

    final EventBus bus = EventBus.getDefault();
    ArrayList<String> notList = new ArrayList<>();
    ArrayList<String> idList = new ArrayList<>();
    private String message, username, password, genelOrt = "-";
    private Context context;

    public NotTask(Context context, String username, String password) {
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

    public HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {

            Log.i("tuna", "onDoInBackground");


            Log.i("tuna", "pass is " + password);
            //OutputStream os = new ByteArrayOutputStream(password.getBytes("ISO-8859-9").);


            /*Document doc = Jsoup.parse(password);

            Document.OutputSettings settings = doc.outputSettings();

            settings.prettyPrint(false);
            settings.escapeMode(Entities.EscapeMode.extended);
            settings.charset("ISO-8859-9");

            String modifiedFileHtmlStr = doc.body().toString().substring(6,13);

            Log.i("tuna", modifiedFileHtmlStr);
*/


            //Log.i("tuna", Jsoup.parse(new ByteArrayInputStream(password.getBytes()), "windows-1253", password).text());
            // password = Jsoup.parse(new ByteArrayInputStream(password.getBytes()), "windows-1253", password).text();
            //password = password.replace("t", URLDecoder.decode("0x0074", "ISO-8859-9"));

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

            //Log.i("tuna", response);
            //Log.i("tuna", res2.body());
            /*Document document = res2.parse();
            document.outputSettings().escapeMode(Entities.EscapeMode.xhtml);

            Element elem = document.select("frame[src]").first();
            String sessionLink = elem.attr("abs:src");*/
            //String myID = sessionLink.substring(57, 89);
            Log.i("tuna", myID);
            String mightyURL = "https://ogrenci.gazi.edu.tr/ogrenci/htmlNavigate.php?ReqID=NOT_GOR_EKR&myID="
                    + myID;
            /*Document doc2 = Jsoup.parse(new URL(mightyURL).openStream(), "ISO-8859-9", mightyURL);*/

                    /*.method(Connection.Method.GET)
                    .timeout(3000)
                    .get();*/

            // the referrer for 17.10.15
            // NotTask fix
            String referrer = "https://ogrenci.gazi.edu.tr/ogrenci/htmlAnaMenu.php?myID=" + myID;
            // ----------------


            Connection.Response connection = Jsoup.connect(mightyURL)
                    .referrer(referrer)
                    .timeout(0)
                    .cookies(res2.cookies())
                    .method(Connection.Method.GET)
                    .execute();

            Document doc2 = Jsoup.parse(new ByteArrayInputStream(connection.bodyAsBytes()), "ISO-8859-9", mightyURL);



            // below is working code
           /* Document doc2 = Jsoup.connect(mightyURL)
                    .cookies(res2.cookies())
                    .timeout(0)
                    .referrer(referrer)
                    .get();
            */
            /*Connection.Response docx = Jsoup.connect(mightyURL)
                    .cookies(res2.cookies())
                    .timeout(0)
                    .execute();*/
            Log.i("tuna", "jsoup 3 passed");
            //Log.i("tuna", "doc2.body= " + doc2.body());
            // test
            //dasdsa
            //asdaasda
            //uuu
            //slmcnm
            Elements element1 = doc2.select("tbody tr");
            String whoAreYou = element1.get(1).text().replace(String.valueOf((char) 160), " ").trim();
            Log.i("tuna", "arrays passed");
            String cleanA = whoAreYou.substring(0, whoAreYou.length() - 13);
            String number = cleanA.substring(0, 9);
            String name = cleanA.substring(10, cleanA.length());
            idList.add(name);
            idList.add(number);
            // use SharedPrefs to see if image is loaded before or not
            boolean haveYouFoundGenelOrt = false;
            for (int i = 3; i < element1.size(); i++) {
                String b = element1.get(i).text().replace(String.valueOf((char) 160), " ").trim();
                if (b.startsWith("2")) {
                    if (b.endsWith("ý")) {
                        notList.add(b);
                        i++;
                        continue;
                    } else if (b.contains("Genel Ortalama : ")) {
                        if (!haveYouFoundGenelOrt) {
                            int c = b.indexOf("Genel Ortalama :");
                            String x2 = b.substring(c + 18, c + 19);
                            Log.i("tuna", "x2= " + x2);
                            String x3 = b.substring(c + 19, c + 20);
                            Log.i("tuna", "x3= " + x3);
                            if (!x2.equals("-")) {
                                x2 = b.substring(c + 18, c + 22);
                                genelOrt = x2;
                                haveYouFoundGenelOrt = true;
                            }
                        }
                        continue;
                    } else {
                        continue;
                    }
                } else {
                    if (b.startsWith("MD- MUAF") || b.startsWith("(WEB")) break;
                }
                // sondaki o Dönem'i alma
                //String a = b.substring(0, b.length() - 15);
                /*Log.i("tuna", "a= " + a);
                Log.i("tuna", "isDigit1= " + Character.isWhitespace(a.charAt(a.length() - 1)));
                Log.i("tuna", "digitAt1= " + a.charAt(a.length()-1));
                Log.i("tuna", "isDigit2= " + Character.isWhitespace(a.charAt(a.length() - 2)));
                Log.i("tuna", "digitAt2= " + a.charAt(a.length() - 2));

                Log.i("tuna", "isDigitNORMAL= " + Character.isDigit('5'));*/


                /*if(Character.isDigit(a.charAt(a.length()-1)) &&
                        !(Character.isDigit(a.charAt(a.length()-2))) ){
                    a+="  -";
                }*/
                //notList.add(b.substring(0, b.length() - 15));
                notList.add(b.substring(0, b.length() - 15));
            }


            SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
            if (!sharedPreferences.getBoolean("IsAvatarDownloadedFor" + number, false)) {
                Log.i("tuna", "gonna download image");
                String idMightyURL = "https://ogrenci.gazi.edu.tr/ogrenci/htmlNavigate.php?ReqID=OGR_BILGI_EKR&myID="
                        + myID;

                //Document doc3 = Jsoup.parse(new URL(idMightyURL).openStream(), "ISO-8859-9", idMightyURL);
                Document doc3 = Jsoup.connect(idMightyURL)
                        .cookies(res2.cookies())
                        .referrer(referrer)
                        .timeout(0)
                        .get();
                Log.i("tuna", "html parsed, gonna find index of link");
                StringBuilder a = new StringBuilder("");
                a.append(doc3.body().toString());
                int x3 = StringUtils.indexOf(a.toString(), "img src=");
                Log.i("tuna", "index found");
                StringBuilder c = new StringBuilder("");
                c.append(StringUtils.substring(a.toString(), x3 + 9, x3 + 129).replace("amp;", ""));
                Log.i("tuna", "image link found");
                idList.add(c.toString());
                Log.i("tuna", "imageLink is = " + c.toString());


            /*int x2 = doc3.body().toString().indexOf("img src=");
            Log.i("tuna", "index found");
            String imageLink = doc3.body().toString().substring(x2+9,x2+129).replace("amp;", "");
            Log.i("tuna", "image link found");
            idList.add(imageLink);
            Log.i("tuna", "imageLink is = " + imageLink);
*/
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("avatarLinkFor" + number, c.toString());
                editor.apply();
            } else {
                idList.add(sharedPreferences.getString("avatarLinkFor" + number, ""));

            }
            message = "GoGoGo";

            for (String a2 : notList) {
                Log.i("tuna", a2);
            }


        } catch (Exception e) {
            message = "error";
            Log.i("tuna", e.toString());
        }


        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        bus.post(new NotDownloadCompleted(notList, idList, message, genelOrt));

        Log.i("tuna", "on post execute");
        //final EventBus bus = EventBus.getDefault();
        // bus.post(new NotDownloadCompleted());
    }
}

class NotDownloadCompleted {
    public ArrayList<String> notList2;
    public ArrayList<String> idList;
    public String message, genelOrt;

    public NotDownloadCompleted(ArrayList<String> notList2, ArrayList<String> idList, String message, String genelOrt) {
        this.notList2 = notList2;
        this.idList = idList;
        this.message = message;
        this.genelOrt = genelOrt;
    }
}
