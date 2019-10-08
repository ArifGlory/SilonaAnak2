package com.thoms.silonaanak;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.thoms.silonaanak.Common.Common;
import com.thoms.silonaanak.Model.AnakDaftar;
import com.thoms.silonaanak.Remote.IGoogleAPI;
import com.thoms.silonaanak.Service.LocationShareService;

import java.util.List;

public class MapsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener

{

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference orangtuaReference,anakReference;
    ImageView img_profile;

    GoogleMap mMap;
    Marker currentMarker;
    LatLng latLngStart;
    FirebaseStorage firebaseStorage;

    StorageReference storageReference;

    TextView textNama,textEmail,textUser;

    GoogleApiClient client;
    LocationRequest request;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        FirebaseApp.initializeApp(MapsActivity.this);
        ref = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        anakReference = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak").child(auth.getCurrentUser().getUid());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Orangtua_images").child(auth.getCurrentUser().getUid());


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        textNama = (TextView) header.findViewById(R.id.namaTxt);
        textEmail = (TextView) header.findViewById(R.id.emailTxt);
        img_profile = (ImageView) header.findViewById(R.id.img_profile);

        anakReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AnakDaftar anakDaftar = dataSnapshot.getValue(AnakDaftar.class);
                String nama = anakDaftar.getNama();
                String User = anakDaftar.getUsername();


                textNama.setText(anakDaftar.getNama());
                textEmail.setText(anakDaftar.getEmail());

               // Toast.makeText(getApplicationContext(),"url:"+anakDaftar.getImageUrl(),Toast.LENGTH_SHORT).show();
                Log.d("urlImage:",anakDaftar.getImageUrl());
                if (anakDaftar.getImageUrl() != null && anakDaftar.getImageUrl().toString().length() > 10){
                    Glide.with(getApplicationContext())
                            .load(anakDaftar.getImageUrl())
                            .into(img_profile);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.maps, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.b_lokasi)
        {

            if(isServiceRunning(getApplicationContext(),LocationShareService.class))
            {
                Toast.makeText(getApplicationContext(),"Kamu Sudah Membagikan Lokasi Kamu Sendiri.",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Intent myIntent = new Intent(MapsActivity.this,LocationShareService.class);
                startService(myIntent);
            }


        } else if (id == R.id.bb_lokasi) {
            Intent myIntent2 = new Intent(MapsActivity.this,LocationShareService.class);
            stopService(myIntent2);
            anakReference.child("isSharing").setValue("false")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {

                                Toast.makeText(getApplicationContext(),"Berbagi Lokasi Kamu Sekarang Dihentikan",Toast.LENGTH_SHORT).show();


                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Berbagi Lokasi Kamu Sekarang Tidak Dapat Dihentikan",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
        else if (id == R.id.bantuan) {
            Intent myIntent = new Intent(MapsActivity.this, BantuanDalam.class);
            startActivity(myIntent);
            finish();
        }

        else if(id == R.id.exit)
        {
            auth.signOut();
            finish();
            Intent intent = new Intent(MapsActivity.this,LocationShareService.class);
            Intent i = new Intent(MapsActivity.this,HomeActivity.class);
            stopService(intent);
            startActivity(i);
            anakReference.child(user.getUid()).child("isSharing").setValue("false")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {

                                Toast.makeText(getApplicationContext(),"Anda Sudah Keluar",Toast.LENGTH_SHORT).show();


                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Berbagi Lokasi Kamu Sekarang Tidak Dapat Dihentikan",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


        }
        else if(id == R.id.change_avatar){
            Intent intent = new Intent(getApplicationContext(),ChangeAvatarActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    public boolean isServiceRunning(Context c, Class<?> serviceClass)
    {
        ActivityManager activityManager = (ActivityManager)c.getSystemService(Context.ACTIVITY_SERVICE);


        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);



        for(ActivityManager.RunningServiceInfo runningServiceInfo : services)
        {
            if(runningServiceInfo.service.getClassName().equals(serviceClass.getName()))
            {
                return true;
            }
        }

        return false;


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        client.connect();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(4000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Toast.makeText(getApplicationContext(), "Lokasi Kamu Tidak Dapat Ditemukan", Toast.LENGTH_LONG).show();
        } else {

            latLngStart = new LatLng(location.getLatitude(), location.getLongitude());

            MarkerOptions options = new MarkerOptions();
            options.position(latLngStart);
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
            options.title("Lokasi Kamu");

            if(currentMarker== null)
            {
                currentMarker = mMap.addMarker(options);
            }

            else
            {
                currentMarker.setPosition(latLngStart);
            }


            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLngStart, 15);
            mMap.moveCamera(update);

        }
    }
}
