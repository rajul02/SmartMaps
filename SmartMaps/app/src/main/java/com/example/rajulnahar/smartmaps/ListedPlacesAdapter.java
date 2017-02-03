package com.example.rajulnahar.smartmaps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Rajul Nahar on 01-02-2017.
 */

public class ListedPlacesAdapter extends BaseAdapter {

    List<LatLng> latLngList;
    List<String> drivingtimeList;
    List<String> walkingtimeList;
    Context context;

    @Override
    public int getCount() {
        if(latLngList!=null)
            if(drivingtimeList!=null &&walkingtimeList!=null)
        return Math.min(drivingtimeList.size(),walkingtimeList.size());
        return 0;
    }
    public ListedPlacesAdapter(Context context)
    {
        this.context = context;
    }
    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardlistedplace,null);
        Holder holder = new Holder();
        holder.drivingtime = (TextView) view.findViewById(R.id.drivingtime);
        holder.walkingtime = (TextView) view.findViewById(R.id.walkingtime);
        holder.positiontitle = (TextView) view.findViewById(R.id.positonname);
        holder.drivingtime.setText("Driving Time: "+ drivingtimeList.get(position));
        holder.walkingtime.setText("Walking Time: "+ walkingtimeList.get(position));
        holder.positiontitle.setText("LatLong: "+ String.valueOf(latLngList.get(position).longitude)+" , "+String.valueOf(latLngList.get(position).latitude));
        return view;
    }
    public class Holder{
        TextView drivingtime;
        TextView walkingtime;
        TextView positiontitle;

    }

    public void setDrivingtimeList(List<String> drivingtimeList) {
        this.drivingtimeList = drivingtimeList;
    }

    public void setLatLngList(List<LatLng> latLngList) {
        this.latLngList = latLngList;
    }

    public void setWalkingtimeList(List<String> walkingtimeList) {
        this.walkingtimeList = walkingtimeList;
    }
}
