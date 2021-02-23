package com.alioptak.spamcallblock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ContactActivity extends AppCompatActivity {

    RecyclerView recyclerView_historic_call;
    Button button_sorting_contact;
    ProgressDialog progressDialog;
    String TAG = "HistoricActivity";

    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic);
        recyclerView_historic_call = findViewById(R.id.recyclerview_contacts_list);

        // We define and set our LayoutManager : Linear vertical (implicit)
        layoutManager = new LinearLayoutManager(this);
        recyclerView_historic_call.setLayoutManager(layoutManager);

        // We instantiate and bind our Adapter
        mAdapter = new MyAdapter();
        recyclerView_historic_call.setAdapter(mAdapter);

        // Other
        button_sorting_contact = findViewById(R.id.bouton_);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Téléchargement de l'historique");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //new fetchURLTask().execute("https://bettercalldave.io/hei/products.json");
    }

    /********* BEGIN DOWNLOAD AND PARSE DATA ************/

    private ArrayList<Contact> jsonToArray (String data) throws JSONException {
        Log.d(TAG,data);
        ArrayList<Produit> qc_ed_arrayList = new ArrayList<Produit>();
        JSONArray qc_ed_jsonArray = new JSONArray(data);

        for (int i=0 ; i < qc_ed_jsonArray.length(); i++){
            JSONObject qc_ed_object = qc_ed_jsonArray.getJSONObject(i);

            int qc_ed_id = qc_ed_object.getInt("id");
            String qc_ed_price = qc_ed_object.getString("price");
            String qc_ed_name = qc_ed_object.getString("name");
            String qc_ed_image = qc_ed_object.getString("image");
            String qc_ed_description = qc_ed_object.getString("description");

            Produit qc_ed_produit = new Produit(qc_ed_id,qc_ed_name,qc_ed_price,qc_ed_image,qc_ed_description);
            qc_ed_arrayList.add(qc_ed_produit);
        }
        Log.d(TAG,qc_ed_arrayList.toString());
        return qc_ed_arrayList;
    }

    // When we click on the "Panier" button
    public void goToPanierActivity(){
        Intent intent = new Intent(this, Panier.class);
        startActivity(intent);
    }

    /********* ADAPTER RECYCLER VIEW ************/
    // The adapter is used to tell our recycler view what we want to render and how we want to render it.
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

        // Tells our RecyclerView that we'll be using the list_cell layout that we created
        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // we specify the contexte for our viewHolder
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.list_cell, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
            if(position % 2 == 0){ // We change the color based and diplay the corresponding restaurant
                holder.setProduit(Singleton.getInstance().getProduitAtPosition(position), Color.GRAY);
            }else{
                holder.setProduit(Singleton.getInstance().getProduitAtPosition(position),Color.LTGRAY);
            }
        }

        @Override
        public int getItemCount() {
            return Singleton.getInstance().getNumberOfProduit(); // We get the number of restaurants from the singleton
        }

        public class MyViewHolder extends RecyclerView.ViewHolder { // Things to display in the item

            TextView qc_ed_textview_produit_name;
            TextView qc_ed_textview_produit_description;
            ImageView qc_ed_imageview_produit_logo;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                qc_ed_textview_produit_name = (TextView) itemView.findViewById(R.id.textview_produit_name);
                qc_ed_imageview_produit_logo = (ImageView) itemView.findViewById(R.id.imageview_produit_logo);
                qc_ed_textview_produit_description = (TextView) itemView.findViewById(R.id.textview_produit_description);

            }

            public void setProduit(final Produit produit, int lineColor) {
                this.itemView.setBackgroundColor(lineColor); // We change the background

                Button qc_ed_button = this.itemView.findViewById(R.id.button_produit_ajouter);
                qc_ed_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Singleton.getInstance().addProduitToPanier(produit);
                        Log.d(TAG,"Produit ajoute: "+produit.getName());
                    }
                });

                qc_ed_textview_produit_name.setText(produit.getName());
                qc_ed_textview_produit_description.setText(produit.getDescription());

                Picasso.get().load(produit.getImage()) // Same for the image
                        .into(qc_ed_imageview_produit_logo, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Log.d(TAG,"IMAGE ERROR : "+e.getMessage());
                            }
                        }); // A lot of stuff here just to display an error...
            }
        }
    }
}