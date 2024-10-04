package com.example.signuploginrealtime.ui.map;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.signuploginrealtime.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Activity activity = getActivity();
        if (activity == null) return;

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 如果權限沒有被授予，則請求權限
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            mMap.setMyLocationEnabled(true); // 權限已被授予，可以執行需要該權限的操作
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                        }
                    }
                });

        /*LatLng sport = new LatLng(25.03261392198456, 121.5358892838469);
        googleMap.addMarker(new MarkerOptions()
                .position(sport)
                .title("大安森林公園溜冰場")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.rollerblade)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sport));*/

        AssetManager assetManager = getContext().getAssets();
        InputStream is = null;
        InputStreamReader isr = null;
        CSVReader reader = null;

        List<SportField> fields = null;
        try {
            is = assetManager.open("sport_location.csv");
            isr = new InputStreamReader(is);
            reader = new CSVReader(isr);
            String[] nextLine;
            fields = new ArrayList<>();
            reader.readNext(); // 跳過標題行
            while ((nextLine = reader.readNext()) != null) {
                String name = nextLine[2];
                double lat = Double.parseDouble(nextLine[9]);
                double lng = Double.parseDouble(nextLine[8]);
                fields.add(new SportField(name, lat, lng));
            }
        } catch (IOException e) {
            e.printStackTrace();

        } catch (CsvValidationException e) {
            e.printStackTrace();

        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d("DEBUG", "Number of fields: " + fields.size());
        for (SportField field : fields) {
            LatLng position = new LatLng(field.lat, field.lng);
            mMap.addMarker(new MarkerOptions().position(position).title(field.name));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
            Log.d("DEBUG", "Success");
        }
    }
}




