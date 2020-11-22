package com.mobileApplicationDevelopment.stockwatch;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class NameLoaderRunnable implements Runnable {

    private MainActivity mainActivity;
    private static final String DATA_URL = "https://api.iextrading.com/1.0/ref-data/symbols";
    public NameLoaderRunnable(MainActivity mainActivity) {
        this.mainActivity =mainActivity;
    }

    @Override
    public void run() {
        Uri dataUri = Uri.parse(DATA_URL);
        String urlToUse = dataUri.toString();
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            if(conn.getResponseCode() != HttpURLConnection.HTTP_OK){
                handleResults(null);
                return;
            }
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null){
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            handleResults(null);
        }
        handleResults(sb.toString());
    }

    private void handleResults(String s)
    {
        if(s == null){
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.downloadFailed();
                }
            });
            return;
        }
        final HashMap<String, String> hashMap = parseJSON(s);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (hashMap != null){
                    mainActivity.setData(hashMap);
                }
            }
        });
    }

    private HashMap<String, String> parseJSON(String s) {
        HashMap<String, String> stringHashMap = new HashMap<>();
        try {
            JSONArray jsonArray = new JSONArray(s);
            for (int i = 0;i<jsonArray.length();i++){
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String symbol = jsonObject.getString("symbol");
                String name = jsonObject.getString("name");
                stringHashMap.put(symbol,name);
            }
            return stringHashMap;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}