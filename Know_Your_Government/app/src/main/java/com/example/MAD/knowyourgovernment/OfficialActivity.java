package com.example.MAD.knowyourgovernment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {
    private static final String TAG = "OfficialActivity";
    private TextView location;
    private TextView office;
    private TextView name;
    private TextView party;
    private TextView address;
    private TextView number;
    private TextView website;
    private TextView email;
    private ImageView imageView;
    private ImageView imageView1;
    private Official o;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        Intent intent = getIntent();

        location = findViewById(R.id.loc_TextView);

        if(intent.hasExtra("location"))
            location.setText(intent.getStringExtra("location"));

        if (intent.hasExtra(Official.class.getName())) {
            o = (Official) intent.getSerializableExtra(Official.class.getName());

            office = findViewById(R.id.location_TextView);
            office.setText(o.getOffice());
            name = findViewById(R.id.officialName_TextView);
            name.setText(o.getName());
            if(!o.getParty().equalsIgnoreCase("Unknown") && !o.getParty().equalsIgnoreCase("No data provided")) {
                party = findViewById(R.id.officialParty_TextView);
                party.setText("(" + o.getParty() + ")");
            }
            address = findViewById(R.id.officialAddress__TextView);
            address.setText(o.getAddress());
            number = findViewById(R.id.officialPhone__TextView);
            number.setText(o.getPhone());
            website = findViewById(R.id.officialWebsite__TextView);
            website.setText(o.getWebsiteURL());
            email = findViewById(R.id.officialEmail__TextView);
            email.setText(o.getEmail());

            imageView = findViewById(R.id.officialPhoto);
            imageView1 = findViewById(R.id.imageView);
            loadRemoteImage(o.getPhotoURL());

            ImageView fbImage = findViewById(R.id.fbLink);
            ImageView twImage = findViewById(R.id.twitterLink);
            ImageView ytImage = findViewById(R.id.ytLink);

            if(o.getFacebookURL()!= null && !o.getFacebookURL().equals(""))
                fbImage.setVisibility(View.VISIBLE);
            else
                fbImage.setVisibility(View.INVISIBLE);

            if(o.getYouTubeURL()!= null && !o.getYouTubeURL().equals(""))
                ytImage.setVisibility(View.VISIBLE);
            else
                ytImage.setVisibility(View.INVISIBLE);

            if(o.getTwitterURL()!= null && !o.getTwitterURL().equals(""))
                twImage.setVisibility(View.VISIBLE);
            else
                twImage.setVisibility(View.INVISIBLE);


            View view = this.getWindow().getDecorView();
            if(o.getParty().equalsIgnoreCase("democratic") || o.getParty().equalsIgnoreCase("Democratic Party")) {
                view.setBackgroundColor(getResources().getColor(R.color.colorBlue));  //red
                imageView1.setImageResource(R.drawable.dem_logo);
            }
            else if(o.getParty().equalsIgnoreCase("republican") || o.getParty().equalsIgnoreCase("Republican Party")) {
                view.setBackgroundColor(getResources().getColor(R.color.colorRed));  //blue
                imageView1.setImageResource(R.drawable.rep_logo);
            }
            else {
                view.setBackgroundColor(getResources().getColor(R.color.colorBlack));  //black
                imageView1.setVisibility(View.INVISIBLE);
            }
        }

        Linkify.addLinks(website, Linkify.WEB_URLS);
        Linkify.addLinks(number, Linkify.PHONE_NUMBERS);
        Linkify.addLinks(address, Linkify.MAP_ADDRESSES);
        Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);


        website.setLinkTextColor(Color.WHITE);
        number.setLinkTextColor(Color.WHITE);
        address.setLinkTextColor(Color.WHITE);
        email.setLinkTextColor(Color.WHITE);
    }

    private  void loadRemoteImage(final String imageURL){

        if (imageURL != null) {
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                // Here we try https if the http image attempt failed
                    final String changedUrl = imageURL.replace("http:", "https:");
                    picasso.load(changedUrl)
                            .fit()
                            .centerCrop()
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(imageView);
                }
            }).build();
            picasso.load(imageURL)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
        } else {
            Picasso.with(this).load(imageURL)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.missingimage)
                    .into(imageView);
        }
    }

    public void openPhotoActivity(View v){
        Log.d(TAG, "openPhotoActivity: ");
        if(o.getPhotoURL()==null)
            return;

        Intent intent = new Intent(OfficialActivity.this, PhotoActivity.class);
        intent.putExtra("location", location.getText());
        intent.putExtra(Official.class.getName(), o);
        startActivity(intent);
    }

  public void facebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/" + o.getFacebookURL();
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else {
                urlToUse = "fb://page/" + o.getFacebookURL();
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL;
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void twitterClicked(View v) {
        Intent intent = null;
        String name = o.getTwitterURL();
        try {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }


    public void youTubeClicked(View v) {
        String name = o.getYouTubeURL();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }
}
