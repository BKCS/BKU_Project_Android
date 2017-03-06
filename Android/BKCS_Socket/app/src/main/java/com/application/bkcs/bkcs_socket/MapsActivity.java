package com.application.bkcs.bkcs_socket;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    LatLng location_vehicle;
    List<Marker> markerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(MainActivity.mlatitude, MainActivity.mlongitude);

        //hien thi vi tri nguoi dung
        mMap.addMarker(new MarkerOptions().position(sydney).title(getString(R.string.Current_Location))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14));
        mMap.setMaxZoomPreference(27);

        try {
            get_vehicle();
        }catch(Exception e)
        {

        }

        mMap.addPolyline(MainActivity.poly);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

    }


    public void get_vehicle(){

        location_vehicle = new LatLng(TransportActivity.lat, TransportActivity.lng);

        if(TransportActivity.type_af.equals("car")) {
            MarkerOptions markerOption = new MarkerOptions()
                    .position(location_vehicle)
                    .title(TransportActivity.time_af)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.vehicle_car));
            Marker marker = mMap.addMarker(markerOption);
            markerList.add(marker);
        }
        if(TransportActivity.type_af.equals("bus"))
        {
            MarkerOptions markerOption = new MarkerOptions()
                    .position(location_vehicle)
                    .title(TransportActivity.time_af)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.vehicle_bus));
            Marker marker = mMap.addMarker(markerOption);
            markerList.add(marker);
        }
        if(TransportActivity.type_af.equals("motorbike")) {
            MarkerOptions markerOption = new MarkerOptions()
                    .position(location_vehicle)
                    .title(TransportActivity.time_af)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.vehicle_scooter));
            Marker marker = mMap.addMarker(markerOption);
            markerList.add(marker);
        }
        if(TransportActivity.type_af.equals("bicycle")) {
            MarkerOptions markerOption = new MarkerOptions()
                    .position(location_vehicle)
                    .title(TransportActivity.time_af)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.vehicle_bicycle));
            Marker marker = mMap.addMarker(markerOption);
            markerList.add(marker);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.action_refresh:
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
