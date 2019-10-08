package com.thoms.silonaanak;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.thoms.silonaanak.Model.AnakDaftar;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class DaftarActivity extends AppCompatActivity {

    Button btnDaftar,btnCekUsername;
    EditText etNama,etEmail,etPassword,etUsername;
    FirebaseAuth auth;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    DatabaseReference ref;
    ImageView ep_profil;
    private FirebaseAuth.AuthStateListener fStateListener;

    static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    static final int RC_IMAGE_GALLERY = 2;
    Uri uriGambar,file;
    List<String> listUsername = new ArrayList<>();
    boolean cek;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("/font/hangyaboly.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        FirebaseApp.initializeApp(DaftarActivity.this);
        ref = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak");
        auth = FirebaseAuth.getInstance();

        btnDaftar = findViewById(R.id.btnDaftar);
        btnCekUsername = findViewById(R.id.btnCekUsername);
        etNama = findViewById(R.id.input_nama);
        etEmail = findViewById(R.id.input_email);
        etPassword = (EditText)findViewById(R.id.input_password);
        etPassword.setTypeface(Typeface.DEFAULT);
        etPassword.setTransformationMethod(new PasswordTransformationMethod());
        etUsername = findViewById(R.id.input_username);

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    checkValidation();
            }
        });
        btnCekUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cekAvaibilityUsername() == true){
                    new SweetAlertDialog(DaftarActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Username tersedia")
                            .setContentText("Anda dapat menggunakan username ini")
                            .show();
                }else {
                    new SweetAlertDialog(DaftarActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Username tidak tersedia")
                            .setContentText("Silakan ganti username anda")
                            .show();
                }
            }
        });

        pDialogLoading = new SweetAlertDialog(DaftarActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);

        getDataUsername();

    }

    public void getDataUsername(){
        listUsername.clear();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    String usernameDiDB = child.child("username").getValue().toString();
                    Log.d("usernameDiDB:",usernameDiDB);
                    listUsername.add(usernameDiDB);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private boolean cekAvaibilityUsername(){
        final String username  = etUsername.getText().toString();
        cek = false;

        if (username.equals("") || username.length() == 0){
            new SweetAlertDialog(DaftarActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Username harus diisi")
                    .show();

        }else {
            if (!listUsername.contains(username)){
                cek = true;
            }
        }

        Log.d("cekUsername:",""+cek);
        return cek;
    }

    private void checkValidation(){
        String getNama = etNama.getText().toString();
        String getEmail = etNama.getText().toString();
        String getPassword = etPassword.getText().toString();


        if (getNama.equals("") || getNama.length() == 0
                || getPassword.equals("") || getPassword.length() == 0
                || getEmail.equals("") || getEmail.length() == 0
        ) {

            new SweetAlertDialog(DaftarActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Semua Field Harus diisi")
                    .show();

        }else if (cekAvaibilityUsername() == false){
            new SweetAlertDialog(DaftarActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Username tidak tersedia")
                    .setContentText("Silakan ganti username anda")
                    .show();
        }
        else {
            daftar();
        }
    }

    private void daftar(){

        pDialogLoading.show();

        String username = etUsername.getText().toString();

        final AnakDaftar anakDaftar = new AnakDaftar(
            etNama.getText().toString(),
                username,
                etEmail.getText().toString(),
                etPassword.getText().toString(),
                "na",
                "na",
                "false",
                "na",
                "na",
                "na"
        );

        auth.createUserWithEmailAndPassword(etEmail.getText().toString(),etPassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()){
                    pDialogLoading.dismiss();
                    Log.e("Erordaftar ",task.getException().toString());
                    new SweetAlertDialog(DaftarActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Terjadi kesalahan, coba lagi nanti")
                            .show();
                }else {
                    pDialogLoading.dismiss();

                    FirebaseUser user = auth.getCurrentUser();
                    String userID =  auth.getCurrentUser().getUid();
                    String token  = FirebaseInstanceId.getInstance().getToken();
                    //cek tanggal skrg

                    //ganti nama
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(etNama.getText().toString()).build();
                    user.updateProfile(profileChangeRequest);

                    anakDaftar.setUserid(userID);

                    ref.child(userID).setValue(anakDaftar);
                    new SweetAlertDialog(DaftarActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Pendaftaran Berhasil !")
                            .setContentText("Akun mu telah dibuat, silakan kembali ke halaman login")
                            .show();

                    etPassword.setText("");
                    etNama.setText("");
                    etEmail.setText("");
                    etUsername.setText("");

                }
            }
        });



    }

    public static String GetMimeType(Context context, Uri uriImage)
    {
        String strMimeType = null;

        Cursor cursor = context.getContentResolver().query(uriImage,
                new String[] { MediaStore.MediaColumns.MIME_TYPE },
                null, null, null);

        if (cursor != null && cursor.moveToNext())
        {
            strMimeType = cursor.getString(0);
        }

        return strMimeType;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, RC_IMAGE_GALLERY);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_IMAGE_GALLERY && resultCode == RESULT_OK) {
            uriGambar = data.getData();
            final String tipe = GetMimeType(DaftarActivity.this,uriGambar);
            //   Toast.makeText(TambahWisata.this, "Tipe : !\n" + tipe, Toast.LENGTH_LONG).show();

            ep_profil.setImageURI(uriGambar);
        }
        else if (requestCode == 100 && resultCode == RESULT_OK){
            uriGambar = file;
            ep_profil.setImageURI(uriGambar);
        }


    }
}
