package com.example.riddhi.imageuploader;

import  android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import android.app.ProgressDialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class MainActivity extends AppCompatActivity {


    private Button upload; //  this is not the upload button its the select file button

    private static int Result_load_image = 1;
    private StorageReference mStorageRef;
    public Uri fileUri;
    public Uri file;
    public int TotalItemsSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        upload = (Button) findViewById(R.id.button2);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "select pictures"), Result_load_image);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == Result_load_image && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                Toast.makeText(MainActivity.this, "Selected Multiple images", Toast.LENGTH_SHORT).show();
                TotalItemsSelected = data.getClipData().getItemCount();
                for (int i = 0; i <= TotalItemsSelected; i++) {
                    fileUri = data.getClipData().getItemAt(i).getUri();
                }
                //String fileName= getFileName(fileUri);
                StorageReference filetoupload = mStorageRef.child("Images").child("/username");
                filetoupload.putFile(fileUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(MainActivity.this, "uploaded successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "\"failed\"+e.getMessage()", Toast.LENGTH_SHORT).show();
                            }

                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(MainActivity.this, "uploading..", Toast.LENGTH_SHORT).show();
                            }
                        });
            }


        } else if (data.getData() != null) {
            Toast.makeText(MainActivity.this, "Selected single file", Toast.LENGTH_SHORT).show();
        }

    }
}






   // public String getFileName(Uri uri){
     //   String result = null;
      //  if(uri.getScheme().equals("content")){
        //    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
           // try{

          //  }
        //}
   // }


