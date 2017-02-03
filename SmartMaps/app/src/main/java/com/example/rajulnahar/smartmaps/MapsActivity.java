package com.example.rajulnahar.smartmaps;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.DialogPreference;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.constant.TransportMode;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback ,
        GoogleApiClient.OnConnectionFailedListener ,
        GpsStatus.Listener,
        GoogleMap.OnMarkerClickListener{

    private GoogleMap mMap;
    public LocationManager locationManager;
    public LocationListener locationListener;
    private GoogleApiClient mGoogleApiClient;
    Location location;
    SmartMapsdb smartMapsdb;
    double lat = 0;
    double lon = 0;
    ImageView settings;
    LinearLayout advancesearch;
    LinearLayout likeus;
    LinearLayout rateus;
    LinearLayout shareit;
    LinearLayout listedplace;
    LinearLayout addnew;
    Dialog distancedialog;
    Dialog advsearch;
    Dialog poiPopup;

    double latitudePOISelected=0;
    double longitudePOISelected=0;
    Marker markerSelectedPoi;

    int distanceVal = 1000;
    boolean iskm = true;

    RadioButton inkm;
    RadioButton inmiles;

    Button avdSearchButton;

    LinearLayout ll_favourite;
    LinearLayout ll_share;
    TextView tvDrivingDirection,tvWalkingDirection;
    SeekBar seekbar;

    public long lastgps = 0;
    public static boolean gpsfixed = false;

    public List<com.example.rajulnahar.smartmaps.Location> locationList;
    public POILoaders poiLoaders;

    com.example.rajulnahar.smartmaps.Location loc;

    public ListView listView;
    public ListviewAdapter listviewAdapter;

    ConnectivityManager connectivityManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.addGpsStatusListener(this);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            alertToSwitchGPS();
        }
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
       // if(connectivityManager!= null)
        //if(!connectivityManager.getActiveNetworkInfo().isConnected()){
        //    alertToSwitchData();
        //}
        settings = (ImageView) findViewById(R.id.settings);
        advancesearch = (LinearLayout) findViewById(R.id.advancesearch);
        likeus = (LinearLayout) findViewById(R.id.likeus);
        rateus = (LinearLayout) findViewById(R.id.rateus);
        shareit = (LinearLayout) findViewById(R.id.shareit);
        listedplace = (LinearLayout) findViewById(R.id.listedplace);
        addnew = (LinearLayout) findViewById(R.id.addnew);
        seekbar = (SeekBar) findViewById(R.id.seekBar);

        poiLoaders = new POILoaders(MapsActivity.this);


        //test purpose
        smartMapsdb = SmartMapsdb.getInstance(MapsActivity.this);
        smartMapsdb.deleteTables();
        smartMapsdb.testCategory();
       // smartMapsdb.getAllCategories();

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                while (!gpsfixed){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                locationLocked();
            }
        }.execute();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this,this)
                .build();

        distancedialog = new Dialog(MapsActivity.this);
        View view = LayoutInflater.from(MapsActivity.this).inflate(R.layout.distancedialog,null);
        distancedialog.setContentView(view);
        distancedialog.setTitle("Select distance in");
        inkm = (RadioButton) view.findViewById(R.id.distancekm);
        inmiles = (RadioButton) view.findViewById(R.id.distancemile);

        advsearch = new Dialog(MapsActivity.this);
        view = LayoutInflater.from(MapsActivity.this).inflate(R.layout.searchdailog,null);
        listView = (ListView) view.findViewById(R.id.categorylist);
        listviewAdapter = new ListviewAdapter(this);
        listviewAdapter.setCategories(smartMapsdb.getAllCategories());
        listView.setAdapter(listviewAdapter);
        advsearch.setContentView(view);
        advsearch.setTitle("Select Categories");
        avdSearchButton = (Button) view.findViewById(R.id.btn_search);

        poiPopup = new Dialog(MapsActivity.this);
        view = LayoutInflater.from(MapsActivity.this).inflate(R.layout.popupdialog,null);
        poiPopup.setContentView(view);
        poiPopup.setTitle("POI Popup");
        ll_favourite = (LinearLayout) view.findViewById(R.id.ll_favourite);
        ll_share = (LinearLayout)view.findViewById(R.id.ll_share);
        tvDrivingDirection = (TextView) view.findViewById(R.id.drivingdirection);
        tvWalkingDirection = (TextView) view.findViewById(R.id.walkingdirctions);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               //progess variable mai value hai kitne km ya miles honge

                if(iskm)
                distanceVal = progress*1000;
                else
                    distanceVal = progress*621;

                saveToSharedPrefrences(Constants.distancekey,String.valueOf(distanceVal));
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(new LatLng(Constants.location.getLatitude(), Constants.location.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                locationList.clear();

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {

                        Log.e("list",getCategories());
                        poiLoaders.getPoi(loc, distanceVal, getCategories());
                        while (!poiLoaders.isTaskComplete()) {

                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        poiLoaded();
                    }
                }.execute();


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                distancedialog.show();
            }
        });

        advancesearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, "Advance Search", Toast.LENGTH_SHORT).show();
                Constants.selectedCategories.clear();
                advsearch.show();


            }
        });

        likeus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, "Like us", Toast.LENGTH_SHORT).show();
                Uri uri = Uri.parse("https://www.facebook.com/Future-Smart-Technologies-Pvt-Ltd-993516597384645/");
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });

        rateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, "Rate us", Toast.LENGTH_SHORT).show();
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.bigduckgames.flowbridges&hl=en");
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
        shareit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, "Share", Toast.LENGTH_SHORT).show();
                String bla = "https://play.google.com/store/apps/details?id=com.bigduckgames.flowbridges&hl=en";
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT,bla);
                startActivity(Intent.createChooser(sharingIntent,"Select to share"));
            }
        });

        listedplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, "List", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MapsActivity.this,ListedPlaceActivity.class));
            }
        });

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, "Add new", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MapsActivity.this,AddNewActivity.class);
                startActivity(intent);
            }
        });

        inkm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inmiles.setChecked(false);
                iskm = true;
            }
        });

        inmiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inkm.setChecked(false);
                //distance miles mai count hoga
                iskm = false;
            }
        });

        avdSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                advsearch.dismiss();
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(new LatLng(Constants.location.getLatitude(), Constants.location.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                locationList.clear();

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {

                        Log.e("list",getCategories());
                        poiLoaders.getPoi(loc, distanceVal, getCategories());
                        while (!poiLoaders.isTaskComplete()) {

                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        poiLoaded();
                    }
                }.execute();

            }
        });
        ll_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Favourites favourites = new Favourites();
                favourites.latitude = String.valueOf(latitudePOISelected);
                favourites.longitude = String.valueOf(longitudePOISelected);
                long arc =  smartMapsdb.addFavourites(favourites);
                Log.e("add to fav",String.valueOf(arc));
                markerSelectedPoi.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                poiPopup.dismiss();
            }
        });
        ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.selectedCategories.clear();
                Intent intent = new Intent(MapsActivity.this,ShareActivity.class);
                startActivity(intent);
            }
        });
        tvDrivingDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.ModeDriving = TransportMode.DRIVING;
                startActivity(new Intent(MapsActivity.this,DrivingDirectionsActivity.class));
            }
        });

        tvWalkingDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.ModeDriving = TransportMode.WALKING;
                startActivity(new Intent(MapsActivity.this,DrivingDirectionsActivity.class));
            }
        });
        mapFragment.getMapAsync(this);





    }

    public void alertToSwitchGPS(){
        AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivity.this);
        alert.setCancelable(false)
                .setMessage("Please Enable your GPS")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertToSwitchGPS();
                    }
                });
        alert.show();
    }
    public void alertToSwitchData(){
        AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivity.this);
        alert.setCancelable(false)
                .setMessage("Please enable data")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = (new Intent(Intent.ACTION_MAIN));
                        intent.setClassName("com.android.phone","com.android.phone.NetworkSetting");
                        startActivity(intent);

                    }
                })
                .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertToSwitchData();
                    }
                });
        alert.show();
    }

    public void poiLoaded(){
        locationList = poiLoaders.getLocationList();
        Log.e("locationlistsize",String.valueOf(locationList.size()));
        for (int i=0;i<locationList.size();i++){
           int x = smartMapsdb.getFavouriteByLatLong(Double.parseDouble(locationList.get(i).getLat()),
                   Double.parseDouble(locationList.get(i).getLng()));
            if(x!= -1){
            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(locationList.get(i).getLat()),Double.parseDouble(locationList.get(i).getLng())))
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));}
            else{
                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(locationList.get(i).getLat()),Double.parseDouble(locationList.get(i).getLng()))));

            }
        }

    }
    private void enableMyLocation() {

            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            //mMap.setOnMarkerDragListener(this);
            //mMap.addMarker(mMap.setMyLocationEnabled(true));

    }

    public  void locationLocked(){

        final Location location = mMap.getMyLocation();
        if(location!=null) {
            loc = new com.example.rajulnahar.smartmaps.Location();
            loc.setLat(String.valueOf(location.getLatitude()));
            loc.setLng(String.valueOf(location.getLongitude()));
            Constants.location = location;
            Log.e("Recievedlocation", String.valueOf(location.getLongitude()));
            mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.0f));
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.sharedPrefrencename,MODE_PRIVATE);
            final String search = sharedPreferences.getString(Constants.categorykey,"temple|atm|food|bank|airport");
            final String distan = sharedPreferences.getString(Constants.distancekey,"1000");
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {

                    poiLoaders.getPoi(loc, Integer.parseInt(distan), search);
                    while (!poiLoaders.isTaskComplete()) {

                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    poiLoaded();
                }
            }.execute();
        }
    }

    public String getCategories(){
        String res = "";
        for (int i  = 0;i<Constants.selectedCategories.size();i++){
            res = res+Constants.selectedCategories.get(i).toLowerCase();
            if(i != Constants.selectedCategories.size()-1 ){
                res+= "|";
            }
        }
        saveToSharedPrefrences(Constants.categorykey,res);
        return res;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();
        mMap.setOnMarkerClickListener(MapsActivity.this);
        // Add a marker in Sydney and move the camera

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGpsStatusChanged(int event) {
        if(event ==1){
            Log.e("gpsstatuschangelistner","eventstarted");
        }else if(event==2){
            Log.e("gpsstatuschangelistner","eventstopped");
        }else if(event==3){

            Log.e("gpsstatuschangelistner","eventfirstfi");
        }else{
            Log.e("gpsstatuschangelistner","satellitestatus");
            long systime = System.currentTimeMillis();
            long d = systime-lastgps;
            if(d<5000){
                gpsfixed = true;
                lastgps = systime;
            }
            else if(d>10000){
                gpsfixed = false;
                lastgps = systime;
            }
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

       // Toast.makeText(this, "marker clicked", Toast.LENGTH_SHORT).show();
        latitudePOISelected = marker.getPosition().latitude;
        longitudePOISelected = marker.getPosition().longitude;
        markerSelectedPoi = marker;
        Constants.markerPoiSelect = marker;
        poiPopup.show();
        return false;
    }

    public  void saveToSharedPrefrences(String key, String msg){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.sharedPrefrencename,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,msg);
        editor.commit();
    }
}
