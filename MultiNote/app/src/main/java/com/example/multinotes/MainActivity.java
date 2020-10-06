package com.example.multinotes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private final List<Notes> notesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private int pos;
    private static final int REQ_ID = 100;
    private NotesAdapter notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler);
        notesAdapter = new NotesAdapter(notesList, this);
        recyclerView.setAdapter(notesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadNotes();
    }

    @Override
    public void onClick(View view) {
        pos = recyclerView.getChildAdapterPosition(view);
        Intent intent =new Intent(this, EditActivity.class);
        intent.putExtra("Title",notesList.get(pos).getTitle());
        intent.putExtra("TimeStamp",notesList.get(pos).getTimestamp());
        intent.putExtra("Data",notesList.get(pos).getData());
        startActivityForResult(intent,REQ_ID);
    }

    @Override
    public boolean onLongClick(View view) {
        pos = recyclerView.getChildAdapterPosition(view);
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                notesList.remove(pos);
                notesAdapter.notifyDataSetChanged();
                pos = -1;
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pos = -1;
            }
        });
        builder.setMessage("Are you sure you want to delete this note?");
        builder.setTitle("Delete");
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.info:
                Intent intent = new Intent(this,AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.add:
                Intent intent1 = new Intent(this,EditActivity.class);
                startActivityForResult(intent1, REQ_ID);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        saveNotes();
        super.onPause();
    }

    @Override
    protected void onResume() {
        notesList.size();
        super.onResume();
        notesAdapter.notifyDataSetChanged();
    }

    private void loadNotes() {

        try {

            InputStream is = getApplicationContext().openFileInput("notes.json");
            JsonReader reader = new JsonReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String note;

            reader.beginObject();

            while (reader.hasNext()) {
                note = reader.nextName();
                if (note.equals("notes")) {

                    reader.beginArray();
                    while (reader.hasNext()) {
                        Notes tempNotes = new Notes();
                        reader.beginObject();
                        while(reader.hasNext()) {
                            note = reader.nextName();
                            switch (note) {
                                case "title":
                                    tempNotes.setTitle(reader.nextString());
                                    break;
                                case "timestamp":
                                    tempNotes.setTimestamp(reader.nextString());
                                    break;
                                case "note":
                                    tempNotes.setData(reader.nextString());
                                    break;
                                default:
                                    reader.skipValue();
                                    break;
                            }
                        }
                        reader.endObject();
                        getNotesList().add(tempNotes);

                    }
                    reader.endArray();
                }
                else{
                    reader.skipValue();
                }

            }
            reader.endObject();

        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: "+e);
        }
    }


    private void saveNotes() {

        try {
            FileOutputStream fos = getApplicationContext().openFileOutput("notes.json", Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("notes");
            writeNotesArray(writer);
            writer.endObject();
            writer.close();

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void writeNotesArray(JsonWriter writer) throws IOException {
        writer.beginArray();
        for (Notes i : notesList) {
            writeNotesObject(writer, i);
        }
        writer.endArray();
    }

    public void writeNotesObject(JsonWriter jsonWriter, Notes val) throws IOException{
        jsonWriter.beginObject();
        jsonWriter.name("title").value(val.getTitle());
        jsonWriter.name("timestamp").value(val.getTimestamp());
        jsonWriter.name("note").value(val.getData());
        jsonWriter.endObject();
    }


    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ID) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Notes editNotes = (Notes) Objects.requireNonNull(data.getExtras()).getSerializable("Note");
                String status = data.getStringExtra("Status");
                assert status != null;
                if (status.equals("No Change")) {
                } else if (status.equals("New")) {
                    notesList.add(0, editNotes);
                } else if (status.equals("Change")) {
                    notesList.remove(pos);
                    notesList.add(0, editNotes);
                }
            }
        }
    }

    public List<Notes> getNotesList() {
        return notesList;
    }

}