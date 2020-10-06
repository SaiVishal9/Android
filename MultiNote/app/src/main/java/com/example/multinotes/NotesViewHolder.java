package com.example.multinotes;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotesViewHolder extends RecyclerView.ViewHolder{

    public TextView title;
    public TextView timestamp;
    public TextView data;

    public NotesViewHolder(@NonNull View view)
    {
        super(view);
        title = view.findViewById(R.id.notes_title);
        timestamp = view.findViewById(R.id.notes_timestamp);
        data = view.findViewById(R.id.notes_data);
    }
}
