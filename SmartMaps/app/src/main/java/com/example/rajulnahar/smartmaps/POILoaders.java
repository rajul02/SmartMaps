package com.example.rajulnahar.smartmaps;

import android.content.ContentValues;
import android.content.Context;
import android.location.*;
import android.location.Location;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rajul Nahar on 27-01-2017.
 */

public class POILoaders {
    public List<com.example.rajulnahar.smartmaps.Location> locationList;
    public boolean taskComplete = false;

    public POILoaders(Context context){
        requestQueue = Volley.newRequestQueue(context);
        locationList = new ArrayList<>();
    }

    StringRequest stringRequest;
    RequestQueue requestQueue;

    public void getPoi(com.example.rajulnahar.smartmaps.Location location,int radius,String type){
        taskComplete = false;
        stringRequest = new StringRequest(buildURL(location,radius,type), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0;i<jsonArray.length();i++){
                        JSONObject jsonObject1 = (JSONObject) ((JSONObject)jsonArray.get(i)).get("geometry");
                        locationList.add(new Gson().fromJson(jsonObject1.getString("location"), com.example.rajulnahar.smartmaps.Location.class));


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                taskComplete = true;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                taskComplete = true;

            }
        });
        requestQueue.add(stringRequest);

    }

    public List<com.example.rajulnahar.smartmaps.Location> getLocationList() {
        return locationList;
    }

    public String buildURL(com.example.rajulnahar.smartmaps.Location location,int radius,String type){
        return Constants.baseurl+
                Constants.locationurl+ location.getLat()+ "," +location.getLng()+
                Constants.radius + String.valueOf(radius) +
                Constants.types +type+
                Constants.key;
    }

    public boolean isTaskComplete() {
        return taskComplete;
    }
}
