package com.example.rajulnahar.smartmaps;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class ShareActivity extends AppCompatActivity implements OnMapReadyCallback {

    ListView listViewleft;
    ListView listViewright;
    Button btnShare;

    EditText comments;

    Button selectAll;
    Button clearAll;

    ListviewAdapter listviewAdapterleft;
    ListviewAdapter listviewAdapterright;
    EditText etComments;
    public List<Categories> categories;
    public List<Categories> categoriesleft;
    public List<Categories> categoriesright;
    SmartMapsdb smartMapsdb;

    SupportMapFragment supportMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        smartMapsdb = SmartMapsdb.getInstance(this);
        btnShare = (Button) findViewById(R.id.btn_share);
        listViewleft = (ListView) findViewById(R.id.lv_left);
        listViewright = (ListView) findViewById(R.id.lv_right);
        comments = (EditText) findViewById(R.id.etComment);
        selectAll = (Button) findViewById(R.id.selectall);
        clearAll = (Button) findViewById(R.id.clearall);
        etComments = (EditText) findViewById(R.id.etComment);
        listviewAdapterleft = new ListviewAdapter(this);
        listviewAdapterright = new ListviewAdapter(this);
        categories = smartMapsdb.getAllCategories();
        categoriesleft = categories.subList(0,categories.size()/2);
        categoriesright = categories.subList(categories.size()/2,categories.size());
        listviewAdapterleft.setCategories(categoriesleft);
        listviewAdapterright.setCategories(categoriesright);
        listViewleft.setAdapter(listviewAdapterleft);
        listViewright.setAdapter(listviewAdapterright);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShare();
            }
        });

        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("rakil","Select All Clicked");
                setAllCheckBox(true);
            }
        });

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllCheckBox(false);
                Constants.selectedCategories.clear();
            }
        });

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

    }


    public void setAllCheckBox(boolean state){
        for(int i = 0; i < listviewAdapterleft.getCount(); i++){
            View view = ((LinearLayout)listViewleft.getChildAt(i));
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            checkBox.setChecked(state);
            if(state){
                if(!Constants.selectedCategories.contains(listviewAdapterleft.categories.get(i).category)){
                    Constants.selectedCategories.add(listviewAdapterleft.categories.get(i).category);
                }
            }
                
        }
        for(int i = 0; i < listviewAdapterright.getCount(); i++){
            View view = ((LinearLayout)listViewright.getChildAt(i));
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            checkBox.setChecked(state);
            if(state){
                if(!Constants.selectedCategories.contains(listviewAdapterright.categories.get(i).category)){
                    Constants.selectedCategories.add(listviewAdapterright.categories.get(i).category);
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.addMarker(new MarkerOptions().position(Constants.markerPoiSelect.getPosition()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constants.markerPoiSelect.getPosition(),15.0f));
    }

    public  void onShare(){
        //geterate string using constants.poiselected
        String shareString = "https://www.google.co.in/maps/@"+Constants.location.getLatitude()+","+Constants.location.getLongitude()+",15z\n";
        String bla = "\nCategories: ";

        for(int i = 0; i < Constants.selectedCategories.size(); i++){
            bla += Constants.selectedCategories.get(i);
            if(i != Constants.selectedCategories.size()-1);
                bla += ",";
        }
        String comment = etComments.getText().toString();
        Log.e("RajulDebugger","Categories: " + bla);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT,shareString+"\n"+comment+"\n"+bla);
        startActivity(Intent.createChooser(sharingIntent,"Select to share"));
    }
}
