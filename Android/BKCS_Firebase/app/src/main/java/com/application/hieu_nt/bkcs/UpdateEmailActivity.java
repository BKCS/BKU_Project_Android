package com.application.hieu_nt.bkcs;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;

public class UpdateEmailActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String get_pass="", email;
    private SpotsDialog spotsDialog;

    Button btn;
    EditText pass, new_email, confirm_new_email;
    String get_new, get_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        btn = (Button)findViewById(R.id.button_change_email);
        new_email = (EditText)findViewById(R.id.new_email);
        confirm_new_email = (EditText)findViewById(R.id.confirm_email);

        spotsDialog = new SpotsDialog(this, R.style.Custom);

        spotsDialog.getWindow().setBackgroundDrawableResource(R.drawable.manhinh);

        onClicked();

        change();

    }


    public void onClicked()
    {
        AlertDialog.Builder ad = new AlertDialog.Builder(this, R.style.MyDialogTheme);

        ad.setTitle(getString(R.string.Enter_Password));

        pass = new EditText(this);
        ad.setView(pass);

        pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        ad.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        //When you touch outside of dialog bounds,
                        //the dialog gets canceled and this method executes.
                        onClicked();
                    }
                }
        );

        ad.setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int which) {

                get_pass = pass.getText().toString().trim();

                if(!get_pass.isEmpty()) {

                    spotsDialog.show();

                    AuthCredential credential = EmailAuthProvider
                            .getCredential(email, get_pass);

                    mAuth.getCurrentUser().reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        spotsDialog.dismiss();
                                    } else {
                                        toast_1();
                                        onClicked();
                                    }
                                }
                            });
                }
                else
                {
                    toast_1();
                    onClicked();
                }
            }
        });

        ad.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int which) {
                startActivity(new Intent(getApplicationContext(), AccountActivity.class));
            }
        });

        ad.show();
    }

    public void change()
    {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                get_new = new_email.getText().toString().trim();
                get_confirm = confirm_new_email.getText().toString().trim();

                if(!get_new.isEmpty() && !get_confirm.isEmpty())
                {

                    spotsDialog.show();

                    if (get_new.equals(get_confirm)) {
                        FirebaseAuth.getInstance().getCurrentUser().updateEmail(get_new)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mAuth.signOut();
                                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                        }
                                    }
                                });

                    }
                    else
                    {
                        toast_3();
                    }
                }
                else
                {
                    toast_2();
                }

            }
        });

    }

    public void toast_1()
    {
        spotsDialog.dismiss();
        Toast.makeText(this, getString(R.string.Wrong_Password), Toast.LENGTH_SHORT).show();

    }

    public void toast_2()
    {
        Snackbar sb = Snackbar.make(btn, getString(R.string.Email_Empty), Snackbar.LENGTH_SHORT);
        sb.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorSnackBar));
        sb.show();
    }

    public void toast_3()
    {
        spotsDialog.dismiss();
        Snackbar sb = Snackbar.make(btn, getString(R.string.Email_Match), Snackbar.LENGTH_SHORT);
        sb.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorSnackBar));
        sb.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, AccountActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
