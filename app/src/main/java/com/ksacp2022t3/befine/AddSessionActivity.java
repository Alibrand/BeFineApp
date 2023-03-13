package com.ksacp2022t3.befine;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.befine.models.ClinicSession;

public class AddSessionActivity extends AppCompatActivity {
    EditText txt_symptoms,txt_diagnosis,txt_notes;
    AppCompatButton btn_save;
    ImageView btn_back;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);
        txt_symptoms = findViewById(R.id.txt_symptoms);
        txt_diagnosis = findViewById(R.id.txt_diagnosis);
        txt_notes = findViewById(R.id.txt_notes);
        btn_save = findViewById(R.id.btn_save);
        btn_back = findViewById(R.id.btn_back);

        String patient_id=getIntent().getStringExtra("patient_id");

        firebaseAuth =FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Saving");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_symptoms =txt_symptoms.getText().toString();
                String str_txt_diagnosis =txt_diagnosis.getText().toString();
                String str_txt_notes =txt_notes.getText().toString();

                if(str_txt_symptoms.isEmpty())
                {
                     txt_symptoms.setError("Required Field");
                     return;
                }
                 if(str_txt_diagnosis.isEmpty())
                 {
                      txt_diagnosis.setError("Required Field");
                      return;
                 }

                SharedPreferences sharedPreferences= AddSessionActivity.this.getSharedPreferences("befine",MODE_PRIVATE);
                String doctor_name=sharedPreferences.getString("full_name","doctor")+"-"+sharedPreferences.getString("speciality","speciality");

                ClinicSession clinicSession=new ClinicSession();

                clinicSession.setDoctor_name(doctor_name);
                clinicSession.setDoctor_uid(firebaseAuth.getUid());
                clinicSession.setNotes(str_txt_notes);
                clinicSession.setDiagnosis(str_txt_diagnosis);
                clinicSession.setPatient_id(patient_id);
                clinicSession.setSymptoms(str_txt_symptoms);

                progressDialog.show();
                DocumentReference new_doc=firestore.collection("accounts")
                        .document(patient_id)
                        .collection("health_history")
                        .document();
                clinicSession.setId(new_doc.getId());
                new_doc.set(clinicSession)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                makeText(AddSessionActivity.this,"Session added to list successfully" , LENGTH_LONG).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                makeText(AddSessionActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();

                            }
                        });
            }
        });

    }
}