package com.alioptak.spamcallblock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ContactActivity extends AppCompatActivity {

    private String TAG = "contact_activity";

    RecyclerView recyclerview_contacts_list;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    private boolean actualSort;
    Button button_contact_sort;

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

        // Button to display all the contacts or only blocked one*
        actualSort = false;
        button_contact_sort = findViewById(R.id.button_contact_sort);
        button_contact_sort.setText("Blocked");
        button_contact_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualSort = !actualSort;
                if(actualSort){
                    button_contact_sort.setText("All");

                    // We instantiate and bind our Adapter
                    mAdapter = new MyContactAdapter();
                    recyclerview_contacts_list.setAdapter(mAdapter);
                }
                else {
                    button_contact_sort.setText("Blocked");
                    // We instantiate and bind our Adapter
                    mAdapter = new MyContactAdapter();
                    recyclerview_contacts_list.setAdapter(mAdapter);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        ArrayList<String> newBlockedContact = Singleton.getInstance().getListNumberBlocked();
        try {
            writeToFile(newBlockedContact,this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Méthode onPauseContact called");
        ArrayList<String> liste = null;
        try {
            liste = readFromFile(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int end = liste.size();
        Set<String> set = new HashSet<>();
        for(int i = 0; i < end; i++){
            set.add(liste.get(i));
        }
        Iterator<String> it = set.iterator();
        ArrayList<String> newListe = new ArrayList<>();
        int compteur = 0;
        while(it.hasNext()) {
            newListe.add(it.next());
        }
    }

    private void writeToFile(ArrayList<String> data, Context context) throws IOException {//String data
        String res="\n";
        for (String str : data) {
            System.out.println(str);
            str += "\n";
            //FileWriter output = new FileWriter("config.txt",false);
            res = str;
        }
        File path = context.getFilesDir();
        File file = new File(path, "test.txt");
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            stream.write(res.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stream.close();
        }
        /*try {
            //OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            //outputStreamWriter.write(data);
            //outputStreamWriter.close();
            String res="\n";
            for (String str : data) {
                System.out.println(str);
                str += "\n";
                //FileWriter output = new FileWriter("config.txt",false);
                res = str;
            }
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_APPEND));

            //FileOutputStream output = openFileOutput("config.txt", MODE_PRIVATE);

            outputStreamWriter.write(res);
            if (outputStreamWriter != null)
                outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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
            if(actualSort){
                holder.setContact(Singleton.getInstance().getContactBlockedAtPosition(position));
            }
            else{
                holder.setContact(Singleton.getInstance().getContactAtPosition(position));
            }
        }

        @Override
        public int getItemCount() {
            if(actualSort){
                return Singleton.getInstance().getNumberContactsBlocked(); // We get the number of contacts from the singleton
            }
            else{
                return Singleton.getInstance().getNumberContacts(); // We get the number of contacts from the singleton
            }
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
                            mAdapter = new MyContactAdapter();
                            recyclerview_contacts_list.setAdapter(mAdapter);

                            button_cell_contact_block.setBackgroundColor(R.color.blue_project);
                            button_cell_contact_block.setText("block");
                            Singleton.getInstance().unblock(contact);

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
    /*private ArrayList<String> readFromFile(Context context) {

        ArrayList<String> res = new ArrayList<>();
        String ret;
        try {
            InputStream inputStream = context.openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    System.out.println("receive "+receiveString);
                    res.add(receiveString);
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
                System.out.println("cc"+ret);
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return res;
    }*/
    private ArrayList<String> readFromFile(Context context) throws IOException {
        ArrayList<String> res = new ArrayList<>();

        File path = context.getFilesDir();
        File file = new File(path, "test.txt");
        int length = (int) file.length();

        byte[] bytes = new byte[length];

        FileInputStream in = new FileInputStream(file);
        try {
            in.read(bytes);
        } finally {
            in.close();
        }

        String contents = new String(bytes);
        System.out.println("My content: "+contents);
        return res;

        /*ArrayList<String> res = new ArrayList<>();
        String ret;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(context.openFileInput("config.txt"));
            if ( inputStreamReader != null ) {
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    System.out.println("receive "+receiveString);
                    res.add(receiveString);
                    stringBuilder.append(receiveString);
                }
                bufferedReader.close();
                ret = stringBuilder.toString();
                System.out.println("cc"+ret);
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return res;*/
    }
}