package ro.pub.cs.systems.eim.practicaltest2skel.threads;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import ro.pub.cs.systems.eim.practicaltest2skel.Utilities;

public class ClientThread extends Thread {

    private String address;
    private int port;
    private String ip;
    private TextView informationTextView;
    private ImageView imageView;

    private Socket socket;

    public ClientThread(String address, int port/*, String ip*/, TextView informationTextView, ImageView imageView) {
        this.address = address;
        this.port = port;
        this.ip = ip;
        this.informationTextView = informationTextView;
        this.imageView = imageView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e("[PracticalTest02]", "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e("[PracticalTest02]", "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            printWriter.println(ip);
            printWriter.flush();
            String information;
            while ((information = bufferedReader.readLine()) != null) {
                final String finalizedInformation = information;
                informationTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        informationTextView.setText(finalizedInformation);
                    }
                });
                // PENTRU PARTEA DE IMAGINE
                String imageUrl = information.substring(information.lastIndexOf(",") + 2);
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGetImage = new HttpGet(imageUrl);
                    HttpResponse httpResponse = httpClient.execute(httpGetImage);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    final Bitmap bitmap = BitmapFactory.decodeStream(httpEntity.getContent());
                    if (bitmap != null) {
                        imageView.post(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                } catch (Exception exception) {
                    Log.i("[PracticalTest02]", exception.getMessage());
                    if (true) {
                        exception.printStackTrace();
                    }
                }
            }
        } catch (IOException ioException) {
            Log.e("[PracticalTest02]", "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (true) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e("[PracticalTest02]", "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (true) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
