package com.thoms.silonaanak;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karan.churi.PermissionManager.PermissionManager;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MasukActivity extends AppCompatActivity {
    EditText ed_email, ed_password;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    PermissionManager manager;
    Button btnDaftar;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masuk);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/hangyaboly.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        btnDaftar = findViewById(R.id.btnDaftar);

        if (firebaseUser == null){

            ed_email = (EditText)findViewById(R.id.input_email);
            ed_password = (EditText)findViewById(R.id.input_password);
            ed_password.setTypeface(Typeface.DEFAULT);
            ed_password.setTransformationMethod(new PasswordTransformationMethod());

            reference = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak");
            manager = new PermissionManager() {};

            manager.checkAndRequestPermissions(this);

            btnDaftar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(),DaftarActivity.class);
                    startActivity(i);
                }
            });
        }
        else {
            finish();
            Intent myIntent = new Intent(MasukActivity.this,MapsActivity.class);
            startActivity(myIntent);
        }

    }

    public void LoginUser(View v)
    {
        if (!validate()) {
            onLoginFailed();
            return;
        }
        //   mLoginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(MasukActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        //onLoginSuccess();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        onLoginSuccess();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    private boolean validate() {
        boolean valid = true;

        String email = ed_email.getText().toString();
        String password = ed_password.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ed_email.setError("Masukkan Email dengan Benar");
            valid = false;
        } else {
            ed_password.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 15) {
            ed_password.setError("antara 6 dan 15 karakter alfanumerik ");
            valid = false;
        } else {
            ed_password.setError(null);
        }

        return valid;
    }

    private void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Gagal Masuk", Toast.LENGTH_LONG).show();
    }


    private void onLoginSuccess() {
        final String emailAddress = ed_email.getText().toString().trim();
        final String password = ed_password.getText().toString().trim();
        if (!TextUtils.isEmpty(emailAddress) && !TextUtils.isEmpty(password))
        {


            Query queryUser = reference.orderByChild("email").equalTo(emailAddress);

            queryUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        auth.signInWithEmailAndPassword(emailAddress,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){

                                    startActivity(new Intent(MasukActivity.this, MapsActivity.class));
                                    finish();

                                } else if (!task.isSuccessful()){
                                    Toast.makeText(MasukActivity.this, "\n" +
                                            "Kombinasi nama pengguna / kata sandi salah dimasukkan.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }

                    else
                    {
                        Toast.makeText(getApplicationContext(),"\n" +
                                "Email atau kata sandi salah dimasukkan",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        manager.checkResult(requestCode,permissions,grantResults);


        ArrayList<String> denied_permissions = manager.getStatus().get(0).denied;

        if(denied_permissions.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"\n" + "Izin lokasi diberikan.",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Izin lokasi tidak diberikan. Mohon berikan izin.",Toast.LENGTH_SHORT).show();
        }
    }







    public void onBackPressed(){
        Intent intent = new Intent(MasukActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
