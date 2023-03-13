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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.befine.adapters.SessionsAdapter;
import com.ksacp2022t3.befine.models.ClinicSession;

import java.util.ArrayList;
import java.util.List;

public class PatientHealthHistoryActivity extends AppCompatActivity {
    ImageView btn_back;
    RecyclerView recycler_sessions;
    FloatingActionButton btn_add_session;

    ProgressDialog progressDialog;
    FirebaseFirestore firestore;
    String patient_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_health_history);
        btn_back = findViewById(R.id.btn_back);
        recycler_sessions = findViewById(R.id.recycler_sessions);
        btn_add_session = findViewById(R.id.btn_add_session);

        patient_id=getIntent().getStringExtra("patient_id");
        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        SharedPreferences sharedPreferences=PatientHealthHistoryActivity.this.getSharedPreferences("befine",MODE_PRIVATE);
        String account_type=sharedPreferences.getString("type","");
        if(!account_type.equals("Doctor"))
            btn_add_session.setVisibility(View.GONE);



        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_add_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientHealthHistoryActivity.this,AddSessionActivity. class);
                intent.putExtra("patient_id",patient_id);
                startActivity(intent);
            }
        });



    }

    void load_sessions(){
        progressDialog.show();
        firestore.collection("accounts")
                .document(patient_id)
                .collection("health_history")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        List<ClinicSession> clinicSessionList=new ArrayList<>();
                        if(queryDocumentSnapshots.getDocuments().size()>0) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                ClinicSession clinicSession = doc.toObject(ClinicSession.class);
                                clinicSessionList.add(clinicSession);
                            }
                            SessionsAdapter adapter = new SessionsAdapter(clinicSessionList, PatientHealthHistoryActivity.this);
                            recycler_sessions.setAdapter(adapter);
                        }
                        else{
                            makeText(PatientHealthHistoryActivity.this,"No record was found in your health history" , LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(PatientHealthHistoryActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_sessions();
    }
}