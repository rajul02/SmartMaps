package com.example.rajulnahar.smartmaps;

import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rajul Nahar on 24-01-2017.
 */

public class Constants {
    public static Location location;
    public static String ModeDriving;
    public static Marker markerPoiSelect;
    public static GoogleMap mMap;
    public static String poiurl = "";
    public static String baseurl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    public static String key = "&key=AIzaSyAQkfSzhnv1dpqETZ7RfpWWKd_JQqFJw3s";
    public static String locationurl = "location=";
    public static String radius = "&radius=";
    public static String types = "&types=";
    public static List<String> selectedCategories = new ArrayList<>();
    public static String sharedPrefrencename = "SmartMapsSharedPrefrences";
    public static String categorykey = "category";
    public static String distanceUnitkey = "distanceunit";
    public static String distancekey = "distance";


}
