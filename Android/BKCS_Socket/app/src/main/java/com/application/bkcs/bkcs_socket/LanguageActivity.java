package com.application.bkcs.bkcs_socket;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton bt1, bt2, bt3;
    Button bt;
    String languageToLoad, languageToCheck;
    boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        languageToCheck = Locale.getDefault().toString();

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup_1);
        bt1 = (RadioButton) findViewById(R.id.radioButton_en);
        bt2 = (RadioButton) findViewById(R.id.radioButton_vi);
        bt3 = (RadioButton) findViewById(R.id.radioButton_ja);
        bt = (Button) findViewById(R.id.button_language);


        switch (languageToCheck)
        {
            case "en_us":
                bt1.setChecked(true);
                break;
            case "en_US":
                bt1.setChecked(true);
                break;
            case "vi":
                bt2.setChecked(true);
                break;
            case "ja":
                bt3.setChecked(true);
                break;
        }


        bt1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                radioGroup.clearCheck();
                languageToLoad = "en_US";
                check = true;

            }
        });

        bt2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                radioGroup.clearCheck();
                languageToLoad = "vi";
                check = true;
            }
        });

        bt3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                radioGroup.clearCheck();
                languageToLoad = "ja";
                check = true;
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check ==true) {
                    doIt();
                }
            }
        });
    }

    //change language
    private void doIt()
    {
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
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

