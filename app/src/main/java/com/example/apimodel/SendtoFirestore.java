package com.example.apimodel;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.fazecast.jSerialComm.SerialPort;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;

public class SendtoFirestore {

    public SendtoFirestore(String time,String name,String date){
        SendEntriestoFirestore(time,name,date);
    }
    public SendtoFirestore(){

    }
    private static void SendEntriestoFirestore(String time,String name,String date){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, String> user = new HashMap<>();
        user.put("time", time);
        user.put("name", name);
        user.put("date",date);
        db.collection("Last24hrs_Entries")
                .add(user)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));

    }
    public static void updateFieldInFirestore(String fieldName, Object updatedValue, Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Action_Trigger_Time").document("QxUYJBPhloRTy9Spe4hl");
        Map<String,Object> updates = new HashMap<>();
        updates.put(fieldName, updatedValue);
        System.out.println(fieldName+" "+"updated value:"+updatedValue);
        docRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    ((Activity)context).runOnUiThread(() -> Toast.makeText(context, fieldName+":updated successfully", Toast.LENGTH_SHORT).show());
                    Log.d(TAG, "Document field updated successfully");
                })
                .addOnFailureListener(e -> {
                    ((Activity)context).runOnUiThread(() -> Toast.makeText(context, "Error updating document field", Toast.LENGTH_SHORT).show());
                    Log.w(TAG, "Error updating document field", e);
                });
    }
}
