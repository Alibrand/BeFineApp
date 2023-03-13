package com.ksacp2022t3.befine;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.befine.adapters.MedicinesAdapter;
import com.ksacp2022t3.befine.models.Medicine;

import java.util.ArrayList;
import java.util.List;

public class MyPrescriptionActivity extends AppCompatActivity {
    ImageView btn_back;
    RecyclerView recycler_medicines;
    FloatingActionButton btn_add_medicine;

    ProgressDialog progressDialog;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    String patient_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_prescription);
        btn_back = findViewById(R.id.btn_back);
        recycler_medicines = findViewById(R.id.recycler_medicines);
        btn_add_medicine = findViewById(R.id.btn_add_medicine);


        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        patient_id=firebaseAuth.getUid();

        SharedPreferences sharedPreferences=MyPrescriptionActivity.this.getSharedPreferences("befine",MODE_PRIVATE);
        String account_type=sharedPreferences.getString("type","");
        if(!account_type.equals("Doctor"))
            btn_add_medicine.setVisibility(View.GONE);



        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_add_medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPrescriptionActivity.this,AddMedicineActivity. class);
                intent.putExtra("patient_id",patient_id);
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
                            MedicinesAdapter adapter = new MedicinesAdapter(medicineList, MyPrescriptionActivity.this);
                            recycler_medicines.setAdapter(adapter);
                        }
                        else{
                            makeText(MyPrescriptionActivity.this,"No record was found in the prescription" , LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(MyPrescriptionActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_prescription();
    }
}