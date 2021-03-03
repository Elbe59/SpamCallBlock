package com.alioptak.spamcallblock;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alioptak.spamcallblock.service.StorageManager;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private final String DISPLAY_NAME = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME;

    private final String FILTER = DISPLAY_NAME + " NOT LIKE '%@%'";

    private final String ORDER = String.format("%1$s COLLATE NOCASE", DISPLAY_NAME);

    private boolean STATUS = false;


    @SuppressLint("InlinedApi")
    private final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
    };

    Button button_main_gocontact;
    Button button_main_gohistory;
    ImageView imgeview_main_activate;
    TextView textview_main_activate;
    String TAG = "MainActivity";
    public boolean getStatus(){
        return STATUS;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Singleton.getInstance().setListNumberBlocked(StorageManager.readFileAsString(this));

        setContentView(R.layout.activity_main);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().goOnline();

        askPermission( permission.READ_CONTACTS, 10);

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
        textview_main_activate = findViewById(R.id.textview_main_activate);
        imgeview_main_activate.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if( askPermission( permission.ANSWER_PHONE_CALLS, 9) && askPermission( permission.READ_PHONE_STATE, 11) && askPermission( permission.READ_EXTERNAL_STORAGE, 12) && askPermission( permission.WRITE_EXTERNAL_STORAGE, 13))
                    if(Singleton.getInstance().getSTATUS_APPLICATION()){
                        String uri = "@drawable/active_icon";  // where myresource (without the extension) is the file
                        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                        Drawable res = getResources().getDrawable(imageResource);
                        imgeview_main_activate.setImageDrawable(res);
                        textview_main_activate.setText("Click on image to activate SPAMCALLBLOCKER");
                        Singleton.getInstance().setSTATUS_APPLICATION(false);
                    }
                    else{

                        String uri = "@drawable/desactive_icon";  // where myresource (without the extension) is the file
                        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                        Drawable res = getResources().getDrawable(imageResource);
                        imgeview_main_activate.setImageDrawable(res);
                        textview_main_activate.setText("Click on image to desactivate SPAMCALLBLOCKER");
                        Singleton.getInstance().setSTATUS_APPLICATION(true);


                    }

                // Only then: proceed.*/
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        ArrayList<String> newBlockedContact = Singleton.getInstance().getListNumberBlocked();
        StorageManager.writeStringAsFile(this, newBlockedContact);
        System.out.println("Méthode onPause called");
    }


    public void goToContact(){
        Intent i = new Intent(MainActivity.this, ContactActivity.class);
        startActivity(i);
    }

    public void goToHistory(){
        Intent i = new Intent(MainActivity.this, HistoryActivity.class);
        startActivity(i);
    }


    public boolean askPermission(String perm, int requestCode){
        boolean autorisationValide =false;
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
            final String[] PERMISSIONS_STORAGE = {perm};
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, requestCode);

        }else{
            autorisationValide = true;
            switch(requestCode){
                case 7:
                    goToContact();
                    break;
                case 8:
                    goToHistory();
                    break;
                case 9: break;// DONT ADD ANYTHING HERE.
                case 10:
                    Log.d(TAG, ">Contacts...");
                    readContacts();
                    ArrayList<Contact> listContact = Singleton.getInstance().getListContact();
                    for (Contact contact: listContact) {
                        if(Singleton.getInstance().isBlocked(contact.getPhone_number())){
                            Singleton.getInstance().blockContact(contact);
                        }
                    }
                case 11:
            }
        }
        System.out.println(requestCode + "  " + autorisationValide);
        return autorisationValide;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionGranted = false;
        try {
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            System.out.println("GRANTED");
        }catch (Exception e){
        }
        switch(requestCode){
            case 7:
                if(permissionGranted){
                    readContacts();
                    goToContact();
                }
                break;
            case 8:
                if(permissionGranted) goToHistory();
                break;
            case 9: if(permissionGranted){ askPermission( permission.READ_PHONE_STATE, 11);}break;
            case 10:
                if(permissionGranted){
                    Log.d(TAG, "Contacts...");
                    readContacts();
                }
                break;
            case 11: if(permissionGranted){askPermission(permission.READ_EXTERNAL_STORAGE, 12);}break;
            case 12: if(permissionGranted){askPermission(permission.WRITE_EXTERNAL_STORAGE, 13);}break;
            case 13: break;
            default:
                permissionGranted = false;
        }
        if(!permissionGranted){
            Toast.makeText(this, "Error: You didn't give the permission. Impossible to launch the service." + requestCode, Toast.LENGTH_SHORT).show();
        }
    }

    public void readContacts() {
        ContentResolver contentResolver= getContentResolver();
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, FILTER, null, ORDER);
        if (cursor != null && cursor.moveToFirst()) {

            do {
                // get the contact's information
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                Integer hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                // get the user's phone number
                String phone = null;
                if (hasPhone > 0) {
                    Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    if (cp != null && cp.moveToFirst()) {
                        phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
                        cp.close();
                    }
                }
                if(phone != null && phone.length() > 0){
                    if(phone.length()>2){
                        if(!phone.substring(0,3).contentEquals("+33") && phone.length()==10){
                            phone = "+33" + phone.substring(1);
                        }
                    }
                    Log.d(TAG, phone + "->" + name);
                    Contact contact = new Contact(name, phone);
                    contacts.add(contact);
                }
            } while (cursor.moveToNext());
            Singleton.getInstance().setContacts(contacts);
            cursor.close();
        }
    }

}