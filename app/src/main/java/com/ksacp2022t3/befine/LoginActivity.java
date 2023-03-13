package com.ksacp2022t3.befine;

import static android.widget.Toast.*;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.ksacp2022t3.befine.models.Account;

import org.checkerframework.common.initializedfields.qual.EnsuresInitializedFields;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    ImageView btn_back;
    AppCompatButton btn_login;
    TextView txt_forgot_password;
    EditText txt_email,txt_password;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txt_email = findViewById(R.id.txt_email);
        txt_password = findViewById(R.id.txt_password);
        btn_back = findViewById(R.id.btn_back);
        btn_login = findViewById(R.id.btn_login);
        txt_forgot_password = findViewById(R.id.txt_forgot_password);


        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Logging In");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);




        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_email =txt_email.getText().toString();
                String str_txt_password =txt_password.getText().toString();

                if(str_txt_email.isEmpty())
                {
                     txt_email.setError("Required Field");
                     return;
                }
                if(str_txt_password.isEmpty())
                {
                    txt_password.setError("Required Field");
                     return;
                }

                progressDialog.show();

                firebaseAuth.signInWithEmailAndPassword(str_txt_email,str_txt_password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                SharedPreferences preferences= LoginActivity.this.getSharedPreferences("befine",MODE_PRIVATE);
                                SharedPreferences.Editor editor=preferences.edit();

                                FirebaseUser user= authResult.getUser();
                                if(user.getEmail().equals("admin@befine.com"))
                                {
                                    editor.putString("full_name","Admin");
                                    editor.putString("type","admin");
                                    editor.apply();
                                    Intent intent = new Intent(LoginActivity.this,AdminHomeActivity. class);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                firestore.collection("accounts")
                                        .document(user.getUid())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                Account account=documentSnapshot.toObject(Account.class);

                                                editor.putString("type",account.getType());
                                                editor.putString("full_name",account.getFirst_name()+" "+account.getLast_name());
                                                editor.putString("speciality",account.getSpeciality());
                                                editor.putString("pharmacy_name",account.getPharmacy_name());
                                                editor.apply();


                                                Intent intent;


                                                if(account.getType().equals("Patient"))
                                                {
                                                    //check account status
                                                    if(account.getStatus().equals("Pending"))
                                                    {
                                                        intent = new Intent(LoginActivity.this, MessagePendingActivity.
                                                                class);
                                                    }
                                                    else if(account.getStatus().equals("Rejected"))
                                                    {
                                                        intent = new Intent(LoginActivity.this, MessageRejectedActivity.
                                                                class);
                                                        intent.putExtra("notes",account.getNotes());
                                                    }
                                                    else {
                                                        intent = new Intent(LoginActivity.this, PatientHomeActivity.
                                                                class);
                                                        finish();
                                                    }
                                                }
                                                else if (account.getType().equals("Pharmacy")){
                                                    intent = new Intent(LoginActivity.this, PharmacistHomeActivity.
                                                            class);
                                                    finish();
                                                }
                                                else{
                                                    Log.d("beee","ddddd");
                                                    intent = new Intent(LoginActivity.this, DoctorHomeActivity.
                                                            class);
                                                    finish();
                                                }
                                                startActivity(intent);
                                                progressDialog.dismiss();



                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                 makeText(LoginActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                            }
                                        });

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                 progressDialog.dismiss();
                                makeText(LoginActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                            }

                        });





            }
        });

        txt_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,ForgotPasswordActivity. class);
                startActivity(intent);
            }
        });

    }


}