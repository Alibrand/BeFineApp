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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.befine.models.ClinicSession;

public class EditSessionActivity extends AppCompatActivity {
    EditText txt_symptoms, txt_diagnosis, txt_notes;
    AppCompatButton btn_save;
    ImageView btn_back;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_session);
        txt_symptoms = findViewById(R.id.txt_symptoms);
        txt_diagnosis = findViewById(R.id.txt_diagnosis);
        txt_notes = findViewById(R.id.txt_notes);
        btn_save = findViewById(R.id.btn_save);
        btn_back = findViewById(R.id.btn_back);

        String patient_id = getIntent().getStringExtra("patient_id");
        String session_id = getIntent().getStringExtra("session_id");

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
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

        //load session info
        progressDialog.show();
        firestore.collection("accounts")
                .document(patient_id)
                .collection("health_history")
                .document(session_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        ClinicSession session = documentSnapshot.toObject(ClinicSession.class);
                        txt_symptoms.setText(session.getSymptoms());
                        txt_diagnosis.setText(session.getDiagnosis());
                        txt_notes.setText(session.getNotes());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(EditSessionActivity.this, "Error :" + e.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_symptoms = txt_symptoms.getText().toString();
                String str_txt_diagnosis = txt_diagnosis.getText().toString();
                String str_txt_notes = txt_notes.getText().toString();

                if (str_txt_symptoms.isEmpty()) {
                    txt_symptoms.setError("Required Field");
                    return;
                }
                if (str_txt_diagnosis.isEmpty()) {
                    txt_diagnosis.setError("Required Field");
                    return;
                }

                progressDialog.setTitle("Saving");
                progressDialog.show();
                firestore.collection("accounts")
                        .document(patient_id)
                        .collection("health_history")
                        .document(session_id).update("diagnosis", str_txt_diagnosis,
                                "symptoms", str_txt_symptoms,
                                "notes", str_txt_notes)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                makeText(EditSessionActivity.this, "changes saved successfully", LENGTH_LONG).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                makeText(EditSessionActivity.this, "Error :" + e.getMessage(), LENGTH_LONG).show();

                            }
                        });
            }
        });

    }


}