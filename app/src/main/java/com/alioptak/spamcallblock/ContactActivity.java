package com.alioptak.spamcallblock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ContactActivity extends AppCompatActivity {

    private String TAG = "contact_activity";

    RecyclerView recyclerview_contacts_list;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        recyclerview_contacts_list = findViewById(R.id.recyclerview_contacts_list);

        // We define and set our LayoutManager : Linear vertical (implicit)
        layoutManager = new LinearLayoutManager(this);
        recyclerview_contacts_list.setLayoutManager(layoutManager);

        // We instantiate and bind our Adapter
        mAdapter = new MyContactAdapter();
        recyclerview_contacts_list.setAdapter(mAdapter);
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
            holder.setContact(Singleton.getInstance().getContactAtPosition(position), Color.WHITE);
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
            public void setContact(final Contact contact, int lineColor) {
                this.itemView.setBackgroundColor(lineColor); // We change the background
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

                String text = contact.getFirstname() + " " + contact.getLastname();
                if(contact.getFirstname().length() == 0 && contact.getLastname().length() == 0){
                    text = contact.getPhone_number();
                }
                textview_cell_contact_name.setText(text);
            }
        }
    }
}