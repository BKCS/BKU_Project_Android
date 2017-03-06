package com.application.hieu_nt.bkcs;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;

    FirebaseDatabase database;
    private User coor;
    Double new_lat, new_long;

    String time, time_car, time_point;
    String id;
    String temp = "0";

    private LocationManager locationManager;
    private String provider;
    private LatLng all_Location;
    private LatLng location_stop, location_vehicle;
    private LatLng location_disease, location_earthquake;
    List<Marker> markerList = new ArrayList<>();
    private String type;

    ArrayList<LatLng> coordinates = new ArrayList<>();

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sound);

        database = FirebaseDatabase.getInstance();
        id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = getLastKnownLocation();
        onLocationChanged(location);

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
        //milisecond, meter
        locationManager.requestLocationUpdates(provider, 5000, 50, this);

    }


    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {


            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12));
        mMap.setMaxZoomPreference(15);


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

    class ReadJSON extends AsyncTask<String, Integer, String>
    {

        @Override
        protected String doInBackground(String... params) {
            String chuoi = docNoiDung_Tu_URL(params[0]);
            return chuoi;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject root = new JSONObject(s);
                JSONArray mang = root.getJSONArray("snappedPoints");
                JSONObject son = mang.getJSONObject(0);
                JSONObject object = son.getJSONObject("location");
                new_lat = object.getDouble("latitude");
                new_long = object.getDouble("longitude");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static String docNoiDung_Tu_URL(String theUrl)
    {
        StringBuilder content = new StringBuilder();

        try
        {
            // create a url object
            URL url = new URL(theUrl);

            // create a urlconnection object
            URLConnection urlConnection = url.openConnection();

            // wrap the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;

            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null)
            {
                content.append(line + "\n");
            }
            bufferedReader.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return content.toString();
    }

    @Override
    public void onLocationChanged(Location location) {

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        time = sdf.format(location.getTime());

        SimpleDateFormat abc = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        time_point = abc.format(location.getTime());

        MainActivity.mlatitude = location.getLatitude();
        MainActivity.mlongitude = location.getLongitude();

       /* if(Integer.parseInt(time) >= 80000 && Integer.parseInt(time) <= 170000
                &&   calendar.get(Calendar.DAY_OF_WEEK) != 8) {*/

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new ReadJSON().execute("https://roads.googleapis.com/v1/snapToRoads?path=" + MainActivity.mlatitude + "," + MainActivity.mlongitude + "&key=AIzaSyBdzvbEIEVGIWALsHswM5b74fOGkk3deyI");
                }
            });


            coor = new User(new_lat, new_long);
            //time = String.valueOf(new Timestamp(location.getTime()));


            Integer new_time = Integer.parseInt(time);
            Integer new_temp = Integer.parseInt(temp);
            Integer sub = new_time - new_temp;
            if (sub > 200) {
              //  database.getReference("STOP").child(id).child(time_point).setValue(coor);
            }

            database.getReference("PATH").child(id).child(time_point).setValue(coor);

            temp = time;

        //}

            get_vehicle();

            drawLocations();

         //   get_report();

       /* get_disease();

        get_earthquake();*/

    }


    //hien thi phuong tien di chuyen
    public void car()
    {

        if(type.equals("car")) {
            MarkerOptions markerOption = new MarkerOptions()
                    .position(location_vehicle)
                    .title(time_car)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.vehicle_car));
            Marker marker = mMap.addMarker(markerOption);
            markerList.add(marker);
        }
        if(type.equals("bus"))
        {
            MarkerOptions markerOption = new MarkerOptions()
                    .position(location_vehicle)
                    .title(time_car)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.vehicle_bus));
            Marker marker = mMap.addMarker(markerOption);
            markerList.add(marker);
        }
        if(type.equals("motorbike")) {
            MarkerOptions markerOption = new MarkerOptions()
                    .position(location_vehicle)
                    .title(time_car)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.vehicle_scooter));
            Marker marker = mMap.addMarker(markerOption);
            markerList.add(marker);
        }
        if(type.equals("bicycle")) {
            MarkerOptions markerOption = new MarkerOptions()
                    .position(location_vehicle)
                    .title(time_car)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.vehicle_bicycle));
            Marker marker = mMap.addMarker(markerOption);
            markerList.add(marker);
        }
    }

    //lay phuong tien di chuyen tu database
    private void get_vehicle(){

        database.getReference("VEHICLE").child(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue() != null)
                {
                    Vehicle v = dataSnapshot.getValue(Vehicle.class);
                    location_vehicle = new LatLng(v.Latitude, v.Longitude);
                    type = v.Type;
                    time_car = v.Time;

                    car();
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //hien thi cham do thong bao khi dung qua lau tai mot vi tri
  /*  private void get_report()
    {
        database.getReference("STOP").child(id).addChildEventListener(new ChildEventListener() {
            LatLngBounds bounds;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User u = dataSnapshot.getValue(User.class);
                location_stop = new LatLng(u.Latitude, u.Longitude);

                builder.include(location_stop);
                bounds = builder.build();

                MarkerOptions mMarkerOption = new MarkerOptions()
                        .position(location_stop)
                        .title(getString(R.string.Stop))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.measle_red));
                Marker mMarker = mMap.addMarker(mMarkerOption);
                markerList.add(mMarker);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/


    //ve len duong di
    private void drawLocations() {

        //location
        database.getReference("PATH").child(id).addChildEventListener(new ChildEventListener() {
            LatLngBounds bounds;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                all_Location = new LatLng(user.Latitude, user.Longitude);


                // Make sure the map boundary contains the location
                builder.include(all_Location);
                bounds = builder.build();

                // Add polyline
                coordinates.add(all_Location);
                PolylineOptions poly = new PolylineOptions().addAll(coordinates)
                        .color(Color.BLUE).width(7);
                mMap.addPolyline(poly);

                // Add a marker for each logged location
                 /*  MarkerOptions mMarkerOption = new MarkerOptions()
                           .position(all_Location)
                           .title(time)
                           .icon(BitmapDescriptorFactory.fromResource(R.drawable.measle_blue));
                   Marker mMarker = mMap.addMarker(mMarkerOption);
                   markerList.add(mMarker);*/


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    //chuc nang dich benh
    private void get_disease()
    {
        database.getReference("DISEASE").addChildEventListener(new ChildEventListener() {
            LatLngBounds bounds;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User u = dataSnapshot.getValue(User.class);
                location_disease = new LatLng(u.Latitude, u.Longitude);

                builder.include(location_disease);
                bounds = builder.build();

                MarkerOptions mMarkerOption = new MarkerOptions()
                        .position(location_disease)
                        .title(getString(R.string.Disease) + " " + u.Type);
                       // .icon(BitmapDescriptorFactory.fromResource(R.drawable.disease));
                Marker mMarker = mMap.addMarker(mMarkerOption);
                markerList.add(mMarker);

                mMap.addCircle(new CircleOptions()
                        .center(location_disease)
                        .radius(u.Radius)
                        .strokeWidth(0)
                        //.strokeColor(Color.GREEN)
                        .fillColor(Color.argb(48, 255, 0, 0))
                        .clickable(true));

                float[] distance = new float[2];
                Location.distanceBetween( MainActivity.mlatitude, MainActivity.mlongitude,
                        u.Latitude, u.Longitude, distance);
                if( distance[0] > u.Radius  ){
                    Toast.makeText(getBaseContext(), "Outside", Toast.LENGTH_LONG).show();
                } else {
                    mediaPlayer.start();
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //chuc nang dong dat
    private void get_earthquake()
    {
        database.getReference("EARTHQUAKE").addChildEventListener(new ChildEventListener() {
            LatLngBounds bounds;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User u = dataSnapshot.getValue(User.class);
                location_earthquake = new LatLng(u.Latitude, u.Longitude);

                builder.include(location_earthquake);
                bounds = builder.build();

                MarkerOptions mMarkerOption = new MarkerOptions()
                        .position(location_earthquake)
                        .title(getString(R.string.Earthquake));
                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.earthquake));
                Marker mMarker = mMap.addMarker(mMarkerOption);
                markerList.add(mMarker);

                mMap.addCircle(new CircleOptions()
                        .center(location_earthquake)
                        .radius(u.Radius)
                        .strokeWidth(0)
                        //.strokeColor(Color.GREEN)
                        .fillColor(Color.argb(48, 255, 0, 0))
                        .clickable(true));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
