package com.alioptak.spamcallblock;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import java.util.ArrayList;
import java.util.Date;

public class getCallDetails {

    ArrayList<Call> list_calls;

    private static void getCallDetails(Context context) {
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        while (cursor.moveToNext()) {

            String phNumber = cursor.getString(number);
            String callDate = cursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            /** A VOIR **/
            String date_string = callDayTime.getDate()+"/"+callDayTime.getMonth();
            /************/
            Call call = new Call(phNumber,date_string);
            Singleton.getInstance().addCall(call);
        }
        cursor.close();
    }
}