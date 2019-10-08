package com.thoms.silonaanak;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class BantuanDalam extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bantuan_dalam);
    }

    public void goToHome (View v){
        Intent myIntent = new Intent (BantuanDalam.this, MapsActivity.class);
        startActivity(myIntent);
        finish();
        return;
    }


    public void onBackPressed(){
        Intent intent = new Intent(BantuanDalam.this, MapsActivity.class);
        startActivity(intent);
        finish();
    }
}
