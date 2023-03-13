package com.ksacp2022t3.befine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.befine.models.Account;
import com.ksacp2022t3.befine.models.ClinicSession;
import com.ksacp2022t3.befine.models.Medicine;

import java.text.SimpleDateFormat;

public class SessionViewActivity extends AppCompatActivity {
    TextView txt_symptoms,txt_diagnosis,txt_notes,txt_date,
    txt_doctor_name
           ;
    ImageView btn_call,btn_back;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_view);
        txt_symptoms = findViewById(R.id.txt_symptoms);
        txt_diagnosis = findViewById(R.id.txt_diagnosis);
        txt_date = findViewById(R.id.txt_date);
        txt_doctor_name = findViewById(R.id.txt_doctor_name);

        txt_notes = findViewById(R.id.txt_notes);
        btn_call = findViewById(R.id.btn_call);
        btn_back = findViewById(R.id.btn_back);

        firestore=FirebaseFirestore.getInstance();


        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        String session_id=getIntent().getStringExtra("session_id") ;
        String patient_id=getIntent().getStringExtra("patient_id") ;

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        progressDialog.show();
        firestore.collection("accounts")
                .document(patient_id)
                .collection("health_history")
                .document(session_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ClinicSession clinicSession=documentSnapshot.toObject(ClinicSession.class);
                        txt_symptoms.setText(clinicSession.getSymptoms());
                        txt_diagnosis.setText(clinicSession.getDiagnosis());
                        txt_notes.setText(clinicSession.getNotes());
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                        txt_date.setText(simpleDateFormat.format(clinicSession.getCreated_at()));
                        txt_doctor_name.setText(clinicSession.getDoctor_name());
                        firestore.collection("accounts")
                                .document(clinicSession.getDoctor_uid())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        progressDialog.dismiss();
                                        Account account=documentSnapshot.toObject(Account.class);
                                        btn_call.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent=new Intent(Intent.ACTION_DIAL);
                                                intent.setData(Uri.parse("tel:"+account.getPhone()));
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SessionViewActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

    }
}