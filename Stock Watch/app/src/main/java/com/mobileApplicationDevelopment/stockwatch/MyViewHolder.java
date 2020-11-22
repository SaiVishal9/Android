package com.mobileApplicationDevelopment.stockwatch;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView companyName;
    public TextView companySymbol;
    public TextView price;
    public TextView priceChange;
    public TextView changePercentage;
    public ImageView arrow;
    public View dividerView;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        companyName = itemView.findViewById(R.id.companyname);
        companySymbol = itemView.findViewById(R.id.companysymbol);
        price = itemView.findViewById(R.id.price);
        priceChange = itemView.findViewById(R.id.pricechange);
        changePercentage = itemView.findViewById(R.id.changepercentage);
        arrow = itemView.findViewById(R.id.arrowImage);
        dividerView = itemView.findViewById(R.id.divider);
    }
}
