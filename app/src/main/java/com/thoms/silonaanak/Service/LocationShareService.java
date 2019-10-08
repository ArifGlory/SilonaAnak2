package com.thoms.silonaanak.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.thoms.silonaanak.MapsActivity;
import com.thoms.silonaanak.Model.LatLonKirim;
import com.thoms.silonaanak.R;


import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class LocationShareService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{
    public LocationShareService() {
    }

    GoogleApiClient client;
    LocationRequest request;
    Timestamp time;
    LatLng latLngCurrent;
    DatabaseReference anakReference, rabu;
    FirebaseAuth auth;
    FirebaseUser user;
    //String[ ]Latitude  =  {"lattitude"};
   // String[ ]Longitude  =  {"longitude"};
  //  String[ ]Lasttime  =  {"lasttime"};
    String[]riwayat = {"lasttime","latitude","longitude"};




    NotificationCompat.Builder notification;
    public final int uniqueId = 654321;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.


        throw new UnsupportedOperationException("Not yet implemented");




    }

    @Override
    public void onCreate() {
        super.onCreate();
        anakReference = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak");
        auth = FirebaseAuth.getInstance();
        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(false);
        notification.setOngoing(true);

        user = auth.getCurrentUser();
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {


                request = new LocationRequest().create();
                request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                request.setInterval(10000);
                request.setFastestInterval(10000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);

        notification.setSmallIcon(R.drawable.gps);
        notification.setTicker("Pemberitahuan.");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("SiLona Anak");
        notification.setContentText("Kamu Membagikan Lokasi Kamu.!");
        notification.setDefaults(Notification.DEFAULT_SOUND);


        Intent intent = new Intent(getApplicationContext(),MapsActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        notification.setContentIntent(pendingIntent);

        // Build the nofification

        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        nm.notify(uniqueId,notification.build());




        // display notification
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latLngCurrent = new LatLng(location.getLatitude(), location.getLongitude());

        shareLocation();


    }





    public void shareLocation()
    {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        Date dates = calendar.getTime();
       // calendar.setTimeInMillis(calendar.getTimeInMillis() + (secs*10000));
        String day = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(dates.getTime());
        String hour = new SimpleDateFormat("HH", Locale.ENGLISH).format(dates.getTime());
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault());
        final String myDate         = sdf1.format(date);
        Log.d("jam:",hour);

        LatLonKirim latLonKirim  = new LatLonKirim(String.valueOf(latLngCurrent.latitude),String.valueOf(latLngCurrent.longitude));
        String tanggal           = new SimpleDateFormat("yyyyMMdd").format(new Date());

        if (day.equals("Monday")) {
            anakReference.child(user.getUid()).child("Senin").child("lasttime").setValue(myDate);
            anakReference.child(user.getUid()).child("Senin").child("latitude").setValue(String.valueOf(latLngCurrent.latitude));
            anakReference.child(user.getUid()).child("Senin").child("longitude").setValue(String.valueOf(latLngCurrent.longitude));

            String key =  anakReference.child(user.getUid()).child("Senin").child("listLatLon").child(tanggal).push().getKey();
            anakReference.child(user.getUid()).child("Senin").child("listLatLon").child(tanggal).child(key).setValue(latLonKirim);

            //set lokasi by time
            String latlon = ""+latLonKirim.latitude+","+latLonKirim.longitude;
            setLocationByTime("Senin",hour,latlon);
        }
       else if (day.equals("Tuesday")) {
            anakReference.child(user.getUid()).child("Selasa").child("lasttime").setValue(myDate);
            anakReference.child(user.getUid()).child("Selasa").child("latitude").setValue(String.valueOf(latLngCurrent.latitude));
            anakReference.child(user.getUid()).child("Selasa").child("longitude").setValue(String.valueOf(latLngCurrent.longitude));

            String key =  anakReference.child(user.getUid()).child("Selasa").child("listLatLon").child(tanggal).push().getKey();
            anakReference.child(user.getUid()).child("Selasa").child("listLatLon").child(tanggal).child(key).setValue(latLonKirim);

            //set lokasi by time
            String latlon = ""+latLonKirim.latitude+","+latLonKirim.longitude;
            setLocationByTime("Selasa",hour,latlon);

        }
        else if (day.equals("Wednesday")) {

            anakReference.child(user.getUid()).child("Rabu").child("lasttime").setValue(myDate);
            anakReference.child(user.getUid()).child("Rabu").child("latitude").setValue(String.valueOf(latLngCurrent.latitude));
            anakReference.child(user.getUid()).child("Rabu").child("longitude").setValue(String.valueOf(latLngCurrent.longitude));

            String key =  anakReference.child(user.getUid()).child("Rabu").child("listLatLon").child(tanggal).push().getKey();
            anakReference.child(user.getUid()).child("Rabu").child("listLatLon").child(tanggal).child(key).setValue(latLonKirim);

            //set lokasi by time
            String latlon = ""+latLonKirim.latitude+","+latLonKirim.longitude;
            setLocationByTime("Rabu",hour,latlon);
        }

        else if (day.equals("Thursday")) {

            anakReference.child(user.getUid()).child("Kamis").child("lasttime").setValue(myDate);
            anakReference.child(user.getUid()).child("Kamis").child("latitude").setValue(String.valueOf(latLngCurrent.latitude));
            anakReference.child(user.getUid()).child("Kamis").child("longitude").setValue(String.valueOf(latLngCurrent.longitude));

            String key =  anakReference.child(user.getUid()).child("Kamis").child("listLatLon").child(tanggal).push().getKey();
            anakReference.child(user.getUid()).child("Kamis").child("listLatLon").child(tanggal).child(key).setValue(latLonKirim);

            //set lokasi by time
            String latlon = ""+latLonKirim.latitude+","+latLonKirim.longitude;
            setLocationByTime("Kamis",hour,latlon);

        }
        else if (day.equals("Friday")) {
            anakReference.child(user.getUid()).child("Jumat").child("lasttime").setValue(myDate);
            anakReference.child(user.getUid()).child("Jumat").child("latitude").setValue(String.valueOf(latLngCurrent.latitude));
            anakReference.child(user.getUid()).child("Jumat").child("longitude").setValue(String.valueOf(latLngCurrent.longitude));

            String key =  anakReference.child(user.getUid()).child("Jumat").child("listLatLon").child(tanggal).push().getKey();
            anakReference.child(user.getUid()).child("Jumat").child("listLatLon").child(tanggal).child(key).setValue(latLonKirim);

            //set lokasi by time
            String latlon = ""+latLonKirim.latitude+","+latLonKirim.longitude;
            setLocationByTime("Jumat",hour,latlon);

        }
        else if (day.equals("Saturday")) {
            anakReference.child(user.getUid()).child("Sabtu").child("lasttime").setValue(myDate);
            anakReference.child(user.getUid()).child("Sabtu").child("latitude").setValue(String.valueOf(latLngCurrent.latitude));
            anakReference.child(user.getUid()).child("Sabtu").child("longitude").setValue(String.valueOf(latLngCurrent.longitude));

            String key =  anakReference.child(user.getUid()).child("Sabtu").child("listLatLon").child(tanggal).push().getKey();
            anakReference.child(user.getUid()).child("Sabtu").child("listLatLon").child(tanggal).child(key).setValue(latLonKirim);

            //set lokasi by time
            String latlon = ""+latLonKirim.latitude+","+latLonKirim.longitude;
            setLocationByTime("Sabtu",hour,latlon);

        }
        else if (day.equals("Sunday")) {
            anakReference.child(user.getUid()).child("Minggu").child("lasttime").setValue(myDate);
            anakReference.child(user.getUid()).child("Minggu").child("latitude").setValue(String.valueOf(latLngCurrent.latitude));
            anakReference.child(user.getUid()).child("Minggu").child("longitude").setValue(String.valueOf(latLngCurrent.longitude));

            String key =  anakReference.child(user.getUid()).child("Minggu").child("listLatLon").child(tanggal).push().getKey();
            anakReference.child(user.getUid()).child("Minggu").child("listLatLon").child(tanggal).child(key).setValue(latLonKirim);

            //set lokasi by time
            String latlon = ""+latLonKirim.latitude+","+latLonKirim.longitude;
            setLocationByTime("Minggu ",hour,latlon);

        }



        anakReference.child(user.getUid()).child("isSharing").setValue("true");
        anakReference.child(user.getUid()).child("lasttime").setValue(myDate);
        anakReference.child(user.getUid()).child("latitude").setValue(String.valueOf(latLngCurrent.latitude));
        anakReference.child(user.getUid()).child("longitude").setValue(String.valueOf(latLngCurrent.longitude))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(),"Tidak dapat membagikan Lokasi.",Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }

    public void setLocationByTime(String hari,String jam,String latlon){
        anakReference.child(user.getUid()).child(hari).child("listJam").child(jam).setValue(latlon);
    }


    @Override
    public void onDestroy() {
        LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        client.disconnect();
        anakReference.child(user.getUid()).child("isSharing").setValue("false");

        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(uniqueId);



    }
}
