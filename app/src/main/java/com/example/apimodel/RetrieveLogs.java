package com.example.apimodel;

import android.util.Pair;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RetrieveLogs {
    public RetrieveLogslistener listener;
    public interface RetrieveLogslistener{
        void onRecieval(String data);
        void onFailure(String errorMessage);
    }
    public RetrieveLogs(RetrieveLogslistener listener){
        this.listener=listener;
         recieve();
    }

    private void recieve(){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("Entries:\n");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Last24hrs_Entries")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                String name = document.getString("name");
                                String time = document.getString("time");
                                String date=document.getString("date");
                                stringBuilder.append("name:"+name+" \ntime:"+time+" \ndate:"+date+"\n");
                            }
                        }
                        listener.onRecieval(stringBuilder.toString());
                    } else {
                        listener.onFailure("task failure");
                    }
                });
    }
}
