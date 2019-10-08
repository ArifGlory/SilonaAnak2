package com.thoms.silonaanak;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChangeAvatarActivity extends AppCompatActivity {

    CircleImageView ivProfPict;
    Button btnKirim;
    static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    static final int RC_IMAGE_GALLERY = 2;
    Uri uri,file;
    private SweetAlertDialog pDialogLoading,pDialodInfo;

    FirebaseUser firebaseUser;
    DatabaseReference ref,anakRef;
    FirebaseAuth auth;
    String imageUrl,uploadedUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_avatar);

        btnKirim = findViewById(R.id.btnKirim);
        ivProfPict = findViewById(R.id.ivProfPict);

        pDialogLoading = new SweetAlertDialog(ChangeAvatarActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);
        pDialogLoading.show();

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        anakRef = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak");

        anakRef.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageUrl = dataSnapshot.child("imageUrl").getValue().toString();

                if (!imageUrl.equals("na") && imageUrl.length() > 10){
                    Glide.with(ChangeAvatarActivity.this)
                            .load(imageUrl)
                            .into(ivProfPict);
                }
                pDialogLoading.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ivProfPict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ChangeAvatarActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChangeAvatarActivity.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, RC_IMAGE_GALLERY);
                }
            }
        });

        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        // menangkap hasil balikan dari Place Picker, dan menampilkannya pada TextView

        if (requestCode == RC_IMAGE_GALLERY && resultCode == RESULT_OK) {
            uri = data.getData();
            ivProfPict.setImageURI(uri);
        }
        else if (requestCode == 100 && resultCode == RESULT_OK){
            uri = file;
            ivProfPict.setImageURI(uri);

        }
    }

    private void checkValidation() {

        // Get all edittext texts

        // Check if all strings are null or not
        if (uri == null) {

            Toast.makeText(getApplicationContext(),"Anda belum memilih gambar",Toast.LENGTH_SHORT).show();
        }
        else{
           pDialogLoading.show();
            uploadFoto();
        }
    }

    public void uploadFoto(){
        final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagesRef = storageRef.child("images");
        StorageReference userRef = imagesRef.child(firebaseUser.getUid());
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = firebaseUser.getUid() + "_" + timeStamp;
        final StorageReference fileRef = userRef.child(filename);

        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloaddUrl = uri;
                        uploadedUrl = downloaddUrl.toString();

                        updateAvatar(uploadedUrl);
                        Toast.makeText(ChangeAvatarActivity.this, "Gambar Tersimpan!", Toast.LENGTH_SHORT).show();
                        Log.d("downloadurl :",uploadedUrl);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChangeAvatarActivity.this, "Upload gagal!\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        /*UploadTask uploadTask = fileRef.putFile(uri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(ChangeAvatarActivity.this, "Upload failed!\n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                pDialogLoading.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               *//* @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getd;*//*

                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        uploadedUrl = uri.toString();
                        Toast.makeText(ChangeAvatarActivity.this, "Upload finished!", Toast.LENGTH_SHORT).show();
                        Log.d("downloadurl :",uploadedUrl);

                        // save  to database
                        updateAvatar(uploadedUrl);
                        Log.d("kirimFoto:",uploadedUrl);
                    }
                });


            }
        });*/
    }

    private void updateAvatar(String uploadedUrl){
        anakRef.child(firebaseUser.getUid()).child("imageUrl").setValue(uploadedUrl);
        pDialogLoading.dismiss();
    }
}
