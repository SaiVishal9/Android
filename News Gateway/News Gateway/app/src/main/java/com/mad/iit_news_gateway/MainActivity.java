package com.mad.iit_news_gateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final String REQUEST_ARTICLES = "REQUEST_ARTICLES";
    static final String RESPONSE_ARTICLES = "RESPONSE_ARTICLES";
    private NewsReceiver newsReceiver;
    private final ArrayList<NewsSource> countryList = new ArrayList<>();
    private final HashMap<String, ArrayList<NewsSource>> countryData = new HashMap<>();
    private Menu menu_main;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private List<Fragment> fragments;
    private MyPageAdapter pageAdapter;
    private ViewPager pager;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, NewsService.class);
        startService(intent);
        newsReceiver = new NewsReceiver();
        registerReceiver(newsReceiver, new IntentFilter(RESPONSE_ARTICLES));
        registerReceiver(newsReceiver, new IntentFilter(REQUEST_ARTICLES));
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.drawer_open, R.string.drawer_close);
        SourceLoaderRunnable asyncSourceLoader = new SourceLoaderRunnable(MainActivity.this);
        new Thread(asyncSourceLoader).start();
        ArrayAdapter<NewsSource> newsSourceArrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_list_item, countryList);
        mDrawerList.setAdapter(newsSourceArrayAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectSource(position);
            }
        });
        fragments = new ArrayList<>();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.view_pager);
        pager.setAdapter(pageAdapter);

        if (savedInstanceState != null){
            fragments = (List<Fragment>) savedInstanceState.getSerializable("fragments");
            setTitle(savedInstanceState.getString("title"));
            pageAdapter.notifyDataSetChanged();
            Log.d(TAG, "pageCount: " +pageAdapter.getCount());
            for (int i = 0; i< pageAdapter.getCount(); i++)
                pageAdapter.notifyChangeInPosition(i);
        }
    }

    public void updateData(ArrayList<NewsSource> listIn) {

        for (NewsSource s : listIn) {
            if (s.getCategory().isEmpty()) {
                s.setCategory("Unspecified");
            }
            if (!countryData.containsKey(s.getCategory())) {
                countryData.put(s.getCategory(), new ArrayList<NewsSource>());
            }
            ArrayList<NewsSource> clist = countryData.get(s.getCategory());
            if (clist != null) {
                clist.add(s);
            }
        }

        countryData.put("All", listIn);
        ArrayList<String> tempList = new ArrayList<>(countryData.keySet());
        Collections.sort(tempList);

        for (String s  : tempList) {
            if (s.equalsIgnoreCase("general")) {
                SpannableStringBuilder builder = new SpannableStringBuilder();
                SpannableString redSpannable = new SpannableString(s);
                redSpannable.setSpan(new AbsoluteSizeSpan(50), 0, s.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                redSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#ffff15")), 0, s.length(), 0);
                builder.append(redSpannable);
                menu_main.add(builder);
            }
            else if (s.equalsIgnoreCase("sports")) {
                SpannableStringBuilder builder2 = new SpannableStringBuilder();
                SpannableString redSpannable = new SpannableString(s);
                redSpannable.setSpan(new AbsoluteSizeSpan(50), 0, s.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                redSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#3F51B5")), 0, s.length(), 0);
                builder2.append(redSpannable);
                menu_main.add(builder2);
            }
            else if (s.equalsIgnoreCase("health")) {
                SpannableStringBuilder builder5 = new SpannableStringBuilder();
                SpannableString redSpannable = new SpannableString(s);
                redSpannable.setSpan(new AbsoluteSizeSpan(50), 0, s.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                redSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#8B008B")), 0, s.length(), 0);
                builder5.append(redSpannable);
                menu_main.add(builder5);
            }
            else if (s.equalsIgnoreCase("business")){
                SpannableStringBuilder builder3 = new SpannableStringBuilder();
                SpannableString redSpannable = new SpannableString(s);
                redSpannable.setSpan(new AbsoluteSizeSpan(50), 0, s.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                redSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#008000")), 0, s.length(), 0);
                builder3.append(redSpannable);
                menu_main.add(builder3);
            }
            else if (s.equalsIgnoreCase("entertainment")){
                SpannableStringBuilder builder4 = new SpannableStringBuilder();
                SpannableString redSpannable = new SpannableString(s);
                redSpannable.setSpan(new AbsoluteSizeSpan(50), 0, s.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                redSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 0, s.length(), 0);
                builder4.append(redSpannable);
                menu_main.add(builder4);
            }
            else if (s.equalsIgnoreCase("science")){
                SpannableStringBuilder builder5 = new SpannableStringBuilder();
                SpannableString redSpannable = new SpannableString(s);
                redSpannable.setSpan(new AbsoluteSizeSpan(50), 0, s.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                redSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#0CB1BB")), 0, s.length(), 0);
                builder5.append(redSpannable);
                menu_main.add(builder5);
            }
            else if (s.equalsIgnoreCase("technology")){
                SpannableStringBuilder builder5 = new SpannableStringBuilder();
                SpannableString redSpannable = new SpannableString(s);
                redSpannable.setSpan(new AbsoluteSizeSpan(50), 0, s.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                redSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#FF1493")), 0, s.length(), 0);
                builder5.append(redSpannable);
                menu_main.add(builder5);
            }
            else {
                SpannableStringBuilder builder6 = new SpannableStringBuilder();
                SpannableString redSpannable = new SpannableString(s);
                redSpannable.setSpan(new AbsoluteSizeSpan(50), 0, s.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                redSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
                builder6.append(redSpannable);
                menu_main.add(builder6);
            }
        }

        countryList.addAll(listIn);
        Log.d(TAG, "Sources Category wise: " +countryData);
        Log.d(TAG, "List of Sources: " +countryList);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, countryList));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(newsReceiver);
        Intent intent = new Intent(MainActivity.this, NewsService.class);
        stopService(intent);
        super.onDestroy();
    }

    private void selectSource(int position) {
        Toast.makeText(this, countryList.get(position).getName(), Toast.LENGTH_SHORT).show();
        setTitle(countryList.get(position).getName());
        Intent requestIntent = new Intent();
        requestIntent.setAction(MainActivity.REQUEST_ARTICLES);
        requestIntent.putExtra("source", countryList.get(position).getId());
        sendBroadcast(requestIntent);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        String q = item.getTitle().toString();
        SpannableStringBuilder builder5 = new SpannableStringBuilder();
        SpannableString redSpannable = new SpannableString(item.getTitle().toString());
        redSpannable.setSpan(new ForegroundColorSpan(Color.WHITE), 0, q.length(), 0);
        builder5.append(redSpannable);
        setTitle(builder5);
        countryList.clear();
        ArrayList<NewsSource> clist = countryData.get(item.getTitle().toString());
        if (clist != null) {
            countryList.addAll(clist);
        }
        ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu_main = menu;
        return true;
    }

    private class MyPageAdapter extends FragmentPagerAdapter{
        private long baseId = 0;
        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            return baseId+position;
        }

        public void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }
    }

    public class NewsReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (RESPONSE_ARTICLES.equals(intent.getAction())) {
                ArrayList<Article> articles = (ArrayList<Article>) intent.getSerializableExtra("articles");
                fragments.clear();
                int x = articles.size() - 90;
                Log.d(TAG, "onReceive: " + x);
                for (int i = 0; i < x; i++) {
                    fragments.add(ArticleFragment.newInstance(articles.get(i)));
                    pageAdapter.notifyChangeInPosition(i);
                }
                pageAdapter.notifyDataSetChanged();
                pager.setCurrentItem(0);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("fragments", (Serializable) fragments);
        outState.putString("title",getTitle().toString());
        super.onSaveInstanceState(outState);
    }
}