package com.example.apimodel;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EntriesLog extends AppCompatActivity {
    public static String display_data="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_entries_log);
        EditText showData=findViewById(R.id.showlog);
        showData.setText(display_data);
    }
}
