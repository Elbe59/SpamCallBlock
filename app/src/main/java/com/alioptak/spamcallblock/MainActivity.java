package com.alioptak.spamcallblock;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.alioptak.spamcallblock.service.PhoneStateBroadcastReceiver;

public class MainActivity extends AppCompatActivity {

    Button button_main_gocontact;
    Button button_main_gohistoric;
    ImageView imgeview_main_activate;
    String TAG = "MainActivity";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_main_gohistoric = findViewById(R.id.button_main_gohistoric);
        button_main_gohistoric.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                askPermission( permission.READ_CALL_LOG, 8);
            }
        });

        button_main_gocontact = findViewById(R.id.button_main_gocontact);
        button_main_gocontact.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                askPermission( permission.READ_CONTACTS, 7);
            }
        });

        imgeview_main_activate = findViewById(R.id.imgeview_main_activate);
        imgeview_main_activate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                askPermission(permission.READ_PHONE_STATE, 9);
            }
        });
    }

    public void goToContact(){
        Intent i = new Intent(MainActivity.this, ContactActivity.class);
        startActivity(i);
    }

    public void goToHistoric(){
        Intent i = new Intent(MainActivity.this, HistoricActivity.class);
        startActivity(i);
    }


    public void askPermission(String perm, int requestCode){
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
            final String[] PERMISSIONS_STORAGE = {perm};
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, requestCode);

        }else{
            switch(requestCode){
                case 7:
                    goToContact();
                    break;
                case 8:
                    goToHistoric();
                    break;
                case 9:
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionGranted = false;
        switch(requestCode){
            case 7:
                permissionGranted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                if(permissionGranted) goToContact();
                break;
            case 8:
                permissionGranted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                if(permissionGranted) goToHistoric();
                break;
            case 9:
                permissionGranted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                break;
            default:
                permissionGranted = false;
        }
        if(permissionGranted){
            goToHistoric();
        }else {
            Toast.makeText(this, "You don't assign permission.", Toast.LENGTH_SHORT).show();
        }
    }




}