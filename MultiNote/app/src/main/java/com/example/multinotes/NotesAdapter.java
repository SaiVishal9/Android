package com.example.multinotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Objects;


public class NotesAdapter extends RecyclerView.Adapter<NotesViewHolder>{

    private List<Notes> notesList;
    MainActivity mainAct;

    NotesAdapter(List<Notes> notesList, MainActivity ma) {

        this.notesList = notesList;
        mainAct = ma;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notes_activity, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new NotesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {

        Notes note = notesList.get(position);
        holder.title.setText(note.getTitle());
        holder.timestamp.setText(note.getTimestamp());
        holder.data.setText(note.getData());
        if(note.getData().length() > 80)
        {
            String newData = note.getData().substring(0,79).concat("...");
            holder.data.setText(newData);
        }
        else
        {
            holder.data.setText(note.getData());
        }
    }

    @Override
    public int getItemCount() {
        Objects.requireNonNull(mainAct.getSupportActionBar()).setTitle("Notes "+"("+notesList.size()+")");
        return notesList.size();
    }
}
