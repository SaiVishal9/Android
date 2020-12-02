package com.example.MAD.knowyourgovernment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = "PhotoActivity";
    private TextView loca;
    private TextView office;
    private TextView name;
    private ImageView iv;
    private ImageView imageView1;
    private Official off;
    private TextView party;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        loca= (TextView) findViewById(R.id.location_TextView);
        office = (TextView) findViewById(R.id.office_TextView);
        name = (TextView) findViewById(R.id.name_TextView);
        iv = (ImageView) findViewById(R.id.photo);
        imageView1 = (ImageView)findViewById(R.id.imageView2);

        Intent intent = getIntent();

        if(intent.hasExtra("location"))
            loca.setText(intent.getStringExtra("location"));

        if (intent.hasExtra(Official.class.getName())) {
            off = (Official) intent.getSerializableExtra(Official.class.getName());
            office.setText(off.getOffice());
            name.setText(off.getName());
            loadRemoteImage(off.getPhotoURL());
        }
        if(!off.getParty().equalsIgnoreCase("Unknown") && !off.getParty().equalsIgnoreCase("No data provided")) {
            party = (TextView) findViewById(R.id.party_TextView);
            party.setText("(" + off.getParty() + ")");
        }

        View view = this.getWindow().getDecorView();
        if(off.getParty().equalsIgnoreCase("democratic") || off.getParty().equalsIgnoreCase("Democratic Party")) {
            view.setBackgroundColor(getResources().getColor(R.color.colorBlue));  //red
            imageView1.setImageResource(R.drawable.dem_logo);
        }
        else if(off.getParty().equalsIgnoreCase("republican") || off.getParty().equalsIgnoreCase("Republican Party")) {
            view.setBackgroundColor(getResources().getColor(R.color.colorRed));  //blue
            imageView1.setImageResource(R.drawable.rep_logo);
        }
        else {
            view.setBackgroundColor(getResources().getColor(R.color.colorBlack));  //black
            imageView1.setVisibility(View.INVISIBLE);
        }
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
                            .into(iv);
                }
            }).build();
            picasso.load(imageURL)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(iv);
        } else {
            Picasso.with(this).load(imageURL)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.missingimage)
                    .into(iv);
        }
    }
}
