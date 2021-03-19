package com.example.service_assignment.Service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.service_assignment.Activities.MainActivity;
import com.example.service_assignment.AppConstants;
import com.example.service_assignment.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

public class My_Service extends Service {

    NotificationCompat.Builder builder;
    NotificationManager notificationManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // get the location
        getLocation();

        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // get the location and show it in notification
    private void getLocation() {

        String channel = "my_channel"; // unique channel for notification
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // create notification builder
        builder = new NotificationCompat.Builder(getApplicationContext(), channel);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setPriority(NotificationCompat.PRIORITY_MAX).build();

        // create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            if (notificationManager != null && notificationManager.getNotificationChannel(channel) == null)
            {
                NotificationChannel notificationChannel = new NotificationChannel(channel,
                        "Location",
                        NotificationManager.IMPORTANCE_HIGH);

                notificationChannel.setDescription("This is using");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        startForeground(AppConstants.LOCATION_SERVICE_ID, builder.build());
    }

    /**
     * Used for receiving notifications from the FusedLocationProviderApi
     * when the device location has changed or can no longer be determined
     */
    private LocationCallback locationCallback = new LocationCallback(){

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (locationResult != null && locationResult.getLastLocation() != null)
            {
                // update the title of notification
                builder.setContentTitle("Latitude : " + locationResult.getLastLocation().getLatitude());
                // update the content of notification
                builder.setContentText(  "Longitude : " + locationResult.getLastLocation().getLongitude()
                        + "\nMoving speed : " + locationResult.getLastLocation().getSpeed());

                notificationManager.notify(AppConstants.LOCATION_SERVICE_ID, builder.build()); // notify
            }
        }
    };

}