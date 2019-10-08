package com.thoms.silonaanak;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity {



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                            .setDefaultFontPath("font/hangyaboly.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build());
        setContentView(R.layout.activity_home);
    }

    public void goToMasuk(View view){
        Intent myIntent = new Intent(HomeActivity.this,MasukActivity.class);
        startActivity(myIntent);
        finish();

    }
    public void goToTentang (View v){
        Intent myIntent = new Intent (HomeActivity.this, Tentang.class);
        startActivity(myIntent);
        finish();

    }

    public void goToBantuan (View v){
        Intent myIntent = new Intent (HomeActivity.this, Bantuan.class);
        startActivity(myIntent);
        finish();

    }
}
