package com.example.MAD.knowyourgovernment;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DataFetch implements Runnable {

    private MainActivity mainActivity;
    private String s;
    private static final String TAG = "DatsFetch";
    String API_KEY = "AIzaSyAvYqgqj-o63leh8jQ4I2lTJscd81z3MuU";
    String CIVIC_API_URL = "https://www.googleapis.com/civicinfo/v2/representatives?key="+API_KEY;
    private String zipcode=null;
    private Uri.Builder buildURL = null;
    private boolean noDataFound=false;
    private StringBuilder sb1;
    private String currentLocationStr=null;
    private ArrayList<Official> officialArrayList= new ArrayList <Official>();
    Object[] objArray = new Object[2];
    boolean isNoDataFound =true;

    public DataFetch(MainActivity mainActivity, String s) {
        this.mainActivity = mainActivity;
        this.s=s;
    }

    @Override
    public void run() {
        Log.d(TAG, "doInBackground: here 123");
        zipcode = s;
        buildURL = Uri.parse(CIVIC_API_URL).buildUpon();
        buildURL.appendQueryParameter("address", s);
        connectToAPI();
        if(!isNoDataFound) {
            parseJSON1(sb1.toString());
        }
        else {
            currentLocationStr = "No Data for location";
        }
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.setOfficialList(objArray);
            }
        });
    }

    public void connectToAPI() {
        Log.d(TAG, "connectToAPI: ");
        String urlToUse = buildURL.build().toString();
        sb1 = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
            {
                noDataFound=true;
            }
            else {
                conn.setRequestMethod("GET");
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

                String line=null;
                while ((line = reader.readLine()) != null) {
                    sb1.append(line).append('\n');
                }
                isNoDataFound=false;

            }
        }
        catch(FileNotFoundException fe){
            Log.d(TAG, "FileNotFoundException ");
        }
        catch (Exception e) {
            //e.printStackTrace();
            Log.d(TAG, "Exception doInBackground: " + e.getMessage());
        }
    }

    private void parseJSON1(String s) {
        try {
            if(!noDataFound) {

                JSONObject jObjMain = new JSONObject(s);

                //location
                JSONObject location = jObjMain.getJSONObject("normalizedInput");
                currentLocationStr = location.getString("city")+" "+location.getString("state")+" "+location.getString("zip");


                JSONArray offices = jObjMain.getJSONArray("offices");
                JSONArray oficials = jObjMain.getJSONArray("officials");


                for(int i=0;i<offices.length();i++)
                {
                    JSONObject o = (JSONObject) offices.get(i);

                    String office_name = o.getString("name");

                    JSONArray idcArray = o.getJSONArray("officialIndices");

                    for(int j=0;j<idcArray.length();j++)
                    {
                        Official off = new Official();
                        off.setOffice(office_name);
                        JSONObject officialData = (JSONObject) oficials.get(idcArray.getInt(j));
                        if (officialData.getString("name") == null || officialData.getString("name").equals(""))
                            off.setName("No Data Provided");
                        else
                            off.setName(officialData.getString("name"));
                        if(officialData.has("address")) {
                            JSONArray addrArr = officialData.getJSONArray("address");
                            String sb_addr = "";
                            JSONObject addrObj = (JSONObject) addrArr.get(0);
                            if (addrObj.has("line1"))
                                sb_addr = sb_addr + addrObj.getString("line1").toString() + '\n';
                            if (addrObj.has("line2"))
                                sb_addr = sb_addr + addrObj.getString("line2").toString() + '\n';
                            if (addrObj.has("line3"))
                                sb_addr = sb_addr + addrObj.getString("line3").toString() + '\n';
                            off.setAddress(sb_addr);
                        }
                        else
                            off.setAddress("No Data Provided");
                        if(officialData.has("party"))
                            off.setParty(officialData.getString("party"));
                        else
                            off.setParty("No data provided");
                        if (officialData.has("phones")) {
                            JSONArray phoneArr = officialData.getJSONArray("phones");
                            off.setPhone(phoneArr.get(0).toString());
                        } else
                            off.setPhone("No Data Provided");
                        if (officialData.has("urls")) {
                            JSONArray urlArr = officialData.getJSONArray("urls");
                            off.setWebsiteURL(urlArr.get(0).toString());
                        } else
                            off.setWebsiteURL("No Data Provided");
                        if (officialData.has("emails")) {
                            JSONArray emailArr = officialData.getJSONArray("emails");
                            off.setEmail(emailArr.get(0).toString());
                        } else
                            off.setEmail("No Data Provided");
                        if (officialData.has("photoUrl"))
                            off.setPhotoURL(officialData.get("photoUrl").toString());
                        if (officialData.has("channels")) {
                            JSONArray mediaArr = officialData.getJSONArray("channels");
                            for (int x = 0; x < mediaArr.length(); x++) {
                                JSONObject mediaObj = (JSONObject) mediaArr.get(x);
                                if (mediaObj.get("type").equals("Facebook"))
                                    off.setFacebookURL(mediaObj.get("id").toString());
                                else if (mediaObj.get("type").equals("Twitter"))
                                    off.setTwitterURL(mediaObj.get("id").toString());
                                else if (mediaObj.get("type").equals("YouTube"))
                                    off.setYouTubeURL(mediaObj.get("id").toString());
                            }
                        }
                        officialArrayList.add(off);
                    }
                }
                objArray[0] = currentLocationStr;
                objArray[1] = officialArrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
