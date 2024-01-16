package com.example.apimodel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        Button home_auto_service=findViewById(R.id.auto_actions);
        Button face_recog_service=findViewById(R.id.face_recognition);
        home_auto_service.setOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this,HomeAutomationService.class);
            startActivity(intent);
        });
        face_recog_service.setOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this, FaceRecognitionService.class);
            startActivity(intent);
        });
    }
}