package com.example.apimodel;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RecieveMillisfromFirestore {
    public static int f=0;
    public static int l=0;
    public static int p=0;
    public RecieveMillisfromFirestorelistener listener;
    public static double[] data=new double[3];
    public interface RecieveMillisfromFirestorelistener{
        void onRecieval(double millis);
        void onfailure(String error);
    }
    public RecieveMillisfromFirestore(RecieveMillisfromFirestorelistener listener,String appliance){
        this.listener=listener;
        recieve(appliance);
    }
    private void recieve(String appliance){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Action_Trigger_Time").document("QxUYJBPhloRTy9Spe4hl");
        System.out.println("appliance"+appliance);
        docRef.get().addOnCompleteListener(task ->{
           if(task.isSuccessful()){
               DocumentSnapshot doc=task.getResult();
               if(doc.exists()){
                   listener.onRecieval(doc.getDouble(appliance));
               }else{
                   System.out.println("doc doesnt exist");
               }
           }else{
               System.out.println("error");
           }
        });
    }

}