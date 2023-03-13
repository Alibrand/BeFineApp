package com.ksacp2022t3.befine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class DoctorHomeActivity extends AppCompatActivity {
    ImageView btn_logout,btn_profile;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    AppCompatButton btn_find_patient,btn_my_inbox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);
        btn_my_inbox = findViewById(R.id.btn_my_inbox);
        btn_logout = findViewById(R.id.btn_logout);
        btn_find_patient = findViewById(R.id.btn_find_patient);
        btn_profile = findViewById(R.id.btn_profile);



        firebaseAuth= FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        btn_find_patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DoctorHomeActivity.this,FindPatientActivity. class);
                startActivity(intent);
            }
        });

        btn_my_inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DoctorHomeActivity.this,InboxActivity. class);
                startActivity(intent);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent intent = new Intent(DoctorHomeActivity.this,MainActivity. class);
                startActivity(intent);
                finish();
            }
        });

        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DoctorHomeActivity.this,DoctorUpdateProfileActivity. class);
                startActivity(intent);
            }
        });


    }

    private  void  check_new_messages(){
        btn_my_inbox.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_mail_24,0,0,0);
        firestore.collection("inbox")
                .whereArrayContains("users_ids",firebaseAuth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments()) {
                            doc.getReference()
                                    .collection("messages")
                                    .whereEqualTo("to",firebaseAuth.getUid())
                                    .whereEqualTo("status","unseen")
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            int new_messages_count= queryDocumentSnapshots.getDocuments().size();
                                            if(new_messages_count>0)
                                            {
                                                btn_my_inbox.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_mark_email_unread_24,0,0,0);
                                                Toast.makeText(DoctorHomeActivity.this, "You have new messages..Check your Inbox", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    })
                            ;
                        }
                    }
                });

    }


    @Override
    protected void onResume() {
        super.onResume();
        check_new_messages();
    }
}