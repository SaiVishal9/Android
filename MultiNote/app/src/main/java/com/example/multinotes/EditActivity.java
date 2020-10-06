package com.example.multinotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class EditActivity extends AppCompatActivity {

    EditText editTitle;
    EditText editData;
    String getTitle = "";
    String getData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);

        editTitle = findViewById(R.id.edit_title);
        editData = findViewById(R.id.edit_note);

        Intent intent = getIntent();
        if(intent.hasExtra("Title")){
            getTitle = intent.getStringExtra("Title");
            editTitle.setText(getTitle);
        }
        if(intent.hasExtra("Data")){
            getData = intent.getStringExtra("Data");
            editData.setText(getData);
        }
        editData.setMovementMethod(new ScrollingMovementMethod());
        editData.setGravity(Gravity.TOP);
    }

    @Override
    public void onBackPressed() {
        if(editTitle.getText().toString().isEmpty()){
            Toast.makeText(this, "Title is empty, note was not saved", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
        else if(getTitle.equals(editTitle.getText().toString()) && getData.equals(editData.getText().toString())){
            super.onBackPressed();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    saveNotes();
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            builder.setMessage("Do you want to save the Content?");
            builder.setTitle("Note Save");
            //AlertDialog dialog = builder.create();
            builder.show();
        }
    }

    public void saveNotes(){
        Notes new_Note = new Notes();
        new_Note.setTitle(editTitle.getText().toString());
        new_Note.setTimestamp(Calendar.getInstance().getTime().toString());
        new_Note.setData(editData.getText().toString());
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("Note",new_Note);
        if(editTitle.getText().toString().isEmpty()){
            intent.putExtra("Status","No Change");
            Toast.makeText(this,"Title is empty, it cannot be saved",Toast.LENGTH_SHORT).show();
        }
        else if(getTitle.isEmpty() && getData.isEmpty()){
            intent.putExtra("Status","New");
        }
        else if(getTitle.equals(editTitle.getText().toString()) && getData.equals(editData.getText().toString())){
            intent.putExtra("Status","No Change");
        }
        else{
            intent.putExtra("Status","Change");
        }
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.save) {
            saveNotes();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        editTitle.setText(savedInstanceState.getString("Title"));
        editData.setText(savedInstanceState.getString("Data"));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("Title", editTitle.getText().toString());
        outState.putString("Data", editData.getText().toString());
        super.onSaveInstanceState(outState);
    }

}
