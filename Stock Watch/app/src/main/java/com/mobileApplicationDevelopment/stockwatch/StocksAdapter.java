package com.mobileApplicationDevelopment.stockwatch;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import java.util.Locale;

class StocksAdapter extends RecyclerView.Adapter<MyViewHolder>{
    private List<Stock> list;
    private MainActivity local_mainActivity;
    public StocksAdapter(List<Stock> stockList, MainActivity mainActivity){
        this.list = stockList;
        local_mainActivity=mainActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stock_list,viewGroup,false);
       view.setOnClickListener(local_mainActivity);
       view.setOnLongClickListener(local_mainActivity);
       return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Stock stock = list.get(i);
        if (stock.getPriceChange() < 0){
            setRedColor(holder);
        }
        else {
            setGreenColor(holder);
        }
        setDetails(holder, stock);
    }

    private void setDetails(MyViewHolder holder, Stock stock) {
        holder.companyName.setText(stock.getStockName());
        holder.companySymbol.setText(stock.getStockSymbol());
        holder.price.setText(String.format(Locale.US, "%.2f", stock.getStockPrice()));
        holder.priceChange.setText(String.format(Locale.US, "%.2f", stock.getPriceChange()));
        holder.changePercentage.setText(String.format(Locale.US, "(%.2f%%)", stock.getPricePercentge()));
    }

    private void setRedColor(MyViewHolder holder) {
        holder.companyName.setTextColor(Color.RED);
        holder.companySymbol.setTextColor(Color.RED);
        holder.price.setTextColor(Color.RED);
        holder.priceChange.setTextColor(Color.RED);
        holder.changePercentage.setTextColor(Color.RED);
        holder.dividerView.setBackgroundColor(Color.RED);
        holder.arrow.setImageResource(R.drawable.ic_arrow_down);
        holder.arrow.setColorFilter(Color.RED);
    }

    private void setGreenColor(MyViewHolder holder) {
        holder.companyName.setTextColor(Color.GREEN);
        holder.companySymbol.setTextColor(Color.GREEN);
        holder.price.setTextColor(Color.GREEN);
        holder.priceChange.setTextColor(Color.GREEN);
        holder.changePercentage.setTextColor(Color.GREEN);
        holder.dividerView.setBackgroundColor(Color.GREEN);
        holder.arrow.setImageResource(R.drawable.ic_arrow_up);
        holder.arrow.setColorFilter(Color.GREEN);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
