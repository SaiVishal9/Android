package com.example.MAD.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class OfficialViewHolder extends RecyclerView.ViewHolder{
    public TextView office;
    public TextView nameAndParty;

    public OfficialViewHolder(View view) {
        super(view);
        office = (TextView) view.findViewById(R.id.location_TextView);
        nameAndParty = (TextView) view.findViewById(R.id.nameAndParty_TextView);
    }
}
