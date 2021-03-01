package com.alioptak.spamcallblock;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.provider.ContactsContract;
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
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String DISPLAY_NAME = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME;

    private final String FILTER = DISPLAY_NAME + " NOT LIKE '%@%'";

    private final String ORDER = String.format("%1$s COLLATE NOCASE", DISPLAY_NAME);

    @SuppressLint("InlinedApi")
    private final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
    };

    Button button_main_gocontact;
    Button button_main_gohistory;
    ImageView imgeview_main_activate;
    String TAG = "MainActivity";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        imgeview_main_activate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                askPermission( permission.ANSWER_PHONE_CALLS, 10);
                askPermission(permission.READ_PHONE_STATE, 9);
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
                case 11:
                    // Proceed!
                    break;
                case 10:
                    readContacts();


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
            case 11:
                break;
            case 10:
                permissionGranted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                readContacts();
            default:
                permissionGranted = false;
        }
        if(!permissionGranted){
            Toast.makeText(this, "Error: You didn't give the permission. Impossible to launch the service.", Toast.LENGTH_SHORT).show();
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
                    Log.d(TAG, phone + " " + name);
                    Contact contact = new Contact(name, phone);
                    contacts.add(contact);
                }


            } while (cursor.moveToNext());
            Singleton.getInstance().setContacts(contacts);
            cursor.close();
        }
    }
}