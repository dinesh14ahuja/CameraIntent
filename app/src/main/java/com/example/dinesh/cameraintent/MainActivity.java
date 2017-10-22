package com.example.dinesh.cameraintent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Button btnCam,btnGall;
    ImageView imageView;
    public static final int Camera_code=123;
    public static final int Gallery_code=888; //Any random nunmber
    String mCurrentPhotoPath ;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCam=(Button)findViewById(R.id.btnCam);
        btnGall=(Button)findViewById(R.id.btn_Gall);
        imageView=(ImageView)findViewById(R.id.imageView);
    }

    public void btn_Cam(View v) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Here we are Capturing image
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

            }
            startActivityForResult(intent, Camera_code);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    public void btn_Gall(View v){
        Intent i = new Intent(Intent.ACTION_PICK);    //Here we are going to gallery to pick an image
        i.setType("image/*"); //You can specify the type of image to select
        //I'm using default to see all types of images
        startActivityForResult(i,Gallery_code);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode== Activity.RESULT_OK){
            switch (requestCode) {
                case Camera_code:
                                //If image is captured them result will be ok
                                //Here we will be using Bitmap to get that image
                   try {
                       bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),Uri.parse(mCurrentPhotoPath));
                       imageView.setImageBitmap(bitmap);
                       Toast.makeText(MainActivity.this,"Image saved in Gallery",Toast.LENGTH_LONG).show();
                       //send broadcast is used to update gallery or else image will be shown after restarting cell
                       sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(mCurrentPhotoPath)));
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
                    break;
               case Gallery_code:
                       Uri uri =data.getData(); // uri is the path of image
                   try {
                       bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri); //contentResolver should be surrounded by try catch
                       imageView.setImageBitmap(bitmap); //seting preview of image in our imageView
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
           }
        }
    }

}
