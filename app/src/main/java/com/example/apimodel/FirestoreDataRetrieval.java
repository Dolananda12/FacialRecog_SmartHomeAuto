package com.example.apimodel;

import android.util.Pair;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

// Retrieving data from Firestore
public class FirestoreDataRetrieval {

    public static FirestoreDataRetrievalListener listener;

    public interface FirestoreDataRetrievalListener {
        void onRetrievalCompletion(List<Pair<String, String>> dataList);

        void onFailure(String errorMessage);
    }

    public FirestoreDataRetrieval(FirestoreDataRetrievalListener listener) {
        this.listener = listener;
    }

    // Method to retrieve data from Firestore
    public void retrieveDataFromFirestore() {
        System.out.println("retrieving data from cloudstore");
        List<Pair<String, String>> dataList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Authorized_Persons")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                String name = document.getString("name");
                                String base64 = document.getString("Base64");
                                dataList.add(new Pair<>(name, base64));
                            }
                        }
                        listener.onRetrievalCompletion(dataList);
                    } else {
                        listener.onFailure("task failure");
                    }
                });
    }
}
