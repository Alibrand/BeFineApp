package com.ksacp2022t3.befine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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

public class DoctorProfileActivity extends AppCompatActivity {

    TextView txt_name,txt_speciality,txt_phone;
    AppCompatButton btn_call,btn_chat;
    ImageView btn_back;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);
        txt_name = findViewById(R.id.txt_name);
        txt_speciality = findViewById(R.id.txt_speciality);
        txt_phone = findViewById(R.id.txt_phone);
        btn_call = findViewById(R.id.btn_call);
        btn_chat = findViewById(R.id.btn_chat);
        btn_back = findViewById(R.id.btn_back);

        firestore=FirebaseFirestore.getInstance();
        String uid=getIntent().getStringExtra("uid");


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
                .document(uid)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        Account account=documentSnapshot.toObject(Account.class);
                        txt_phone.setText(account.getPhone());
                        txt_name.setText(account.getFirst_name()+" "+account.getLast_name());
                        txt_speciality.setText(account.getSpeciality());
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
                                Intent intent = new Intent(DoctorProfileActivity.this,ChatActivity. class);
                                intent.putExtra("receiver_id",uid);
                                intent.putExtra("receiver_name",account.getFirst_name()+" "+account.getLast_name());
                                intent.putExtra("receiver_type","Doctor");
                                startActivity(intent);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                         Toast.makeText(DoctorProfileActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                    }
                });
        }



}