package com.application.hieu_nt.bkcs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    private Button buttonSignIn;
    private TextView tv_forgot;

    private ImageView iv_logo;

    EditText email;
    String get_email;

    private FirebaseAuth mAuth;
    private SpotsDialog spotsDialog;

    private LocationManager locationManager;
    ConnectivityManager connectivityManager;
    NetworkInfo activeNetworkInfo;

    Animation anim_FadeIn, anim_Right, anim_Left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        checkinternet_gps();

        iv_logo = (ImageView)findViewById(R.id.logo);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        buttonSignIn = (Button) findViewById(R.id.email_sign_in_button);
        tv_forgot = (TextView)findViewById(R.id.textViewForgot);


        //user da login
        if(mAuth.getCurrentUser() != null){
            //close this activity
            finish();
            //opening profile activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }


        spotsDialog = new SpotsDialog(this, R.style.Custom);

        spotsDialog.getWindow().setBackgroundDrawableResource(R.drawable.manhinh);

        buttonSignIn.setOnClickListener(this);

        tv_forgot.setOnClickListener(this);

        Animation();
        iv_logo.startAnimation(anim_FadeIn);
        mEmailView.startAnimation(anim_Right);
        mPasswordView.startAnimation(anim_Left);
        buttonSignIn.startAnimation(anim_Right);
        tv_forgot.startAnimation(anim_Left);

    }

    private void Animation(){
        anim_FadeIn = AnimationUtils.loadAnimation(this, R.anim.anim_fadein);
        anim_Right = AnimationUtils.loadAnimation(this, R.anim.anim_right);
        anim_Left = AnimationUtils.loadAnimation(this, R.anim.anim_left);
    }

    public void checkinternet_gps()
    {
        //check internet and GPS
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER )
                || activeNetworkInfo == null || !activeNetworkInfo.isConnected())
        {
            askUserForLocation();
        }
    }

    public void askUserForLocation() {

        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        mAlertDialog.setTitle(getString(R.string.Use_Network))
                .setMessage(getString(R.string.Use_Network_Text));

        mAlertDialog.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        //When you touch outside of dialog bounds,
                        //the dialog gets canceled and this method executes.
                        askUserForLocation();
                    }
                }
        );

        mAlertDialog.setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intent);
                    }
                });

        mAlertDialog.setNegativeButton(getString(R.string.Cancel),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        mAlertDialog.show();
    }

    private void dialog_error(){
        Snackbar sb = Snackbar.make(buttonSignIn, getString(R.string.Wrong_Email_Password), Snackbar.LENGTH_SHORT);
        sb.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorSnackBar));
        sb.show();
        return;
    }

    private void userLogin(){
        String email = mEmailView.getText().toString().trim();
        String password  = mPasswordView.getText().toString().trim();

        checkinternet_gps();


        if (locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER )
                && activeNetworkInfo != null && activeNetworkInfo.isConnected()) {

            //checking if email and passwords are empty
            if(TextUtils.isEmpty(email)){
                Snackbar sb = Snackbar.make(buttonSignIn, getString(R.string.Enter_Email), Snackbar.LENGTH_SHORT);
                sb.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorSnackBar));
                sb.show();
                return;
            }

            if(TextUtils.isEmpty(password)){
                Snackbar sb = Snackbar.make(buttonSignIn, getString(R.string.Enter_Password), Snackbar.LENGTH_SHORT);
                sb.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorSnackBar));
                sb.show();
                return;
            }

            if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                spotsDialog.show();
            }

            //logging in the user
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            spotsDialog.dismiss();

                            //if the task is successfull
                            if (task.isSuccessful()) {
                                //start the profile activity
                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                dialog_error();
                            }
                        }
                    });
        }else
        {
            spotsDialog.dismiss();
        }

    }

    private void forgotPassword()
    {
        AlertDialog.Builder ad = new AlertDialog.Builder(this, R.style.MyDialogTheme);


        ad.setTitle(getString(R.string.Enter_Email));

        email = new EditText(this);
        ad.setView(email);

        ad.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        //When you touch outside of dialog bounds,
                        //the dialog gets canceled and this method executes.
                        forgotPassword();
                    }
                }
        );

        ad.setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int which) {

                get_email = email.getText().toString().trim();

                if(!get_email.isEmpty()) {

                    mAuth.sendPasswordResetEmail(get_email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                       toast1();
                                    }
                                    else
                                    {
                                        toast3();
                                    }
                                }
                            });
                }
                else
                {
                    toast2();
                    forgotPassword();
                }
            }
        });

        ad.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int which) {
               dlg.dismiss();
            }
        });


        ad.show().getWindow().setLayout(700,400);

    }


    @Override
    public void onClick(View view) {
        if(view == buttonSignIn)
        {
            userLogin();
        }

        if(view == tv_forgot)
        {
            forgotPassword();
        }
    }


    public void toast1()
    {
        Snackbar sb = Snackbar.make(buttonSignIn, getString(R.string.Check_Email), Snackbar.LENGTH_SHORT);
        sb.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorSnackBar));
        sb.show();
    }

    public void toast2()
    {
        Toast.makeText(this, getString(R.string.Email_Empty), Toast.LENGTH_SHORT).show();
    }

    public void toast3()
    {
        Snackbar sb = Snackbar.make(buttonSignIn, getString(R.string.Wrong_Email), Snackbar.LENGTH_SHORT);
        sb.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorSnackBar));
        sb.show();
    }
}

