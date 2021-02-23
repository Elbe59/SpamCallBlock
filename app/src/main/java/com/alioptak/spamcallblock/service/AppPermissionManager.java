package com.alioptak.spamcallblock.service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class AppPermissionManager extends AppCompatActivity {


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void askPermission(Activity context, String perm, int requestCode){
        if (checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
            final String[] PERMISSIONS_STORAGE = {perm};
            ActivityCompat.requestPermissions(AppPermissionManager.this, PERMISSIONS_STORAGE, requestCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionGranted = false;
        switch(requestCode){
            case 9: // 9 : READ_PHONE_STATE
                permissionGranted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                break;
            default:
                permissionGranted = false;
        }
        if(!permissionGranted){
            Toast.makeText(this, "You don't assign permission.", Toast.LENGTH_SHORT).show();
        }
    }
}
