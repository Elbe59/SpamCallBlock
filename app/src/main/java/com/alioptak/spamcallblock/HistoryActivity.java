package com.alioptak.spamcallblock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class HistoryActivity extends AppCompatActivity {

    private String TAG = "history_activity";

    RecyclerView recyclerview_history_calls;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getCallDetails(this);

        recyclerview_history_calls = findViewById(R.id.recyclerview_history_calls);

        // We define and set our LayoutManager : Linear vertical (implicit)
        layoutManager = new LinearLayoutManager(this);
        recyclerview_history_calls.setLayoutManager(layoutManager);

        // We instantiate and bind our Adapter
        mAdapter = new HistoryActivity.MyHistoryAdapter();
        recyclerview_history_calls.setAdapter(mAdapter);

    }
    private static void getCallDetails(Context context) {
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        // VOUS AVEZ OUBLIÉ DE RESET LA LISTE DES CALLS!
        Singleton.getInstance().resetCalls();
        while (cursor.moveToNext()) {

            String phNumber = cursor.getString(number);
            String callDate = cursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            /** A VOIR **/
            String date_string = "21/02";
            /************/
            Call call = new Call(phNumber,date_string);
            Singleton.getInstance().addCall(call);
        }
        cursor.close();
    }

    public class MyHistoryAdapter extends RecyclerView.Adapter<HistoryActivity.MyHistoryAdapter.MyHistoryViewHolder>{

        @NonNull
        @Override
        public HistoryActivity.MyHistoryAdapter.MyHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // we specify the context for our viewHolder
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.cell_call, parent, false);
            return new HistoryActivity.MyHistoryAdapter.MyHistoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryActivity.MyHistoryAdapter.MyHistoryViewHolder holder, int position) {
            holder.setHistory(Singleton.getInstance().getCallAtPosition(position));
        }

        @Override
        public int getItemCount() {
            return Singleton.getInstance().getNumberOfCall();
        }

        public class MyHistoryViewHolder extends RecyclerView.ViewHolder {

            TextView textview_cellcall_date;
            TextView textview_cellcall_phnumber;

            public MyHistoryViewHolder(@NonNull View itemView) {
                super(itemView);
                textview_cellcall_date = (TextView) itemView.findViewById(R.id.textview_cellcall_date);
                textview_cellcall_phnumber = (TextView) itemView.findViewById(R.id.textview_cellcall_phnumber);
            }

            @SuppressLint("ResourceAsColor")
            public void setHistory(final Call call) {

                ArrayList<Contact> contacts = Singleton.getInstance().getListContact();

                String text = call.getPhNumber();
                for (Contact c : contacts) {
                    if (c.getPhone_number() == call.getPhNumber()) {
                        text = c.getName();
                        break;
                    }
                }

                textview_cellcall_date.setText(call.getDate());
                textview_cellcall_phnumber.setText(text);

                Button button_cellcall_block;
                button_cellcall_block = (Button) itemView.findViewById(R.id.button_cellcall_block);

                if (Singleton.getInstance().isBlocked(call.getPhNumber())) {
                    button_cellcall_block.setBackgroundColor(R.color.red_block);
                    button_cellcall_block.setText("unblock");
                } else {
                    button_cellcall_block.setBackgroundColor(R.color.blue_project);
                    button_cellcall_block.setText("block");
                }

                button_cellcall_block.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Singleton.getInstance().isBlocked(call.getPhNumber())) {
                            Singleton.getInstance().unblock(call.getPhNumber());
                            button_cellcall_block.setBackgroundColor(R.color.blue_project);
                            button_cellcall_block.setText("block");
                            Log.d(TAG, "UnBlock Contact: " + call.getPhNumber());
                        } else {
                            Singleton.getInstance().block(call.getPhNumber());
                            button_cellcall_block.setBackgroundColor(R.color.red_block);
                            button_cellcall_block.setText("unblock");
                            Log.d(TAG, "Block Contact: " + call.getPhNumber());
                        }
                        MyHistoryAdapter.this.notifyDataSetChanged();
                    }
                });
            }
        }
    }
}