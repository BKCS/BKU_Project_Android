package com.application.bkcs.bkcs_socket;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import dmax.dialog.SpotsDialog;

public class AccountActivity extends AppCompatActivity {

    String get_pass, email;
    private SpotsDialog spotsDialog;

    Button btn;
    EditText pass, new_pass, confirm_new;
    String get_new, get_confirm;

    private Socket mSocket;
    {
        try {
           // mSocket = IO.socket("http://192.168.43.143:3000");
            mSocket = IO.socket("https://bkcs.herokuapp.com/");
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSocket.connect();

        btn = (Button)findViewById(R.id.button_change_password);
        new_pass = (EditText)findViewById(R.id.new_password);
        confirm_new = (EditText)findViewById(R.id.confirm_password);

        spotsDialog = new SpotsDialog(this, R.style.Custom);

        spotsDialog.getWindow().setBackgroundDrawableResource(R.drawable.manhinh);

        onClicked();

        change();

    }


    //hien thi hop thoai thong bao nhap old pass
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

                    mSocket.emit("client-confirm-password", LoginActivity.email, get_pass);

                    mSocket.on("ketqua-confirm-password", onNewMessage_ConfirmPassword );

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
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        ad.show();
    }


    private Emitter.Listener onNewMessage_ConfirmPassword = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String noidung;
                    try {
                        noidung = data.getString("noidung");
                        if(noidung == "true")
                        {
                            spotsDialog.dismiss();
                        }
                        else
                        {
                            toast_1();
                            onClicked();
                        }
                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };


    //change pass
    public void change()
    {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_new = new_pass.getText().toString().trim();
                get_confirm = confirm_new.getText().toString().trim();

                if(!get_new.isEmpty() && !get_confirm.isEmpty()) {

                    spotsDialog.show();

                    if (get_new.equals(get_confirm)) {
                        mSocket.emit("client-change-password", LoginActivity.email, get_confirm);

                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));


                    } else {
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
        Snackbar sb = Snackbar.make(btn, getString(R.string.Password_Empty), Snackbar.LENGTH_SHORT);
        sb.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorSnackBar));
        sb.show();
    }

    public void toast_3()
    {
        spotsDialog.dismiss();
        Snackbar sb = Snackbar.make(btn, getString(R.string.Password_Match), Snackbar.LENGTH_SHORT);
        sb.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorSnackBar));
        sb.show();
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
