package com.ksacp2022t3.befine;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.befine.models.Account;

public class PatientFileActivity extends AppCompatActivity {
    TextView txt_national_id,txt_first_name,txt_last_name,
            txt_relative_relation,
            txt_phone,txt_title,txt_chronic_diseases,txt_age;
    AppCompatButton btn_call,btn_prescription,btn_health_history,btn_chat;
    ImageView btn_back;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_file);
        txt_national_id = findViewById(R.id.txt_national_id);
        txt_first_name = findViewById(R.id.txt_first_name);
        txt_last_name = findViewById(R.id.txt_last_name);
        txt_phone = findViewById(R.id.txt_phone);
        txt_relative_relation = findViewById(R.id.txt_relative_relation);
        btn_prescription = findViewById(R.id.btn_prescription);
        btn_call = findViewById(R.id.btn_call);
        btn_back = findViewById(R.id.btn_back);
        txt_title = findViewById(R.id.txt_title);
        btn_health_history = findViewById(R.id.btn_health_history);
        btn_chat = findViewById(R.id.btn_chat);
        txt_chronic_diseases = findViewById(R.id.txt_chronic_diseases);
        txt_age = findViewById(R.id.txt_age);


        String patient_id=getIntent().getStringExtra("patient_id");

        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading profile");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });





        btn_health_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientFileActivity.this,PatientHealthHistoryActivity. class);
                intent.putExtra("patient_id",patient_id);
                startActivity(intent);
            }
        });



        //load info from fire store
        progressDialog.show();

        firestore.collection("accounts")
                .document(patient_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        Account account=documentSnapshot.toObject(Account.class);
                        txt_national_id.setText(account.getNational_id());
                        txt_first_name.setText(account.getFirst_name());
                        txt_last_name.setText(account.getLast_name());
                        txt_phone.setText(account.getPhone());
                        txt_relative_relation.setText(account.getRelative_relation());
                        txt_age.setText(account.getAge());
                        if(account.getChronic_diseases()!=null) {
                            txt_chronic_diseases.setText(account.getChronic_diseases());
                        } else {
                            txt_chronic_diseases.setText(" None");
                        }

                        btn_call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent=new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+account.getPhone()));
                                startActivity(intent);
                            }
                        });

                        btn_chat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(PatientFileActivity.this,ChatActivity. class);
                                intent.putExtra("receiver_id",patient_id);
                                intent.putExtra("receiver_name",account.getFirst_name()+" "+account.getLast_name());
                                intent.putExtra("receiver_type","Patient");
                                startActivity(intent);
                            }
                        });

                        btn_prescription.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(PatientFileActivity.this,PatientPrescriptionActivity. class);
                                intent.putExtra("patient_id",patient_id);
                                intent.putExtra("patient_name",account.getFirst_name()+" "+account.getLast_name());
                                startActivity(intent);
                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(PatientFileActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                        finish();
                    }
                });

    }
}