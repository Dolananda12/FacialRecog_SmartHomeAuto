package com.example.apimodel;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SendImagetoFireBase implements ImageUtils.ImageUtilsListner{


    public  String name;
    private String encoded;
    private int a=-1;
    public SendImagetoFireBase(String name, Bitmap bitmap) {
        this.name=name;
        ImageUtils utils = new ImageUtils(this); // Pass 'this' as listener
        utils.convertBitmapToBase64(bitmap);
    }

    @Override
    public void onCompletetionOfConversion(String encodedString) {
        sendString_toFirestore(name,encodedString);
    }

    @Override
    public void onFailOfTransfer(String errorMessage) {
        System.out.println(errorMessage);
    }

    private void sendString_toFirestore(String name, String base64) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("Base64", base64);
        user.put("name", name);
        db.collection("Authorized_Persons")
                .add(user)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

}
