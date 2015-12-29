package com.sci2015fair.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import static com.sci2015fair.fileoperations.LocationGPSLogCSVWriter.writeNewEntry;

public class LocationGPSLogPersistentService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient = null;
    public LocationGPSLogPersistentService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {//everything starts here when service is started
        super.onCreate();
        Log.d("LocationLogSvc", "In onCreateCommand");
        buildGoogleApiClient();
//        mGoogleApiClient.connect();
//        startLocationUpdates(createLocationRequest());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("LocationLogSvc", "In onStartCommand");
        mGoogleApiClient.connect();
        return 0;
    }

    protected synchronized void buildGoogleApiClient() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)//http://stackoverflow.com/questions/23751905/error-implementing-googleapiclient-builder-for-android-development
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // TODO Auto-generated method stub
/*
        if (!mIntentInProgress && result.hasResolution()) {
            try {
                mIntentInProgress = true;
                result.startResolutionForResult(this, // your activity
                        RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent. Return to the
                // default
                // state and attempt to connect to get an updated
                // ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
*/
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // TODO Auto-generated method stub
        startLocationUpdates(createLocationRequest());
        Log.d("Connected", "CONNECTED onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    //    @Override
    public void onDisconnected() {
        // TODO Auto-generated method stub

    }


    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000);
        mLocationRequest.setFastestInterval(60000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    protected void startLocationUpdates(LocationRequest mLocationRequest) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location currentLocation) {
        writeNewEntry(currentLocation, null);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onDestroy(){
        mGoogleApiClient.disconnect();
        stopLocationUpdates();
        Log.d("LocationGPSLogPServ", "Location tracking service shutting down.");
        super.onDestroy();
    }
}
