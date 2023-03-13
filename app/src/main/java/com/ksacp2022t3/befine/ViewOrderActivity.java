package com.ksacp2022t3.befine;

import static android.widget.Toast.*;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.ksacp2022t3.befine.adapters.PharmacyMedicinesOrderAdapter;
import com.ksacp2022t3.befine.models.Medicine;
import com.ksacp2022t3.befine.models.Order;

import java.util.List;

public class ViewOrderActivity extends AppCompatActivity {

    TextView txt_title,txt_view_file;
    RecyclerView recycler_medicines;
    ProgressDialog progressDialog;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    AppCompatButton btn_submit,btn_cancel,btn_confirm,btn_finish;
    List<Medicine> medicines;
    PharmacyMedicinesOrderAdapter adapter;
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        txt_title = findViewById(R.id.txt_title);
        txt_view_file = findViewById(R.id.txt_view_file);
        recycler_medicines = findViewById(R.id.recycler_medicines);
        btn_submit = findViewById(R.id.btn_submit);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_back = findViewById(R.id.btn_back);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_finish = findViewById(R.id.btn_finish);








        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();


        String order_id=getIntent().getStringExtra("order_id");


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                progressDialog.setTitle("Canceling");
                firestore.collection("orders")
                        .document(order_id)
                        .update("status","Canceled")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                makeText(ViewOrderActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                            }
                        });
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              progressDialog.show();
              progressDialog.setTitle("Submit info");
                firestore.collection("orders")
                        .document(order_id)
                        .update("status","Reviewed")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                for (Medicine m: medicines
                                ) {
                                    firestore.collection("orders")
                                            .document(order_id)
                                            .collection("items")
                                            .document(m.getId())
                                            .update("available",m.isAvailable());
                                }

                                makeText(ViewOrderActivity.this,"Order has been submitted successfully" , LENGTH_LONG).show();
                                finish();



                                //TODO: send FCM message

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                makeText(ViewOrderActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                            }
                        });

            }
        });

//        btn_confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                progressDialog.show();
//                progressDialog.setTitle("Confirm  Order");
//                firestore.collection("orders")
//                        .document(order_id)
//                        .update("status","Confirmed")
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void unused) {
//                                progressDialog.dismiss();
//                                makeText(ViewOrderActivity.this,"Your order has been confirmed..you can visit the pharmacy to complete order process" , LENGTH_LONG).show();
//                                finish();
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                progressDialog.dismiss();
//                                makeText(ViewOrderActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
//                            }
//                        });
//            }
//        });

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                progressDialog.setTitle("Finishing  Order");
                WriteBatch batch=firestore.batch();
                batch.update(firestore.collection("orders")
                        .document(order_id)
                        ,"status","Completed") ;
                for(Medicine m:medicines)
                {
                    if(m.isAvailable()) {
                        batch.update(firestore.collection("accounts")
                                .document(m.getPatient_id())
                                .collection("prescription")
                                .document(m.getId()),"current_quantity", m.getCurrent_quantity()+m.getQuantity(),
                                "status","Ordered");

                    }
                }
                batch.commit()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                makeText(ViewOrderActivity.this,"Order has been done successfully" , LENGTH_LONG).show();
                            finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                makeText(ViewOrderActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                            }
                        });
            }
        });


        progressDialog.show();
        firestore.collection("orders")
                .document(order_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Order order=documentSnapshot.toObject(Order.class);

                        if(order.getPatient_id().equals(firebaseAuth.getUid())) {
                            txt_title.setText(order.getPharmacy_name());
                            txt_view_file.setVisibility(View.GONE);
                        }
                        else {
                            txt_title.setText(order.getName());
                        }

                        txt_view_file.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ViewOrderActivity.this,PatientFileActivity. class);
                                intent.putExtra("patient_id",order.getPatient_id());
                                startActivity(intent);
                            }
                        });


                        if(firebaseAuth.getUid().equals(order.getPharmacy_id()))
                            if(order.getStatus().equals("Pending"))
                                btn_submit.setVisibility(View.VISIBLE);
//                        else if(order.getStatus().equals("Confirmed"))
//                                btn_finish.setVisibility(View.VISIBLE);
                        else if(order.getStatus().equals("Reviewed"))
                            btn_finish.setVisibility(View.VISIBLE);

                        if(order.getStatus().equals("Completed"))
                            btn_cancel.setVisibility(View.GONE);



                        firestore.collection("orders")
                                .document(order_id)
                                .collection("items")
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        progressDialog.dismiss();
                                          medicines=queryDocumentSnapshots.toObjects(Medicine.class);

                                          adapter=new PharmacyMedicinesOrderAdapter(medicines,ViewOrderActivity.this);
                                        recycler_medicines.setAdapter(adapter);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        makeText(ViewOrderActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                        finish();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                     progressDialog.dismiss();
                     makeText(ViewOrderActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                     finish();
                    }
                });

    }
}