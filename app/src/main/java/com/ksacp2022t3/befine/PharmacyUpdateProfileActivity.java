package com.ksacp2022t3.befine;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.ksacp2022t3.befine.models.Account;

import java.util.Arrays;

public class PharmacyUpdateProfileActivity extends AppCompatActivity {
    EditText txt_pharmacy_name,txt_first_name,txt_last_name,
            txt_phone;
    AppCompatButton  btn_save,btn_location,btn_change_password;
    ImageView btn_back;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    GeoPoint pharmacy_location=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_update_profile);
        txt_pharmacy_name = findViewById(R.id.txt_pharmacy_name);
        btn_location = findViewById(R.id.btn_location);
        txt_first_name = findViewById(R.id.txt_first_name);
        txt_last_name = findViewById(R.id.txt_last_name);
        txt_phone = findViewById(R.id.txt_phone);
        btn_save = findViewById(R.id.btn_save);
        btn_back = findViewById(R.id.btn_back);
        btn_change_password = findViewById(R.id.btn_change_password);



        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading Account");
        progressDialog.setMessage("Please wait");


        loadInfo();

        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PharmacyUpdateProfileActivity.this,ChangePasswordActivity. class);
                startActivity(intent);
            }
        });

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PharmacyUpdateProfileActivity.this,SelectLocationActivity.class);
                startActivityForResult(intent,110);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String str_txt_pharmacy_name =txt_pharmacy_name.getText().toString();
                String str_txt_first_name =txt_first_name.getText().toString();
                String str_txt_last_name =txt_last_name.getText().toString();
                String str_txt_phone =txt_phone.getText().toString();



                if(str_txt_pharmacy_name.isEmpty())
                {
                    txt_pharmacy_name.setError("Required Field");
                    return;
                }
                if(str_txt_first_name.isEmpty())
                {
                    txt_first_name.setError("Required Field");
                    return;
                }
                if(str_txt_last_name.isEmpty())
                {
                    txt_last_name.setError("Required Field");
                    return;
                }


                if(str_txt_phone.isEmpty())
                {
                    txt_phone.setError("Required Field");
                    return;
                }





                progressDialog.show();


                                firestore.collection("accounts").document(firebaseAuth.getUid())
                                        .update("pharmacy_name",str_txt_pharmacy_name,
                                                "first_name",str_txt_first_name,
                                                "last_name",str_txt_last_name,
                                                "phone",str_txt_phone,
                                                "location",pharmacy_location)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                SharedPreferences sharedPreferences=PharmacyUpdateProfileActivity.this.getSharedPreferences("befine",MODE_PRIVATE);
                                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                                editor.putString("type","Pharmacist");
                                                editor.putString("full_name",str_txt_first_name+" "+str_txt_last_name);
                                                editor.putString("pharmacy_name",str_txt_pharmacy_name);
                                                editor.apply();

                                                progressDialog.dismiss();
                                                Toast.makeText(PharmacyUpdateProfileActivity.this,"Account updated successfully" , Toast.LENGTH_LONG).show();


                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                makeText(PharmacyUpdateProfileActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                                progressDialog.dismiss();
                                            }
                                        });








            }
        });


    }

    void loadInfo(){
        progressDialog.show();
        firestore.collection("accounts")
                .document(firebaseAuth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        Account account=documentSnapshot.toObject(Account.class);
                        txt_first_name.setText(account.getFirst_name());
                        txt_phone.setText(account.getPhone());
                        txt_last_name.setText(account.getLast_name());
                        pharmacy_location=account.getLocation();
                        txt_pharmacy_name.setText(account.getPharmacy_name());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(PharmacyUpdateProfileActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                        finish();
                    }
                });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==110)
            if(resultCode==RESULT_OK)
            {
                makeText(PharmacyUpdateProfileActivity.this,"Location set successfully", LENGTH_LONG).show();
                double lat=data.getDoubleExtra("lat",0.0);
                double lng=data.getDoubleExtra("lng",0.0);
                pharmacy_location=new GeoPoint(lat,lng);
            }
    }
}