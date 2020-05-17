package ro.pub.cs.systems.eim.practicaltest2skel.threads;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;
import ro.pub.cs.systems.eim.practicaltest2skel.Utilities;
import ro.pub.cs.systems.eim.practicaltest2skel.Information;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i("[PracticalTest02]", "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");

            // AICI SE OBTIN INFORMATIILE DE CARE ESTE NEVOIE
//            String city = bufferedReader.readLine();
//            String informationType = bufferedReader.readLine();
//            if (city == null || city.isEmpty() || informationType == null || informationType.isEmpty()) {
//                Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
//                return;
//            }
            Information data = serverThread.getData();
            Information information = null;
            // IN CAZ CA VREA CU HASHMAP, RAMANE PARTEA ASTA
//            if (data.containsKey(city)) {
//                Log.i("[PracticalTest02]", "[COMMUNICATION THREAD] Getting the information from the cache...");
//                weatherForecastInformation = data.get(city);
//            } else {
            Log.i("[PracticalTest02]", "[COMMUNICATION THREAD] Getting the information from the webservice...");
            HttpClient httpClient = new DefaultHttpClient();
            String pageSourceCode = "";
                // IN CAZUL IN CARE SE DORESTE SA SE FACA POST
//                if (false) {
//                    HttpPost httpPost = new HttpPost("https://api.openweathermap.org/data/2.5/weather");
//                    List<NameValuePair> params = new ArrayList<>();
//                    params.add(new BasicNameValuePair("q", city));
//                    params.add(new BasicNameValuePair("mode", "json"));
//                    params.add(new BasicNameValuePair("APPID", "e03c3b32cfb5a6f7069f2ef29237d87e"));
//                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
//                    httpPost.setEntity(urlEncodedFormEntity);
//                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
//
//                    pageSourceCode = httpClient.execute(httpPost, responseHandler);
//                } else {


            // SE FACE CEREREA ASTA LA SITE-UL PRIMIT IN ENUNT DUPA CUM SE DORESTE
//            HttpGet httpGet = new HttpGet("https://api.openweathermap.org/data/2.5/weather" + "?q=" + city + "&APPID=" + "e03c3b32cfb5a6f7069f2ef29237d87e" + "&units=" + "metric");
//            HttpResponse httpGetResponse = httpClient.execute(httpGet);
//            HttpEntity httpGetEntity = httpGetResponse.getEntity();
//            if (httpGetEntity != null) {
//                pageSourceCode = EntityUtils.toString(httpGetEntity);
//
//            }



//                }

            if (pageSourceCode == null) {
                Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                return;
            } else
                Log.i("[PracticalTest02]", pageSourceCode );


            JSONObject content = new JSONObject(pageSourceCode);

            // CA MAI JOS POTI OBTINE INFORMATIILE DINTR-UN JSON
//            JSONArray weatherArray = content.getJSONArray(Constants.WEATHER);
//            JSONObject weather;
//            String condition = "";
//            for (int i = 0; i < weatherArray.length(); i++) {
//                weather = weatherArray.getJSONObject(i);
//                condition += weather.getString(Constants.MAIN) + " : " + weather.getString(Constants.DESCRIPTION);
//
//                if (i < weatherArray.length() - 1) {
//                    condition += ";";
//                }
//            }

//            String countryCode = content.getString("alpha2");
//            String countryName = content.getString("name");
//            String continentName = content.getString("continent");
//
//            JSONObject geo = content.getJSONObject("geo");
//            String latitude = geo.getString("latitude");
//            String longitude = geo.getString("longitude");

            information = new Information(
                    /*countryName, continentName, countryCode, Double.parseDouble(latitude), Double.parseDouble(longitude)*/
            );

            if (information == null) {
                Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] Weather Forecast Information is null!");
                return;
            }
            String result = information.toString();

            printWriter.println(result);
            printWriter.flush();
        } catch (IOException ioException) {
            Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            ioException.printStackTrace();
        } catch (JSONException jsonException) {
            Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] An exception has occurred: " + jsonException.getMessage());
            jsonException.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    ioException.printStackTrace();
                }
            }
        }
    }
}
