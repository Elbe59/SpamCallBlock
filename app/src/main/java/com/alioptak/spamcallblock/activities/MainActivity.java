package com.alioptak.spamcallblock.activities;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.alioptak.spamcallblock.Contact;
import com.alioptak.spamcallblock.R;
import com.alioptak.spamcallblock.Singleton;
import com.alioptak.spamcallblock.service.StorageManager;
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
    TextView textview_main_activate;
    String TAG = "MainActivity";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);

        Singleton.getInstance().setListNumberBlocked(StorageManager.readFileAsString(this));
        StorageManager.writeStringAsFile(this,new ArrayList<String>());

        setContentView(R.layout.activity_main);

        try{
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        FirebaseDatabase.getInstance().goOnline();
        Singleton.getInstance().fetchFromDatabase();

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


        imgeview_main_activate = (ImageView) findViewById(R.id.imgeview_main_activate);
        textview_main_activate = (TextView) findViewById(R.id.textview_main_activate);
        Log.d(TAG, ">>>>>" + Singleton.getInstance().getSTATUS_APPLICATION());
        setImageStatusApplication(); // Display the Pause/Play Image depending on the application STATUS when we launch the application
        imgeview_main_activate.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if( askPermission( permission.ANSWER_PHONE_CALLS, 9) && askPermission( permission.READ_PHONE_STATE, 11) && askPermission( permission.READ_EXTERNAL_STORAGE, 12) && askPermission( permission.WRITE_EXTERNAL_STORAGE, 13)) {
                    Singleton.getInstance().setSTATUS_APPLICATION(!Singleton.getInstance().getSTATUS_APPLICATION());
                    setImageStatusApplication();
                }
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();

        ArrayList<String> newBlockedContact = Singleton.getInstance().getListNumberBlocked();
        StorageManager.writeStringAsFile(this, newBlockedContact);
        StorageManager.readFileAsString(this);
        Log.d(TAG,"STATUS: "+Singleton.getInstance().getSTATUS_APPLICATION());
        if(Singleton.getInstance().getSTATUS_APPLICATION()) createNotification();
    }

    private void setImageStatusApplication(){
        if(Singleton.getInstance().getSTATUS_APPLICATION()){
            String uri = "@drawable/desactive_icon";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            Drawable res = getResources().getDrawable(imageResource);
            imgeview_main_activate.setImageDrawable(res);
            textview_main_activate.setText("Activated");
            Toast.makeText(getApplicationContext(),"The service is now turned ON.", Toast.LENGTH_SHORT).show();
        }
        else{
            String uri = "@drawable/active_icon";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            Drawable res = getResources().getDrawable(imageResource);
            imgeview_main_activate.setImageDrawable(res);
            textview_main_activate.setText("Deactivated");
            Toast.makeText(getApplicationContext(),"The service is now turned OFF.", Toast.LENGTH_SHORT).show();
            try{
                deleteNotification();
            }catch (Exception e){
                Log.e(TAG, e.getMessage());
            }
        }
    }


    public void goToContact(){
        Intent i = new Intent(MainActivity.this, ContactActivity.class);
        startActivity(i);
    }

    public void goToHistory(){
        Intent i = new Intent(MainActivity.this, HistoryActivity.class);
        startActivity(i);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
        return autorisationValide;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionGranted = false;
        try {
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                        phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)); // Cannot normalize in emulator.
                        cp.close();
                    }
                }
                Log.d(TAG, phone + "--->" + name);
                if(phone != null && phone.length() > 0){
                    Log.d(TAG, phone + "->" + name);
                    String phoneH164 = PhoneNumberUtils.formatNumberToE164(phone, "FR");
                    phone = phoneH164 == null ? phone : phoneH164;
                    Contact contact = new Contact(name, phone);
                    contacts.add(contact);
                }
            } while (cursor.moveToNext());


            Singleton.getInstance().setContacts(contacts);
            cursor.close();
        }
    }

    private void createNotification(){
        createNotificationChannel();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), "spamcallblock")
                .setContentTitle("SpamCallBlock est actif !")
                .setContentText("Les numéros bloqués ne seront donc pas notifiés")
                .setOngoing(true)
                .setSmallIcon(R.drawable.active_icon)
                .setContentIntent(pendingIntent)
                .setNotificationSilent()
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(888, notification);
    }

    private void deleteNotification(){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(888);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "SPAMCALLBLOCKER";
            String description = "indicator";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("spamcallblock", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}