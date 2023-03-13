package com.ksacp2022t3.befine;

import static android.widget.Toast.*;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.ksacp2022t3.befine.adapters.MedicinesOrderAdapter;
import com.ksacp2022t3.befine.models.Medicine;
import com.ksacp2022t3.befine.models.Order;

import java.util.List;

public class OrderSelectActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    RecyclerView recycler_medicines;
    FloatingActionButton btn_add_order;
    List<Medicine> medicines;
    MedicinesOrderAdapter adapter;

    String pharma_id,pharma_name;
    String patient_id,patient_name;
    String logged_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_select);
        recycler_medicines = findViewById(R.id.recycler_medicines);
        btn_add_order = findViewById(R.id.btn_add_order);


        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        if(getIntent().getStringExtra("pharma_id")!=null)
        {
            logged_type="Patient";
        }
        else {
            logged_type="Pharmacy";
        }
        SharedPreferences sharedPreferences= OrderSelectActivity.this.getSharedPreferences("befine",MODE_PRIVATE);

        if(logged_type.equals("Patient")) {
            pharma_id = getIntent().getStringExtra("pharma_id");
            pharma_name = getIntent().getStringExtra("pharma_name");
            patient_id=firebaseAuth.getUid();
            patient_name=sharedPreferences.getString("full_name","p");
        }
        else
        {
            pharma_id=firebaseAuth.getUid();
            pharma_name=sharedPreferences.getString("pharmacy_name","p");
            patient_id = getIntent().getStringExtra("patient_id");
            patient_name = getIntent().getStringExtra("patient_name");
        }




        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("Loading");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();
        firestore.collection("accounts")
                .document(patient_id)
                .collection("prescription")
                .orderBy("current_quantity")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        medicines=queryDocumentSnapshots.toObjects(Medicine.class);
                        adapter=new MedicinesOrderAdapter(medicines, OrderSelectActivity.this);
                        recycler_medicines.setAdapter(adapter);






                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                         makeText(OrderSelectActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                         finish();

                    }
                });
        btn_add_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(adapter.orderList.size()==0)
                {
                    makeText(OrderSelectActivity.this,"You should select at least on item" , LENGTH_LONG).show();
                return;

                }

                progressDialog.setTitle("Sending order");
                progressDialog.show();

                Order order=new Order();
                order.setPatient_id(patient_id);
                order.setName(patient_name);
                order.setPharmacy_id(pharma_id);
                order.setPharmacy_name(pharma_name);
                DocumentReference doc=firestore.collection("orders")
                        .document();
                order.setId(doc.getId());
                if(logged_type.equals("Pharmacy"))
                    order.setStatus("Reviewed");

                doc.set(order)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                WriteBatch batch=firestore.batch();



                                for (Medicine item:adapter.orderList
                                     ) {
                                    if(logged_type.equals("Pharmacy"))
                                        item.setAvailable(true);
                                   DocumentReference itdoc= doc.collection("items")
                                            .document(item.getId());
                                    batch.set(itdoc,item);
                                }

                                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        makeText(OrderSelectActivity.this,"Order sent successfully" , LENGTH_LONG).show();
                                        Intent intent;
                                        if(logged_type.equals("Patient"))
                                        intent = new Intent(OrderSelectActivity.this,PatientHomeActivity. class);
                                        else
                                            intent = new Intent(OrderSelectActivity.this,PharmacistHomeActivity. class);

                                        intent.putExtra("launch_orders","true");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                         makeText(OrderSelectActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();

                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                 makeText(OrderSelectActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();


                            }
                        });





            }
        });
    }
}