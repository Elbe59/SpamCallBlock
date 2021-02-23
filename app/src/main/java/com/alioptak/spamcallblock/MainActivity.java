package com.alioptak.spamcallblock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
                goToHistoric();
            }
        });
        button_main_gocontact = findViewById(R.id.button_main_gocontact);
        button_main_gocontact.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                goToContact();
            }
        });

        imgeview_main_activate = findViewById(R.id.imgeview_main_activate);
        imgeview_main_activate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        /** PERMISSIONS **/
        AppPermissionManager apm = new AppPermissionManager();
        apm.askPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE, 9);
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

}