package com.alioptak.spamcallblock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        mAdapter = new MyAdapter();
        recyclerview_contacts_list.setAdapter(mAdapter);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // we specify the contexte for our viewHolder
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.cell_contact, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
            holder.setContact(Singleton.getInstance().getContactAtPosition(position), Color.WHITE);
        }

        @Override
        public int getItemCount() {
            return Singleton.getInstance().getNumberContacts();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView textview_cell_contact_name;
            Button button_cell_contact_block;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                textview_cell_contact_name = (TextView) itemView.findViewById(R.id.textview_cell_contact_name);
                button_cell_contact_block = (Button) itemView.findViewById(R.id.button_cell_contact_block);
            }

            public void setContact(final Contact c, int lineColor) {
                this.itemView.setBackgroundColor(lineColor);

                Button button = this.itemView.findViewById(R.id.button_cell_contact_block);
                boolean is_blocked = Singleton.getInstance().isBlocked(c);
                button.setText(is_blocked ? "unblock" : "Block");
                button.setBackgroundColor(is_blocked ? 0xFF0000 : 0x00AA00);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "Block toggle on");
                    }
                });

                textview_cell_contact_name.setText((c.getFirstname().length() == 0 && c.getLastname().length() == 0) ? c.getPhone_number() : c.getFirstname() + " " + c.getLastname());
            }

        }
    }

}