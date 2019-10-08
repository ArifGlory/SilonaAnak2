package com.thoms.silonaanak;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {
    private ImageView silona,family;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        silona = (ImageView) findViewById(R.id.silona);
        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.mytransition);
        silona.startAnimation(myanim);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();



        final Intent i = new Intent(this,HomeActivity.class);
        Thread timer = new Thread(){
            public void run (){
                try {
                    sleep(5000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    /*startActivity(i);
                    finish();*/

                    if (firebaseUser == null){
                        finish();
                        startActivity(i);
                    }else {
                        finish();
                        Intent myIntent = new Intent(MainActivity.this,MapsActivity.class);
                        startActivity(myIntent);
                    }
                }
            }
        };
        timer.start();
    }


}