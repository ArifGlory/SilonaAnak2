package com.thoms.silonaanak;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TutorialDaftar extends AppCompatActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_daftar);


        viewPager = (ViewPager)findViewById(R.id.viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

    }


    public void goToBeranda (View v){
        Intent myIntent = new Intent (TutorialDaftar.this, Bantuan .class);
        startActivity(myIntent);
        finish();
        return;
    }


    public void onBackPressed(){
        Intent intent = new Intent(TutorialDaftar.this, Bantuan.class);
        startActivity(intent);
        finish();
    }
}
