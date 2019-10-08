package com.thoms.silonaanak;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Bantuan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bantuan);
    }

    public void goToHome (View v){
        Intent myIntent = new Intent (Bantuan.this, HomeActivity.class);
        startActivity(myIntent);
        finish();
        return;
    }

    public void goToTutorialPendaftaran (View v){
        Intent myIntent = new Intent (Bantuan.this, TutorialDaftar.class);
        startActivity(myIntent);
        finish();
        return;
    }

    public void onBackPressed(){
        Intent intent = new Intent(Bantuan.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
