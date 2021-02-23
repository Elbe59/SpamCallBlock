package com.alioptak.spamcallblock;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.alioptak.spamcallblock.service.AppPermissionManager;

public class MainActivity extends AppCompatActivity {

    Button button_main_gocontact;
    Button button_main_gohistoric;
    ImageView imgeview_main_activate;
    @RequiresApi(api = Build.VERSION_CODES.M)
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

    public void goToContact(){
        Intent i = new Intent(this, ContactActivity.class);
        startActivity(i);
    }

    public void goToHistoric(){
        Intent i = new Intent(this, HistoricActivity.class);
        startActivity(i);
    }

}