package com.alioptak.spamcallblock.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneStateBroadcastReceiver extends BroadcastReceiver {

    private String TAG = "Receiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String state = extras.getString(TelephonyManager.EXTRA_STATE);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                String phoneNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                if(phoneNumber != null && phoneNumber.length() != 0){
                    Log.d(TAG, "Incoming call: " + phoneNumber);
                    // We can plug a method here. We'll be able to check whether or not this number needs to be blocked
                }
            }
        }
    }
}
