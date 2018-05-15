package com.example.shamshadh.assignmentwork;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class add_activity extends AppCompatActivity implements View.OnClickListener{

    private Button upload;
    private Button select_image;
    private EditText caption;
    private ImageView imageView;
    private Uri filepath;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private  final int PICK_IMAGE_REQUEST=71;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);
        getSupportActionBar().hide();
        upload=findViewById(R.id.upload);
        upload.setOnClickListener(this);
        select_image=findViewById(R.id.selectImage);
        select_image.setOnClickListener(this);
        imageView=findViewById(R.id.image_select);

        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        caption=findViewById(R.id.caption);

    }

    @Override
    public void onClick(View v) {
        if (v == upload) {
            uploadImage();

        }
        if(v==select_image){
            chooseImage();
        }
    }

    private void uploadImage() {
        if(filepath!=null){
            final ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("Uploading..");
            progressDialog.show();

            StorageReference ref=storageReference.child("image/"+ UUID.randomUUID().toString());
            ref.putFile(filepath).
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(add_activity.this, "Uploading Finished", Toast.LENGTH_SHORT).show();
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("post").push();
                            String name = caption.getText().toString().trim();
                            mRef.child("text").setValue(name);
                            mRef.child("like").setValue(String.valueOf(0));
                            mRef.child("image").setValue(String.valueOf(downloadUrl));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(add_activity.this, "Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100*(taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount()));
                    progressDialog.setMessage("uploading.."+(int)progress+"%");
                }
            });

        }
    }

    private void chooseImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){

            filepath=data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
