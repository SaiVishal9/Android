package com.example.distanceconverter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private EditText userText;
    private TextView output;
    private TextView label1;
    private TextView label2;
    private TextView outHistory;
    private double res;
    private int flag = 1;
    private static DecimalFormat df = new DecimalFormat("0.0");
    String out = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userText = findViewById(R.id.editTextNumberDecimal);
        output = findViewById(R.id.Result);
        label1 = findViewById(R.id.Value_Label_1);
        label2 = findViewById(R.id.Value_Label_2);
        outHistory = findViewById(R.id.Result_History);
        outHistory.setMovementMethod(new ScrollingMovementMethod());
    }

    @SuppressLint("SetTextI18n")
    public void radioClicked(View v)
    {
        switch(v.getId())
        {
            case R.id.radioButton:
                label1.setText("Miles Value:");
                label2.setText("Kilometres Value:");
                flag = 1;
                break;
            case R.id.radioButton2:
                label1.setText("Kilometres Value:");
                label2.setText("Miles Value:");
                flag = 2;
                break;
        }
    }

    @SuppressLint("DefaultLocale")
    public void convertValue(View v)
    {
        String input = userText.getText().toString();
        if(input.equals("")) Toast.makeText(this,"Enter Value",Toast.LENGTH_SHORT).show();
        else {
            double d = Double.parseDouble(input);
            if (flag == 1)
            {
                res = d * 1.60934;
                out = d + " Mi ==> " + String.format("%.1f", res) + " Km\n" + out;
            }
            else if (flag == 2)
            {
                res = d * 0.621371;
                out = d + " Km ==> " + String.format("%.1f", res) + " Mi\n" + out;
            }
            userText.getText().clear();
            output.setText(df.format(res));
            outHistory.setText(out);
        }
    }

    public void clearValue(View v)
    {
        outHistory.setText("");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString("HISTORY", output.getText().toString());
        outState.putDouble("VALUE", res);
        outState.putString("History_All", outHistory.getText().toString());
        outState.putInt("Flag",flag);
        outState.putString("Label1",label1.getText().toString());
        outState.putString("Label2",label2.getText().toString());
        outState.putString("Output", out);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        output.setText(savedInstanceState.getString("HISTORY"));
        res = savedInstanceState.getDouble("VALUE");
        outHistory.setText(savedInstanceState.getString("History_All"));
        flag = savedInstanceState.getInt("Flag");
        label1.setText(savedInstanceState.getString("Label1"));
        label2.setText(savedInstanceState.getString("Label2"));
        out = savedInstanceState.getString("Output");
    }

}