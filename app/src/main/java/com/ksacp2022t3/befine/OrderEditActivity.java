package com.ksacp2022t3.befine;

import static android.widget.Toast.*;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.ksacp2022t3.befine.adapters.MedicinesOrderAdapter;
import com.ksacp2022t3.befine.models.Medicine;
import com.ksacp2022t3.befine.models.Order;

import java.util.List;

public class OrderEditActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;


    RecyclerView recycler_medicines;
    FloatingActionButton btn_add_order,btn_delete;
    List<Medicine> medicines;
    MedicinesOrderAdapter adapter;
    List<Medicine> checked;
    ImageView btn_back;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_edit);
        recycler_medicines = findViewById(R.id.recycler_medicines);
        btn_add_order = findViewById(R.id.btn_add_order);
        btn_delete = findViewById(R.id.btn_delete);
        btn_back = findViewById(R.id.btn_back);





        String order_id=getIntent().getStringExtra("order_id");


        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        progressDialog.show();
        firestore.collection("accounts")
                .document(firebaseAuth.getUid())
                .collection("prescription")
                .orderBy("current_quantity")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        medicines=queryDocumentSnapshots.toObjects(Medicine.class);
                        firestore.collection("orders")
                                .document(order_id)
                                .collection("items")
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        progressDialog.dismiss();
                                         checked=queryDocumentSnapshots.toObjects(Medicine.class);
                                        adapter=new MedicinesOrderAdapter(medicines, OrderEditActivity.this,checked);
                                        recycler_medicines.setAdapter(adapter);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                         makeText(OrderEditActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                         finish();
                                    }
                                });



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(OrderEditActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                        finish();

                    }
                });
        btn_add_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(adapter.orderList.size()==0)
                {
                    makeText(OrderEditActivity.this,"You should select at least on item" , LENGTH_LONG).show();
                    return;

                }

                Log.d("dddd",adapter.orderList.size()+"k");
                Log.d("dddd",checked.size()+"t");
                if(checked.equals(adapter.orderList))

                {
                    makeText(OrderEditActivity.this,"No changes" , LENGTH_LONG).show();
                    return;

                }





                progressDialog.setTitle("Updating order");
                progressDialog.show();

                WriteBatch batch=firestore.batch();

                for (Medicine m:checked
                     ) {

                     batch.delete( firestore.collection("orders")
                                     .document(order_id)
                                     .collection("items")
                                     .document(m.getId()))
                                ;

                }

                for (Medicine m:adapter.orderList
                ) {

                        batch.set( firestore.collection("orders")
                                .document(order_id)
                                .collection("items")
                                .document(m.getId()),m)
                        ;

                }

                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        makeText(OrderEditActivity.this, "Order updated successfully", LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                         makeText(OrderEditActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();

                    }
                });








            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                progressDialog.setTitle("Canceling Order");
                firestore.collection("orders")
                        .document(order_id)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                firestore.collection("orders")
                                        .document(order_id)
                                                .collection("items")
                                                        .get()
                                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                        for(DocumentSnapshot doc:queryDocumentSnapshots.getDocuments())
                                                                        {
                                                                            firestore.collection("orders")
                                                                                    .document(order_id)
                                                                                    .collection("items")
                                                                                    .document(doc.getId())
                                                                                    .delete();
                                                                        }
                                                                    }
                                                                })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {

                                                                            }
                                                                        });
                                progressDialog.dismiss();

                                makeText(OrderEditActivity.this,"Order was cancelled successfully" , LENGTH_LONG).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                 makeText(OrderEditActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();

                            }
                        });
            }
        });
    }


}