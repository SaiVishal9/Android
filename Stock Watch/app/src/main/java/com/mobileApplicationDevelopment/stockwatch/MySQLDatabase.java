package com.mobileApplicationDevelopment.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class MySQLDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StockWatchDB";
    private static final String TABLE_NAME = "StockWatchTable";
    private static final String STOCKSYMBOL = "StockSymbol";
    private static final String COMPANYNAME = "CompanyName";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLECREATE = "CREATE TABLE " + TABLE_NAME + " (" + STOCKSYMBOL + " TEXT not null unique," + COMPANYNAME + " TEXT not null)";

    private SQLiteDatabase database;

    public MySQLDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLECREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<Stock> loadStocks() {
        ArrayList<Stock> stocks = new ArrayList<>();
        Cursor cursor = database.query(
                TABLE_NAME, // The table to query
                new String[]{STOCKSYMBOL, COMPANYNAME}, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null,
                null
        ); // don't filter by row groups
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String name = cursor.getString(1);
                Stock s = new Stock();
                s.setStockSymbol(symbol);
                s.setStockName(name);
                s.setStockPrice(0.0);
                s.setPriceChange(0.0);
                s.setPricePercentge(0.0);
                stocks.add(s);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return stocks;
    }

    public void addStock(Stock newStock) {
        ContentValues values = new ContentValues();
        values.put(STOCKSYMBOL, newStock.getStockSymbol());
        values.put(COMPANYNAME, newStock.getStockName());
        long key = database.insert(TABLE_NAME, null, values);
    }

    public void deleteStock(String symbol) {
        int cnt = database.delete(TABLE_NAME, STOCKSYMBOL + " = ?", new String[]{symbol});
    }

    public void shutDown() {
        database.close();
    }
}
