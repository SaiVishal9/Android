package com.mad.iit_news_gateway;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ArticleLoaderRunnable implements Runnable{

    NewsService newsService;
    String source;

    ArticleLoaderRunnable(NewsService newsService, String source) {
        this.newsService = newsService;
        this.source = source;
    }

    @Override
    public void run() {

        StringBuilder sb = new StringBuilder();

        try {
            String prefix = "https://newsapi.org/v2/everything?sources=";
            String apikey = "&language=en&pageSize=100&apiKey=a3eff516b64e40a6af6644a14d53e6ea";
            URL url = new URL(prefix +source+ apikey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.addRequestProperty("User-Agent","");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
            String line;
            while ((line = reader.readLine()) != null) sb.append(line).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        handleResults(sb.toString());
    }

    private void handleResults(String s){
        parseJson(s);
    }

    private void parseJson(String s) {
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray jArrSources = jObjMain.getJSONArray("articles");
            for (int i =0; i<jArrSources.length(); i++){
                JSONObject jObjSource = jArrSources.getJSONObject(i);
                String author = jObjSource.getString("author");
                String title = jObjSource.getString("title");
                String description = jObjSource.getString("description");
                String url = jObjSource.getString("url");
                String urlToImage = jObjSource.getString("urlToImage");
                String publishedAt = jObjSource.getString("publishedAt");
                newsService.addArticle(new Article(author, title, description, url, urlToImage, publishedAt, jArrSources.length(),i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
