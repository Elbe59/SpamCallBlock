package com.alioptak.spamcallblock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button button_main_gocontact;
    Button button_main_gohistoric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_main_gohistoric = findViewById(R.id.button_main_gohistoric);
        button_main_gohistoric.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                checkPermissionOnClick();
            }
        });
        button_main_gocontact = findViewById(R.id.button_main_gocontact);
        button_main_gocontact.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                goToContact();
            }


        });
    }

    public void checkPermissionOnClick(){
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CALL_LOG)) {

            final String[] PERMISSIONS_STORAGE = {permission.READ_CALL_LOG};
            //Asking request Permissions
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, 9);

        }
    }

    public void goToContact(){
        Intent i = new Intent(this, ContactActivity.class);
        startActivity(i);
    }

    public void goToHistoric(){
        Intent i = new Intent(this, HistoricActivity.class);
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionGranted = false;
        switch(requestCode){
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