package com.example.apimodel;

public class GetMillis {
    public static double get_millis(double hour,double minute){
        return (hour*60+minute)*60*1000;
    }
}
