package com.mobileApplicationDevelopment.stockwatch;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class StockLoaderRunnable implements Runnable {
    private MainActivity mainActivity;
    private String s;
    private static final String DATAURL_1 = "https://cloud.iexapis.com/stable/stock/";
    private static final String DATAURL_2 = "quote?token=pk_5ebc0d3013dc49dab9f511d8622cc69d";
    public StockLoaderRunnable(MainActivity mainActivity, String s) {
        this.mainActivity = mainActivity;
        this.s=s;
    }

    @Override
    public void run() {
        String API_URL = DATAURL_1 + s+"/" + DATAURL_2;
        Uri uri = Uri.parse(API_URL);
        String url_string = uri.toString();
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(url_string);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            if(conn.getResponseCode() != HttpURLConnection.HTTP_OK){
                handleResults(null);
                return;
            }
            InputStream inputStream = conn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = bufferedReader.readLine())!=null){
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
        //final HashMap<String,String> hashMap = parseJSON(s);
        final Stock stock = parseJSON(s);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.setStock(stock);
            }
        });
    }

    private Stock parseJSON(String s) {
        Stock temp_stock = new Stock();
        try {
            JSONObject jsonObject = new JSONObject(s);
            String symbol = jsonObject.getString("symbol");
            String name = jsonObject.getString("companyName");
            double price = jsonObject.getDouble("latestPrice");
            double priceChange = jsonObject.getDouble("change");
            double changePercentage = jsonObject.getDouble("changePercent");

            temp_stock.setStockName(name);
            temp_stock.setStockSymbol(symbol);
            temp_stock.setStockPrice(price);
            temp_stock.setPriceChange(priceChange);
            temp_stock.setPricePercentge(changePercentage);
            return temp_stock;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
