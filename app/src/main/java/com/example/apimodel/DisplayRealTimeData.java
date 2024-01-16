package com.example.apimodel;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalTime;

public class DisplayRealTimeData extends AppCompatActivity {
    TextView fan;
    TextView light;
    TextView pump;

    private Handler handler;
    private boolean stopUpdates = false;
    double fan_duration = 0, light_duration = 0, pump_duration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_data);
        pump = findViewById(R.id.pump);
        fan = findViewById(R.id.fan);
        light = findViewById(R.id.light);
        Button clear=findViewById(R.id.clear);
        handler = new Handler();
        startUpdatingTextViews();
        clear.setOnClickListener(v -> {
            fan_duration=0;
            light_duration=0;
            pump_duration=0;
            stopUpdates=true;
        });
    }

    private void startUpdatingTextViews() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!stopUpdates) {
                    double[] data = RecieveMillisfromFirestore.data;
                    LocalTime currentTime = LocalTime.now();
                    int currentHour = currentTime.getHour();
                    int currentMinute = currentTime.getMinute();
                    Double time= (double) GetMillis.get_millis(currentHour,currentMinute);
                    if (data[0] == 0||RecieveMillisfromFirestore.f==0) {
                        fan.setText("fan: " + converttoSeconds(data[0]));
                    }else{
                        fan_duration += time - data[0];
                        fan.setText("fan: " + converttoSeconds(fan_duration));
                    }
                    if (data[1] == 0||RecieveMillisfromFirestore.l==0) {
                        light.setText("light: " + converttoSeconds(data[1]));
                    }else{
                        light_duration += time - data[1];
                        light.setText("light: " +light_duration);
                    }
                    if (data[2] == 0||RecieveMillisfromFirestore.p==0) {
                        pump.setText("pump: " + converttoSeconds(data[2]));
                    }else{
                        pump_duration +=time - data[2];
                        pump.setText("pump: " + converttoSeconds(pump_duration));
                    }
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        SendtoFirestore.updateFieldInFirestore("fan",fan_duration,this);
        SendtoFirestore.updateFieldInFirestore("light",light_duration,this);
        SendtoFirestore.updateFieldInFirestore("pump",pump_duration,this);
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
        stopUpdates = true;
    }

    public static double converttoSeconds(double millis){
        return  millis/1000;
    }
}
