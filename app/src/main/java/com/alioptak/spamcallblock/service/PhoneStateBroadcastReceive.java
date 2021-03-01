package com.alioptak.spamcallblock.service;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PhoneStateBroadcastReceive  extends BroadcastReceiver {

    private String TAG = "Receiver";

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseReference mDatabase;
        Bundle extras = intent.getExtras();
        FirebaseDatabase.getInstance().goOnline();
        if (extras != null) {
            String state = extras.getString(TelephonyManager.EXTRA_STATE);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                String phoneNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                if(phoneNumber != null && phoneNumber.length() != 0){
                    Log.d(TAG, "Incoming call: " + phoneNumber);
                    Toast.makeText(context, phoneNumber, Toast.LENGTH_SHORT).show();
                    // Here, we check whether or not we should block the incoming phone call.

                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child(phoneNumber).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.d(TAG, "Not blocking, not in database.");
                            }
                            else {
                                if(String.valueOf(task.getResult().getValue()).equalsIgnoreCase("1")){
                                    TelecomManager tcom = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                                    tcom.endCall();
                                    Log.d(TAG, "Blocking!");
                                }
                            }
                        }
                    });

                }
            }
        }
    }
}
