package com.example.apimodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageUtils {

    private ImageUtilsListner listener;

    public interface ImageUtilsListner {
        void onCompletetionOfConversion(String encodedString);
        void onFailOfTransfer(String errorMessage);
    }

    public ImageUtils(ImageUtils.ImageUtilsListner listener) {
        this.listener = listener;
    }
    public String convertBitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) {
            listener.onFailOfTransfer("error converting");
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        listener.onCompletetionOfConversion(Base64.getEncoder().encodeToString(byteArray));
        System.out.println(Base64.getEncoder().encodeToString(byteArray).length());
        return Base64.getEncoder().encodeToString(byteArray);
    }
}
