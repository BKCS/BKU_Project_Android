package com.application.hieu_nt.bkcs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by HIEU_NT on 14/11/2016.
 */
public class EndDayReceiver extends BroadcastReceiver {

    FirebaseDatabase database;

    @Override
    public void onReceive(Context context, Intent intent) {

       /* database = FirebaseDatabase.getInstance();
        database.getReference("PATH").removeValue();
        database.getReference("REPORT").removeValue();
        database.getReference("VEHICLE").removeValue();*/

    }
}
