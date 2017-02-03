package com.example.rajulnahar.smartmaps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class ListedPlaceActivity extends AppCompatActivity {
    SmartMapsdb smartMapsdb;
    List<LatLng> listedplacelatlong;
    List<String> walkingtime;
    List<String> drivingtime;
    ListedPlacesAdapter listedPlacesAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listed_place);
        smartMapsdb = SmartMapsdb.getInstance(this);
        listView = (ListView) findViewById(R.id.listedplacelist);
        listedPlacesAdapter = new ListedPlacesAdapter(ListedPlaceActivity.this);
        listView.setAdapter(listedPlacesAdapter);
        listedplacelatlong = new ArrayList<>();
        drivingtime = new ArrayList<>();
        walkingtime = new ArrayList<>();
        final List<ListedPlace> listedplace = smartMapsdb.getListedlace();
        for (int i =0;i<listedplace.size();i++){
            listedplacelatlong.add(new LatLng(Double.parseDouble(listedplace.get(i).latitude),Double.parseDouble(listedplace.get(i).longitude)));
        }
        for(int i = 0;i<listedplacelatlong.size();i++){
            GoogleDirection.withServerKey(getResources().getString(R.string.google_maps_key))
                    .from(new LatLng(Constants.location.getLatitude(),Constants.location.getLongitude()))
                    .to(listedplacelatlong.get(i))
                    .transportMode(TransportMode.DRIVING)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            //Log.e("listedplace",rawBody);
                            if (direction.isOK()){
                                Route route = direction.getRouteList().get(0);
                                String txt = route.getLegList().get(0).getDuration().getText();
                                drivingtime.add(txt);
                                listedPlacesAdapter.setDrivingtimeList(drivingtime);
                                listedPlacesAdapter.setLatLngList(listedplacelatlong);
                                listedPlacesAdapter.setWalkingtimeList(walkingtime);
                                listedPlacesAdapter.notifyDataSetChanged();
                                Log.e("listedplaceactivity",txt);
                            }
                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {

                        }
                    });

            GoogleDirection.withServerKey(getResources().getString(R.string.google_maps_key))
                    .from(new LatLng(Constants.location.getLatitude(),Constants.location.getLongitude()))
                    .to(listedplacelatlong.get(i))
                    .transportMode(TransportMode.WALKING)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            if (direction.isOK()){
                                walkingtime.add(direction.getRouteList().get(0).getLegList().get(0).getDuration().getText());
                                Log.e("listedplaceactivity",direction.getRouteList().get(0).getLegList().get(0).getDuration().getText());
                                listedPlacesAdapter.setDrivingtimeList(drivingtime);
                                listedPlacesAdapter.setLatLngList(listedplacelatlong);
                                listedPlacesAdapter.setWalkingtimeList(walkingtime);
                                listedPlacesAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {

                        }
                    });
                    }




    }
}
