package com.application.hieu_nt.bkcs;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SpeechToTextActivity extends AppCompatActivity {

    ImageButton btn_record, btn_delete, btn_tick;
    TextView Text;
    Boolean key = true;

    long startTime;
    long countUp;
    Chronometer Watch;

    FirebaseDatabase database;
    String id, time, text_report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_to_text);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();
        id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Watch = (Chronometer) findViewById(R.id.chronometer);
        startTime = SystemClock.elapsedRealtime();


        Text = (TextView)findViewById(R.id.text);

        //dem thoi gian
        Watch.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
            @Override
            public void onChronometerTick(Chronometer arg0) {
                countUp = (SystemClock.elapsedRealtime() - arg0.getBase()) / 1000;
            }
        });



        btn_record  = (ImageButton)findViewById(R.id.imageBut);
        btn_delete = (ImageButton)findViewById(R.id.imageBut1);
        btn_tick = (ImageButton)findViewById(R.id.imageBut2);

        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bat chuc nang ghi am
                if(key == true) {
                    btn_record.setBackgroundResource(R.drawable.pause);
                    btn_delete.setVisibility(View.INVISIBLE);
                    btn_tick.setVisibility(View.INVISIBLE);
                    key = false;

                    Watch.start();

                    Intent intent = new Intent(
                            RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault());

                    try {
                        startActivityForResult(intent, 1);
                        Text.setText("");
                    } catch (ActivityNotFoundException a) {
                        Toast.makeText(getApplicationContext(),
                                "Your device doesn't support Speech to Text",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    btn_record.setBackgroundResource(R.drawable.record);
                    btn_delete.setVisibility(View.VISIBLE);
                    btn_tick.setVisibility(View.VISIBLE);
                    key = true;


                    Watch.stop();
                }
            }
        });


        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                question_delete();

            }
        });


        btn_tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                question_tick();

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Text.setText(text.get(0));
                }
                break;
            }
        }
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


    public void question_delete() {

        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        mAlertDialog.setTitle("Are you sure you want to delete?");

        mAlertDialog.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        //When you touch outside of dialog bounds,
                        //the dialog gets canceled and this method executes.
                        question_delete();
                    }
                }
        );

        mAlertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Text.setText("");

                btn_delete.setVisibility(View.INVISIBLE);
                btn_tick.setVisibility(View.INVISIBLE);

                Watch.setBase(SystemClock.elapsedRealtime());
            }
        });

        mAlertDialog.setNegativeButton("NO",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mAlertDialog.show();
    }


    public void question_tick() {

        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        mAlertDialog.setTitle("Are you sure you want to send report?");

        mAlertDialog.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        //When you touch outside of dialog bounds,
                        //the dialog gets canceled and this method executes.
                        question_tick();
                    }
                }
        );

        mAlertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if(!Text.equals("")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss");
                    time = sdf.format(new Date());

                    text_report = Text.getText().toString();

                    database.getReference("REPORT").child(id).child(time).setValue(text_report);

                    Toast.makeText(SpeechToTextActivity.this, "Done", Toast.LENGTH_SHORT).show();

                    btn_delete.setVisibility(View.INVISIBLE);
                    btn_tick.setVisibility(View.INVISIBLE);

                    Watch.setBase(SystemClock.elapsedRealtime());
                }
            }
        });

        mAlertDialog.setNegativeButton("NO",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mAlertDialog.show();
    }
}
