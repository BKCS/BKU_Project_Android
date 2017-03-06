package com.application.hieu_nt.bkcs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TransportActivity extends AppCompatActivity {

    ImageButton btn1, btn2, btn3, btn4;
    Button btn;
    private String type = "";
    FirebaseDatabase database;
    String id, time, time_car;
    private Vehicle coor_car;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);
        //getSupportActionBar().hide();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            askUserToOpenInternet();
        }

        database = FirebaseDatabase.getInstance();
        id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        btn1 = (ImageButton)findViewById(R.id.imageButton1);
        btn2 = (ImageButton)findViewById(R.id.imageButton2);
        btn3 = (ImageButton)findViewById(R.id.imageButton3);
        btn4 = (ImageButton)findViewById(R.id.imageButton4);
        btn = (Button)findViewById(R.id.buttonOK);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type= "car";
                btn1.setBackgroundResource(R.drawable.vehicle_car_big_src);
                btn2.setBackgroundResource(R.drawable.vehicle_bus_big);
                btn3.setBackgroundResource(R.drawable.vehicle_scooter_big);
                btn4.setBackgroundResource(R.drawable.vehicle_bicycle_big);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="bus";
                btn1.setBackgroundResource(R.drawable.vehicle_car_big);
                btn2.setBackgroundResource(R.drawable.vehicle_bus_big_src);
                btn3.setBackgroundResource(R.drawable.vehicle_scooter_big);
                btn4.setBackgroundResource(R.drawable.vehicle_bicycle_big);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="motorbike";
                btn1.setBackgroundResource(R.drawable.vehicle_car_big);
                btn2.setBackgroundResource(R.drawable.vehicle_bus_big);
                btn3.setBackgroundResource(R.drawable.vehicle_scooter_big_src);
                btn4.setBackgroundResource(R.drawable.vehicle_bicycle_big);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="bicycle";
                btn1.setBackgroundResource(R.drawable.vehicle_car_big);
                btn2.setBackgroundResource(R.drawable.vehicle_bus_big);
                btn3.setBackgroundResource(R.drawable.vehicle_scooter_big);
                btn4.setBackgroundResource(R.drawable.vehicle_bicycle_big_src);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();

                SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
                time = sdf.format(new Date());

                SimpleDateFormat abc = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
                time_car = abc.format(new Date());

               /* if(Integer.parseInt(time) >= 80000
                        && Integer.parseInt(time) <= 170000
                        &&   calendar.get(Calendar.DAY_OF_WEEK) != 8
                        && type != "") {*/
                if (type != ""){

                    coor_car = new Vehicle(MainActivity.mlatitude, MainActivity.mlongitude, type, time_car);
                    database.getReference("VEHICLE").child(id).child(time).setValue(coor_car);

               /* database.getReference("VEHICLE").child(id).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        //neu da co du lieu tren database
                        if(dataSnapshot.getValue() != null) {
                            Vehicle v = dataSnapshot.getValue(Vehicle.class);

                            if(Math.abs(v.Latitude - MainActivity.mlatitude) > 0.01
                                    || Math.abs(v.Longitude - MainActivity.mlongitude) > 0.01)
                            {
                                coor_car = new Vehicle(MainActivity.mlatitude, MainActivity.mlongitude, type);
                                database.getReference("VEHICLE").child(id).child(time).setValue(coor_car);
                            }
                            else
                            {
                                coor_car = new Vehicle(v.Latitude, v.Longitude, type);
                                database.getReference("VEHICLE").child(id).child(time).setValue(coor_car);
                            }
                        }
                        else
                        {
                            coor_car = new Vehicle(MainActivity.mlatitude, MainActivity.mlongitude, type);
                            database.getReference("VEHICLE").child(id).child(time).setValue(coor_car);
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
                });*/

                }

                if (type != "") {
                    Intent intent = new Intent(TransportActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
