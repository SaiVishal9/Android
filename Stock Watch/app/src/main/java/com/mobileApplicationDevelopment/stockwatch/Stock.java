package com.mobileApplicationDevelopment.stockwatch;

import java.io.Serializable;

public class Stock implements Serializable {
    private String stockSymbol;
    private String stockName;
    private double stockPrice;
    private double priceChange;

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public double getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(double stockPrice) {
        this.stockPrice = stockPrice;
    }

    public double getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(double priceChange) {
        this.priceChange = priceChange;
    }

    public double getPricePercentge() {
        return pricePercentge;
    }

    public void setPricePercentge(double pricePercentge) {
        this.pricePercentge = pricePercentge;
    }

    private double pricePercentge;



    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj == null || obj.getClass() != getClass()) {
            result = false;
        } else {
            Stock stock = (Stock) obj;
            if (this.stockSymbol.equals(stock.stockSymbol)) {
                result = true;
            }
        }
        return result;
    }

}
