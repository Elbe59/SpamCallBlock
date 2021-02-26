package com.alioptak.spamcallblock;

import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alioptak.spamcallblock.database.DataBaseHandler;

public class MainActivity extends AppCompatActivity {

    Button button_main_gocontact;
    Button button_main_gohistory;
    ImageView imgeview_main_activate;
    String TAG = "MainActivity";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_main_gohistory = findViewById(R.id.button_main_gohistory);
        button_main_gohistory.setOnClickListener(new View.OnClickListener(){
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
                askPermission( permission.ANSWER_PHONE_CALLS, 10);
                askPermission(permission.READ_PHONE_STATE, 9);
                askPermission(permission.MODIFY_PHONE_STATE, 11);
                // Only then: proceed.
            }
        });
        DataBaseHandler db = new DataBaseHandler(this);
        // Inserting Contacts
        Log.d("Insert: ", "Inserting ..");
        db.addContact(new Contact("Ravi", "9100000000"));
        db.addContact(new Contact("Srinivas", "9199999999"));
    }

    public void goToContact(){
        Intent i = new Intent(MainActivity.this, ContactActivity.class);
        startActivity(i);
    }

    public void goToHistory(){
        Intent i = new Intent(MainActivity.this, HistoryActivity.class);
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
                    goToHistory();
                    break;
                case 9: // DONT ADD ANYTHING HERE.
                case 10:
                case 11:
                    // Proceed!
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionGranted = false;
        try {
            permissionGranted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
        }catch (Exception e){
            e.printStackTrace();
        }
        switch(requestCode){
            case 7:
                if(permissionGranted) goToContact();
                break;
            case 8:
                if(permissionGranted) goToHistory();
                break;
            case 9:
            case 10:
            case 11:
                break;
            default:
                permissionGranted = false;
        }
        if(!permissionGranted){
            Toast.makeText(this, "Error: You didn't give the permission. Impossible to launch the service.", Toast.LENGTH_SHORT).show();
        }
    }
}