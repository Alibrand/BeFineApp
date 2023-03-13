package com.ksacp2022t3.befine;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.befine.adapters.MedicinesAdapter;
import com.ksacp2022t3.befine.models.Medicine;

import java.util.ArrayList;
import java.util.List;

public class PatientPrescriptionActivity extends AppCompatActivity {
    ImageView btn_back;
    RecyclerView recycler_medicines;
    FloatingActionButton btn_add_medicine;
    AppCompatButton btn_instant_order;
    ProgressDialog progressDialog;
    FirebaseFirestore firestore;
    String patient_id;
    String patient_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_prescription);
        btn_back = findViewById(R.id.btn_back);
        recycler_medicines = findViewById(R.id.recycler_medicines);
        btn_add_medicine = findViewById(R.id.btn_add_medicine);
        btn_instant_order = findViewById(R.id.btn_instant_order);

        patient_id=getIntent().getStringExtra("patient_id");
        patient_name=getIntent().getStringExtra("patient_name");

        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("Loading");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);


        SharedPreferences sharedPreferences=PatientPrescriptionActivity.this.getSharedPreferences("befine",MODE_PRIVATE);
        String account_type=sharedPreferences.getString("type","");
        if(!account_type.equals("Doctor"))
            btn_add_medicine.setVisibility(View.GONE);
        if(!account_type.equals("Pharmacy"))
        {
            btn_instant_order.setVisibility(View.GONE);
        }



        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_add_medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientPrescriptionActivity.this,AddMedicineActivity. class);
                intent.putExtra("patient_id",patient_id);
                startActivity(intent);
            }
        });


        btn_instant_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientPrescriptionActivity.this,OrderSelectActivity. class);
                intent.putExtra("patient_id",patient_id);
                intent.putExtra("patient_name",patient_name);
                startActivity(intent);
            }
        });



    }

    void load_prescription(){
        progressDialog.show();
        firestore.collection("accounts")
                .document(patient_id)
                .collection("prescription")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        List<Medicine> medicineList=new ArrayList<>();
                        if(queryDocumentSnapshots.getDocuments().size()>0) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                Medicine medicine = doc.toObject(Medicine.class);
                                medicineList.add(medicine);
                            }
                            MedicinesAdapter adapter = new MedicinesAdapter(medicineList, PatientPrescriptionActivity.this);
                            recycler_medicines.setAdapter(adapter);
                        }
                        else{
                            makeText(PatientPrescriptionActivity.this,"No record was found in the prescription" , LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(PatientPrescriptionActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_prescription();
    }
}