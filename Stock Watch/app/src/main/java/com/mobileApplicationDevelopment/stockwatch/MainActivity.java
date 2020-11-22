package com.mobileApplicationDevelopment.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "MainActivity";
    List<Stock> stockList = new ArrayList<>();
    HashMap<String, String> symbolMap;
    RecyclerView recyclerView;
    StocksAdapter stocksAdapter;
    SwipeRefreshLayout refreshLayout;
    MySQLDatabase sqlDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        refreshLayout = findViewById(R.id.swipelayout);
        stocksAdapter = new StocksAdapter(stockList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(stocksAdapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!networkCheck()) {
                    refreshLayout.setRefreshing(false);
                    errorUpdateDialog();
                } else {
                    reloadData();
                }
            }
        });
        sqlDatabase = new MySQLDatabase(this);
        NameLoaderRunnable myRunnable1 = new NameLoaderRunnable(this);
        new Thread(myRunnable1).start();
        ArrayList<Stock> tempList = sqlDatabase.loadStocks();
        if (!networkCheck()) {
            stockList.addAll(tempList);
            Collections.sort(stockList, new Comparator<Stock>() {
                @Override
                public int compare(Stock o1, Stock o2) {
                    return o1.getStockSymbol().compareTo(o2.getStockSymbol());
                }
            });
            stocksAdapter.notifyDataSetChanged();
        } else {
            for (int i = 0; i < tempList.size(); i++) {
                String symbol = tempList.get(i).getStockSymbol();
                //new Stockloader(MainActivity.this).execute(symbol);
                StockLoaderRunnable myRunnable2 = new StockLoaderRunnable(this, symbol);
                new Thread(myRunnable2).start();
            }
        }

    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        String marketPlaceURL = "http://www.marketwatch.com/investing/stock/" + stockList.get(pos).getStockSymbol();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(marketPlaceURL));
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        final int id = recyclerView.getChildLayoutPosition(v);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Stock");
        builder.setIcon(R.drawable.baseline_delete_black_48);
        builder.setMessage("Delete Stock Symbol "+((TextView)v.findViewById(R.id.companysymbol)).getText().toString().toUpperCase()+" ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sqlDatabase.deleteStock(stockList.get(id).getStockSymbol());
                stockList.remove(id);
                stocksAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        stocksAdapter.notifyDataSetChanged();
    }

    private void reloadData() {
        refreshLayout.setRefreshing(false);
        ArrayList<Stock> tempArrayList = sqlDatabase.loadStocks();
        for (int i = 0; i < tempArrayList.size(); i++) {
            String symbol = tempArrayList.get(i).getStockSymbol();
            StockLoaderRunnable myRunnable2 = new StockLoaderRunnable(this, symbol);
            new Thread(myRunnable2).start();
        }
        Toast.makeText(this,"Data Refreshed",Toast.LENGTH_SHORT).show();
    }

    private boolean networkCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void networkErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        builder.setMessage("Stocks cannot be added without Network Connection");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void errorUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        builder.setMessage("Stocks cannot be updated without Network Connection");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (!networkCheck()) {
            networkErrorDialog();
            return false;
        } else {
            if (id == R.id.add_stock) {
                addStockDialog();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    private void addStockDialog() {
        if(symbolMap == null){
            NameLoaderRunnable myRunnable1 = new NameLoaderRunnable(this);
            new Thread(myRunnable1).start();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stock Selection");
        builder.setMessage("Please enter a Stock Symbol:");
        builder.setCancelable(false);
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        editText.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(editText);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!networkCheck()){
                    networkErrorDialog();
                }
                else if(editText.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Please Enter Valid Input", Toast.LENGTH_SHORT).show();
                }else {
                    ArrayList<String> tempList = searchStock(editText.getText().toString());
                    if(!tempList.isEmpty()){
                        ArrayList<String> stockOptions = new ArrayList<>(tempList);
                        if(stockOptions.size() == 1){
                            if(isDuplicateStock(stockOptions.get(0))){
                                duplicateItemDialog(editText.getText().toString());
                            }
                            else {
                                saveStock(stockOptions.get(0));
                            }
                        }
                        else {
                            multipleStocksFoundDialog(editText.getText().toString(),stockOptions,stockOptions.size());
                        }
                    }
                    else {
                        dataNotFoundDialog(editText.getText().toString());
                    }

                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void dataNotFoundDialog(String toString) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Symbol Not Found: "+toString.toUpperCase() );
        builder.setMessage("Data for stock symbol");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void multipleStocksFoundDialog(final String s, ArrayList<String> stockOptions, int size) {
        final String[] strings = new String[size];
        for(int i=0;i<strings.length;i++){
            strings[i]=stockOptions.get(i);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a Selection");
        builder.setItems(strings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(isDuplicateStock(strings[which])){
                    duplicateItemDialog(s);
                }
                else {
                    saveStock(strings[which]);
                }
            }
        });
        builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveStock(String s) {
        String symbol = s.split("-")[0].trim();
        StockLoaderRunnable myRunnable2 = new StockLoaderRunnable(this, symbol);
        new Thread(myRunnable2).start();
        Stock temp_stock = new Stock();
        temp_stock.setStockSymbol(symbol);
        temp_stock.setStockName(symbolMap.get(symbol));
        sqlDatabase.addStock(temp_stock);
    }

    private void duplicateItemDialog(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Duplicate Stock");
        builder.setIcon(R.drawable.baseline_warning_black_48);

        builder.setMessage("Stock symbol "+s.toUpperCase()+" already displayed");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isDuplicateStock(String s) {
        Log.d(TAG, "isDuplicateStock: ");
        String symbol = s.split("-")[0].trim();
        Stock temp = new Stock();
        temp.setStockSymbol(symbol);
        return stockList.contains(temp);
    }

    private ArrayList<String> searchStock(String s) {
        ArrayList<String> stockOption = new ArrayList<>();
        if(symbolMap != null && !symbolMap.isEmpty()) {
            for (String symbol : symbolMap.keySet()) {
                String name = symbolMap.get(symbol);
                if (symbol.toUpperCase().contains(s.toUpperCase())) {
                    stockOption.add(symbol + " - " + name);
                } else if (name.toUpperCase().contains(s.toUpperCase())) {
                    stockOption.add(symbol + " - " + name);
                }

            }
        }
        return stockOption;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;

    }



    public void setData(HashMap<String, String> hashMap) {
        if (hashMap != null && !hashMap.isEmpty()) {
            this.symbolMap = hashMap;
        }
    }

    public void setStock(Stock stock) {
        if (stock != null) {
                int index = stockList.indexOf(stock);
            if (index > -1) {
                stockList.remove(index);
            }
            stockList.add(stock);
            Collections.sort(stockList, new Comparator<Stock>() {
                @Override
                public int compare(Stock o1, Stock o2) {
                    return o1.getStockSymbol().compareTo(o2.getStockSymbol());
                }
            });
            stocksAdapter.notifyDataSetChanged();
        }
    }

    public void downloadFailed() {
        stockList.clear();
        stocksAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sqlDatabase.shutDown();
    }
}