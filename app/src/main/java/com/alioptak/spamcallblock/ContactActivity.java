package com.alioptak.spamcallblock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity {

    private String TAG = "contact_activity";

    private final String DISPLAY_NAME = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME;

    private final String FILTER = DISPLAY_NAME + " NOT LIKE '%@%'";

    private final String ORDER = String.format("%1$s COLLATE NOCASE", DISPLAY_NAME);

    @SuppressLint("InlinedApi")
    private final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
    };


    RecyclerView recyclerview_contacts_list;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        readContacts();

        recyclerview_contacts_list = findViewById(R.id.recyclerview_contacts_list);

        // We define and set our LayoutManager : Linear vertical (implicit)
        layoutManager = new LinearLayoutManager(this);
        recyclerview_contacts_list.setLayoutManager(layoutManager);

        // We instantiate and bind our Adapter
        mAdapter = new MyContactAdapter();
        recyclerview_contacts_list.setAdapter(mAdapter);

    }

    private void readContacts() {
        ContentResolver contentResolver=getContentResolver();
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
                        phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        cp.close();
                    }
                }

                if(phone != null && phone.length() > 0){
                    Log.d(TAG, phone + " " + name);
                    Contact contact = new Contact(name, phone);
                    contacts.add(contact);
                }


            } while (cursor.moveToNext());
            Singleton.getInstance().setContacts(contacts);
            cursor.close();
        }
    }

    public class MyContactAdapter extends RecyclerView.Adapter<MyContactAdapter.MyContactViewHolder>{

        @NonNull
        @Override
        public MyContactAdapter.MyContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // we specify the context for our viewHolder
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.cell_contact, parent, false);
            return new MyContactViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyContactAdapter.MyContactViewHolder holder, int position) {
            holder.setContact(Singleton.getInstance().getContactAtPosition(position));
        }

        @Override
        public int getItemCount() {
            return Singleton.getInstance().getNumberContacts(); // We get the number of contacts from the singleton
        }

        public class MyContactViewHolder extends RecyclerView.ViewHolder { // Things to display in the item

            TextView textview_cell_contact_name;

            public MyContactViewHolder(@NonNull View itemView) {
                super(itemView);
                textview_cell_contact_name = (TextView) itemView.findViewById(R.id.textview_cell_contact_name);
            }

            @SuppressLint("ResourceAsColor")
            public void setContact(final Contact contact) {
                Button button_cell_contact_block;
                button_cell_contact_block = (Button) itemView.findViewById(R.id.button_cell_contact_block);

                if (Singleton.getInstance().isBlocked(contact)) {
                    button_cell_contact_block.setBackgroundColor(R.color.red_block);
                    button_cell_contact_block.setText("unblock");
                } else {
                    button_cell_contact_block.setBackgroundColor(R.color.blue_project);
                    button_cell_contact_block.setText("block");
                }

                button_cell_contact_block.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Singleton.getInstance().isBlocked(contact)) {
                            Singleton.getInstance().unblock(contact);
                            button_cell_contact_block.setBackgroundColor(R.color.blue_project);
                            button_cell_contact_block.setText("block");
                            Log.d(TAG, "UnBlock Contact: " + contact.getPhone_number());
                        } else {
                            Singleton.getInstance().block(contact);
                            button_cell_contact_block.setBackgroundColor(R.color.red_block);
                            button_cell_contact_block.setText("unblock");
                            Log.d(TAG, "Block Contact: " + contact.getPhone_number());
                        }
                    }
                });

                String text = contact.getName();
                if(text.length() == 0){
                    text = contact.getPhone_number();
                }
                textview_cell_contact_name.setText(text);
            }
        }
    }
}