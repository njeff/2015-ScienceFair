package com.sci2015fair.activity;


import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sci2015fair.R;
import com.sci2015fair.filecontrolcenter.SaveLocations;
import com.sci2015fair.programlogic.LocationIDObject;

import java.util.ArrayList;
import java.util.Arrays;

import static com.sci2015fair.fileoperations.LocationGPSLogCSVReader.readLocationCSVFile;

/**
 * A fragment that launches other parts of the demo application.
 */
public class MapsFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate and return the layout
        View v = inflater.inflate(R.layout.fragment_maps, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        // latitude and longitude
        double latitude = 17.385044;
        double longitude = 78.486671;

        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(latitude, longitude)).title("Hello Maps");

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

//        // adding marker
//        googleMap.addMarker(marker);
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(new LatLng(17.385044, 78.486671)).zoom(12).build();
//        googleMap.animateCamera(CameraUpdateFactory
//                .newCameraPosition(cameraPosition));

        // adding marker
//        googleMap.addMarker(new MarkerOptions()
//                .position(new LatLng(10, 10))
//                .title("Hello world"));
        ArrayList<LocationIDObject> allLocationData = new ArrayList<>(readLocationCSVFile(SaveLocations.DFLocationGPSLogCSV));
        for (int i = 0; i < allLocationData.size(); i++) {
            Log.d("ARRAYLIST", allLocationData.get(i).getLocation().getLatitude() + ", " + allLocationData.get(i).getLocation().getLongitude());
        }
        int j = 0;
        for (int i = 0; i < allLocationData.size(); i++) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(allLocationData.get(i).getLocation().getLatitude(), allLocationData.get(i).getLocation().getLongitude()))
                    .title(allLocationData.get(i).getLocation().getLatitude() + ", " + allLocationData.get(i).getLocation().getLongitude()));
            Log.d("coord", allLocationData.get(i).getLocation().getLatitude() + ", " + allLocationData.get(i).getLocation().getLongitude());
            j = i;
        }
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(allLocationData.get(j).getLocation().getLatitude(), allLocationData.get(j).getLocation().getLongitude())).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        // Perform any camera updates here
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}