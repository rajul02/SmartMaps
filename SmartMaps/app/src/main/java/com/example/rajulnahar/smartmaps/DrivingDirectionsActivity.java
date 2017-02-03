package com.example.rajulnahar.smartmaps;

import android.graphics.Color;
import android.location.*;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class DrivingDirectionsActivity extends FragmentActivity implements OnMapReadyCallback {

    SupportMapFragment supportMapFragment;
    GoogleMap googleMap;
    android.location.Location startLocation = Constants.location;
    Marker detinationLocation = Constants.markerPoiSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving_directions);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        GoogleDirection.withServerKey(getResources().getString(R.string.google_maps_key))
        .from(new LatLng(startLocation.getLatitude(),startLocation.getLongitude()))
        .to(new LatLng(detinationLocation.getPosition().latitude,detinationLocation.getPosition().longitude))
        .transportMode(Constants.ModeDriving)
        .execute(new DirectionCallback() {
            @Override
            public void onDirectionSuccess(Direction direction, String rawBody) {
                if(direction.isOK()){
                    getDirectionPoints(direction);
                }
            }

            @Override
            public void onDirectionFailure(Throwable t) {

            }
        });
    }

    public void getDirectionPoints(Direction direction){
        Route route = direction.getRouteList().get(0);
        Leg leg = route.getLegList().get(0);
        ArrayList<LatLng> latLngArrayList = leg.getDirectionPoint();
        String disanceString = leg.getDistance().getText();
        String timetring = leg.getDuration().getText();
        updateMap(latLngArrayList);
        Toast.makeText(this, "Total Distance: " + disanceString + " Total Time: " + timetring, Toast.LENGTH_SHORT).show();
    }

    public void updateMap(ArrayList<LatLng> directionPositionList){
        //directionPositionList = leg.getDirectionPoint();
        PolylineOptions polylineOptions = DirectionConverter.createPolyline(this, directionPositionList, 5, Color.RED);
        googleMap.addPolyline(polylineOptions);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(startLocation.getLatitude(),startLocation.getLongitude())));
        googleMap.addMarker(new MarkerOptions().position(detinationLocation.getPosition()));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(startLocation.getLatitude(),startLocation.getLongitude()),13.0f));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap =  googleMap;


    }
}
