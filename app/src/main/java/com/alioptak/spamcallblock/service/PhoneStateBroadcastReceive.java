package com.alioptak.spamcallblock.service;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.alioptak.spamcallblock.MainActivity;
import com.alioptak.spamcallblock.Singleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PhoneStateBroadcastReceive  extends BroadcastReceiver {

    private String TAG = "CallReceiver";

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Singleton.getInstance().getSTATUS_APPLICATION()) {
            DatabaseReference mDatabase;
            Bundle extras = intent.getExtras();
            FirebaseDatabase.getInstance().goOnline();
            if (extras != null) {
                String state = extras.getString(TelephonyManager.EXTRA_STATE);
                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    String phoneNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    if (phoneNumber != null && phoneNumber.length() != 0) {

                        String pHNumber_H164 = PhoneNumberUtils.formatNumberToE164(phoneNumber, "FR");
                        phoneNumber = pHNumber_H164 == null ? phoneNumber : pHNumber_H164;

                        Log.d(TAG, "Incoming call: " + phoneNumber);
                        Toast.makeText(context, phoneNumber, Toast.LENGTH_SHORT).show();

                        // Here, we check whether or not we should block the incoming phone call.
                        TelecomManager tcom = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                        if (Singleton.getInstance().getListNumberBlocked().contains(phoneNumber) || Singleton.getInstance().isNumberInDatabase(phoneNumber)) {
                            tcom.endCall();
                            Log.d(TAG, "Blocking!");
                        }
                    }
                }
            }
        }
    }
}
