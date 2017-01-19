package com.petanikode.cobagooglemaps;

/**
 * Created by petanikode on 1/19/17.
 */


import android.util.Log;
import android.util.StringBuilderPrinter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class HttpHandler {

    private static final String TAG = HttpHandler.class.getSimpleName();

    public HttpHandler(){

    }

    public String makeServiceCall(String reqUrl){
        String response = null;

        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // baca respon
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStremToString(in);

        } catch (MalformedURLException e){
            Log.e(TAG, "MalFormedURLException: " + e.getMessage());
        } catch (ProtocolException e){
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }

        return response;
    }

    private String convertStremToString(InputStream is){
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
