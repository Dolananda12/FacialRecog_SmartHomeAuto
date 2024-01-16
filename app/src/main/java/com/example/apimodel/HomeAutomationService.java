package com.example.apimodel;
import java.time.LocalTime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static android.Manifest.permission.RECORD_AUDIO;

public class HomeAutomationService extends AppCompatActivity implements RecieveMillisfromFirestore.RecieveMillisfromFirestorelistener {

    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private SpeechRecognizer speechRecognizer;
    private Intent intentRecognizer;
    private TextView textView;
    private Button startButton;
    private Button stopButton;
    private int fan_light=-1;
    private  int bazooka=-1;
    private  int index=0;
    private int on_off=-1;
    double[] arraycontainigtime=new double[3];
    String[] appliance= new String[]{"fan", "light", "pump"};
    private ArrayList<String> wordsList;
    private BluetoothSocket bluetoothSocket=null;
    private String hc05MacAddress = "00:21:13:01:D4:91";
    private ArrayList<String> a;
    private EditText fanbutton;
    private Button fann;
    public static final String SERVICE_ID = "00001101-0000-1000-8000-00805f9b34fb";
    private BluetoothAdapter bluetoothAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_automation);
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        connectToBluetoothDevice();
        Button consumptionButton=findViewById(R.id.consumption);
        a=new ArrayList<>();
        textView = findViewById(R.id.textview);
        startButton = findViewById(R.id.start1);
        stopButton = findViewById(R.id.stop1);
        fanbutton=findViewById(R.id.editext);
        fann=findViewById(R.id.fan3);
        ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, PackageManager.PERMISSION_GRANTED);
        intentRecognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                // Called when the recognizer is ready for speech input
            }

            @Override
            public void onBeginningOfSpeech() {
                // Called when the user starts speaking
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matches= bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String string = "";
                if (matches != null && !matches.isEmpty()) {
                    string=matches.get(0);
                    String[] words = matches.get(0).split(" "); // Split the recognized speech into words
                    wordsList = new ArrayList<>(Arrays.asList(words));
                    textView.setText(string);
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {
                // Called when partial recognition results are available
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
                // Called when an event related to recognition occurs
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechRecognizer.startListening(intentRecognizer);
            }
        });
        stopButton.setOnClickListener(v -> {
            if(bluetoothSocket==null||bluetoothAdapter==null) connectToBluetoothDevice();
            textView.setText("");
            decipher(wordsList);
        });
        fann.setOnClickListener(v -> {
            String b=fanbutton.getText().toString();
            if(b.equals("1")) send_signal(6);
            if(b.equals("2")) send_signal(7);
            if(b.equals("3")) send_signal(8);
            if(b.equals("4")) send_signal(9);
        });
        consumptionButton.setOnClickListener(v -> {
            index=0;
            new RecieveMillisfromFirestore(this,appliance[index]);
        });
    }
    private void showToast(String d){
        Toast.makeText(this, d, Toast.LENGTH_SHORT).show();
    }

    void connectToBluetoothDevice() {
        BluetoothDevice hc05 = bluetoothAdapter.getRemoteDevice(hc05MacAddress);
        try {
            showToast("connecting");
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.BLUETOOTH,
                        android.Manifest.permission.BLUETOOTH_ADMIN,
                        android.Manifest.permission.BLUETOOTH_CONNECT
                }, REQUEST_BLUETOOTH_PERMISSIONS);
            }
            bluetoothSocket = hc05.createRfcommSocketToServiceRecord(UUID.fromString(SERVICE_ID));
            bluetoothSocket.connect();
            showToast("connected to bluetooth device: " + bluetoothSocket.isConnected());
        } catch (IOException e) {
            showToast("Failed to connect to the Bluetooth device.");
            e.printStackTrace();
        }
    }
    protected void decipher(ArrayList<String> dola) {
        if(dola!=null) {
            for (int i = 0; i < dola.size(); i++) {
                String to_search = dola.get(i);
                if (to_search.equals("on")) {
                    on_off = 1;
                }
                if (to_search.equals("of") || to_search.equals("off")) {
                    on_off = 0;
                }
                if (to_search.equals("fan")) {
                    fan_light = 1;
                }
                if (to_search.equals("light")) {
                    fan_light = 0;
                }
                if (to_search.equals("pump")) {
                    fan_light = 2;
                }

            }
        if(fan_light==0||fan_light==1){
            int d=2*fan_light+on_off;
            send_signal(d);
            sendto_millis(d);
        }else{
            if(on_off==0){
                send_signal(4);
                sendto_millis(4);
            }
            else{
                send_signal(5);
                sendto_millis(5);
            }
        }
        }
    }
    protected void send_signal(int d) {
        if (bluetoothSocket != null) {
            try {
                OutputStream outputStream = bluetoothSocket.getOutputStream();
                String ball = Integer.toString(d);
                byte[] bytes = ball.getBytes();
                outputStream.write(bytes);
                outputStream.flush();
                showToast("sent to arduino");
                System.out.println(d);
            } catch (IOException e) {
                e.printStackTrace();
                showToast("Error sending signal: " + e.getMessage());
            }
        } else {
            showToast("Bluetooth connection failure");
        }
    }
    protected void sendto_millis(int bazooka){
        LocalTime currentTime = LocalTime.now();
        int currentHour = currentTime.getHour();
        int currentMinute = currentTime.getMinute();
        if(bazooka==1) {RecieveMillisfromFirestore.l=1;SendtoFirestore.updateFieldInFirestore("light",GetMillis.get_millis(currentHour,currentMinute),this);}
        else if(bazooka==0) {RecieveMillisfromFirestore.l=0;}
        else if(bazooka==2) {RecieveMillisfromFirestore.f=0;}
        else if(bazooka==3) {RecieveMillisfromFirestore.f=1;SendtoFirestore.updateFieldInFirestore("fan",GetMillis.get_millis(currentHour,currentMinute),this);}
        else if(bazooka==5) {RecieveMillisfromFirestore.p=1;SendtoFirestore.updateFieldInFirestore("pump",GetMillis.get_millis(currentHour,currentMinute),this);}
        else if(bazooka==4) {RecieveMillisfromFirestore.p=0;}
    }

    @Override
    public void onRecieval(double millis) {
        arraycontainigtime[index]=millis;
        System.out.println(appliance[index]+" :"+millis);
        index++;
        if(index<3)new RecieveMillisfromFirestore(this,appliance[index]);
        else if(index==3){
            RecieveMillisfromFirestore.data=arraycontainigtime;
            Intent intent=new Intent(HomeAutomationService.this, DisplayRealTimeData.class);
            startActivity(intent);
        }
    }

    @Override
    public void onfailure(String error) {
        System.out.println(error);
    }
}