package com.application.bkcs.bkcs_socket;

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

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TransportActivity extends AppCompatActivity {

    ImageButton btn1, btn2, btn3, btn4;
    Button btn;
    private String type = "";
    String time_car;
    public static Double lat, lng;
    public static String time_af, type_af;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            askUserToOpenInternet();
        }

        MainActivity.mSocket.connect();

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

                SimpleDateFormat abc = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
                time_car = abc.format(new Date());

                if (type != "") {

                    MainActivity.mSocket.emit("client-goi-vehicle", type, time_car, MainActivity.mlatitude, MainActivity.mlongitude);

                    MainActivity.mSocket.on("ketquaVehicle", onNewMessage_Vehicle );

                    Intent intent = new Intent(TransportActivity.this, MainActivity.class);
                    startActivity(intent);
                }

            }
        });

    }


    private Emitter.Listener onNewMessage_Vehicle = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];

                    try {
                        lat = data.getDouble("Latitude");
                        lng = data.getDouble("Longitude");
                        time_af = data.getString("Time");
                        type_af = data.getString("Type");

                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };

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
