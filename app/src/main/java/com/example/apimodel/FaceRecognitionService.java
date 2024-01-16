package com.example.apimodel;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
public class FaceRecognitionService extends AppCompatActivity implements CompareFaces.CompareFacesListner, ImageUtils.ImageUtilsListner,RetrieveLogs.RetrieveLogslistener {
    private static final int PICK_IMAGE = 1001;
    private static final int REQUEST_IMAGE_CAPTURE = 1002;
    private EditText nameField;
    private double confidence = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Button compareButton = findViewById(R.id.comp);
        nameField = findViewById(R.id.editext);
        Button uploadButton = findViewById(R.id.uploadPhoto);
        Button logButton =findViewById(R.id.logButton);
        compareButton.setOnClickListener(v -> {
            Intent cameraIntent = getCameraIntent();
            if (cameraIntent != null) {
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                showToast("No camera app available");
            }
        });
        uploadButton.setOnClickListener(v -> {
            dispatchTakePictureIntent();
        });
        logButton.setOnClickListener(v -> {
            new RetrieveLogs(this);
        });
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        takePictureIntent.setType("image/*"); // Set the type of content to image
        Intent chooserIntent = Intent.createChooser(takePictureIntent, "Choose Image");
        if (chooserIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(chooserIntent, PICK_IMAGE);
        }
    }

    private Intent getCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            return takePictureIntent;
        }
        return null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        showToast("opening faial service");
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                if (data != null && data.getData() != null) {
                    Bitmap bitmap = getBitmapFromURI(this, data.getData());
                    if (bitmap != null) {
                        new SendImagetoFireBase(nameField.getText().toString(),bitmap);
                    } else {
                        showToast("Error: Image capture failed");
                    }
                    nameField.setText("");
                    showToast("image sent to cloudStore");
                } else {
                    showToast("Error: No image selected");
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bitmap imageBitmap;
                if (data != null && data.getExtras() != null) {
                    imageBitmap = (Bitmap) data.getExtras().get("data");
                    if (imageBitmap != null) {
                        ImageUtils imageUtils=new ImageUtils(this);
                        imageUtils.convertBitmapToBase64(imageBitmap);
                    } else {
                        showToast("Error: Image capture failed");
                    }
                } else {
                    showToast("Error: No image data received");
                }
            }
        }
    }

    private Bitmap getBitmapFromURI(Context context, Uri contentUri) {
        Bitmap bitmap = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(projection[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            if (filePath != null) {
                bitmap = BitmapFactory.decodeFile(filePath);
            }
        }
        return bitmap;
    }
    @Override
    public void onResult(String name) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        new SendtoFirestore(timeFormat.format(calendar.getTime()),name,dateFormat.format(calendar.getTime()));
        runOnUiThread(() -> {
            showToast("Hello: " + name);
            confidence=0;
        });
    }

    @Override
    public void onError(String errorMessage) {

         runOnUiThread(() -> {
             if(confidence==-1){
                 showToast(errorMessage);
             }
         });
    }


    @Override
    public void onCompletetionOfConversion(String encodedString) {
        new CompareFaces(this, encodedString,FaceRecognitionService.this);
    }

    @Override
    public void onFailOfTransfer(String errorMessage) {
        System.out.println(errorMessage);
    }

    @Override
    public void onRecieval(String data) {
        EntriesLog.display_data=data;
        System.out.println("data came");
        System.out.println(data);
        Intent intent=new Intent(this,EntriesLog.class);
        startActivity(intent);
    }

    @Override
    public void onFailure(String errorMessage) {
        System.out.println(errorMessage);
    }
}
