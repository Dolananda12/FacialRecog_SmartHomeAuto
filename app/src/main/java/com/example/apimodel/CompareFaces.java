package com.example.apimodel;
import android.app.Activity;
import android.content.Context;
import android.util.Pair;
import android.widget.Toast;

import java.util.*;
public class CompareFaces implements  FaceComparison.FaceComparisonListener, FirestoreDataRetrieval.FirestoreDataRetrievalListener {
    private CompareFacesListner listener;
    private int index=0;
    List<Pair<String, String>> dataList = new ArrayList<>();
    private Context context;
    @Override
    public void onRetrievalCompletion(List<Pair<String, String>> dataList1) {
        dataList=dataList1;
        performFaceComparison(dataList.get(index).second);
    }

    @Override
    public void onFailure(String errorMessage) {
        System.out.println(errorMessage);
    }

    public interface CompareFacesListner {
        void onResult(String name);
        void onError(String errorMessage);
    }
    public CompareFaces(CompareFaces.CompareFacesListner listener, String encodedInputImage, Context context) {
        this.listener = listener;
        this.context=context;
        encodedImagetoCompare=encodedInputImage;
        FirestoreDataRetrieval firestoreDataRetrieval=new FirestoreDataRetrieval(this);
        firestoreDataRetrieval.retrieveDataFromFirestore();
    }
    private int search=-1;
    private double confidence=-1;

    private String encodedImagetoCompare="";
    private String name=" ";

    private void performFaceComparison(String base64Image1) {
        new Thread(() -> {
            FaceComparison faceComparison = new FaceComparison(this);
            faceComparison.faceComparison(base64Image1, encodedImagetoCompare);
        }).start();
    }
    @Override
    public void onComparisonResult(double similarityPercentage) {
        System.out.println("name:"+dataList.get(index).first+"similarity percentage "+similarityPercentage);
        if(similarityPercentage>80.00){
            search=0;
            confidence=similarityPercentage;
            name=dataList.get(index).first;
            listener.onResult(name);
        }
        index++;
        if(search==-1&&index<dataList.size()){
            performFaceComparison(dataList.get(index).second);
            if (context != null) {
                ((Activity) context).runOnUiThread(() -> Toast.makeText(context, "searching...", Toast.LENGTH_SHORT).show());
            }        }else{
            listener.onError("No Match!!");
        }
    }

    @Override
    public void onError(String errorMessage) {
        if (context != null) {
            ((Activity) context).runOnUiThread(() -> Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show());
        }
        System.out.println("Failed!!"+errorMessage);
    }
}
