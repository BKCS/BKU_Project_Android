package com.application.hieu_nt.bkcs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , LocationListener {

    private FirebaseAuth mAuth;
    private LocationManager locationManager;

    FirebaseDatabase database;
    private User coor;
    static Double mlatitude, mlongitude;
    Double new_lat, new_long;

    String time, time_point;
    String id;
    String temp = "0";
    private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        try {

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
                askUserToOpenInternet();
            } else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                askUserToOpenGPS();
            }

            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock cpuWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "gps_service");
            cpuWakeLock.acquire();

            mAuth = FirebaseAuth.getInstance();
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


        }catch(Exception e){

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_user) {

            startActivity(new Intent(getApplicationContext(), AccountActivity.class));

        } else if (id == R.id.nav_location) {
            if (!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                askUserToOpenGPS();
            }else {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }

        } else if (id == R.id.nav_transport) {

            startActivity(new Intent(getApplicationContext(), TransportActivity.class));

        } else if (id == R.id.nav_cost) {

            startActivity(new Intent(getApplicationContext(), CostActivity.class));

        } else if (id == R.id.nav_voice) {

            startActivity(new Intent(getApplicationContext(), SpeechToTextActivity.class));

        } else if (id == R.id.nav_languages) {

            startActivity(new Intent(getApplicationContext(), LanguageActivity.class));

        } else if (id == R.id.nav_about) {

            startActivity(new Intent(getApplicationContext(), AboutActivity.class));

        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void askUserToOpenGPS() {

        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        mAlertDialog.setTitle("Location not available, Open GPS?")
                .setMessage("Activate GPS to use location services?");

        mAlertDialog.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        //When you touch outside of dialog bounds,
                        //the dialog gets canceled and this method executes.
                        askUserToOpenGPS();
                    }
                }
        );

        mAlertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });

        mAlertDialog.setNegativeButton("NO",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        mAlertDialog.show();
    }

    public void askUserToOpenInternet() {

        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        mAlertDialog.setTitle("Internet is not available, Open Internet?")
                .setMessage("Use Wi-Fi and cell networks?");

        mAlertDialog.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        //When you touch outside of dialog bounds,
                        //the dialog gets canceled and this method executes.
                        askUserToOpenInternet();
                    }
                }
        );

        mAlertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(intent);
                    }
                });

        mAlertDialog.setNegativeButton("NO",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        mAlertDialog.show();
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

        mlatitude = location.getLatitude();
        mlongitude = location.getLongitude();

        //lay du lieu trong thoi gian lam viec
       /* if(Integer.parseInt(time) >= 80000 && Integer.parseInt(time) <= 170000
                &&   calendar.get(Calendar.DAY_OF_WEEK) != 8) {*/

            //snap to road
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new ReadJSON().execute("https://roads.googleapis.com/v1/snapToRoads?path=" + mlatitude + "," + mlongitude + "&key=AIzaSyBdzvbEIEVGIWALsHswM5b74fOGkk3deyI");
                }
            });


            coor = new User(new_lat, new_long);
            //time = String.valueOf(new Timestamp(location.getTime()));


            Integer new_time = Integer.parseInt(time);
            Integer new_temp = Integer.parseInt(temp);
            Integer sub = new_time - new_temp;
            //dua len thong bao khi dung qua lau tai mot vi tri
            if (sub > 200) {
               // database.getReference("STOP").child(id).child(time_point).setValue(coor);
            }

            database.getReference("PATH").child(id).child(time_point).setValue(coor);

            temp = time;

        //}
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
}
