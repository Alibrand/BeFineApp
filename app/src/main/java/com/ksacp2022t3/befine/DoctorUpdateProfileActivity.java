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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.ksacp2022t3.befine.models.Account;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoctorUpdateProfileActivity extends AppCompatActivity {
    EditText txt_speciality,txt_first_name,txt_last_name,
            txt_phone;
    AppCompatButton btn_save,btn_location,btn_change_password;
    ImageView btn_back;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    GeoPoint clinic_location =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_update_profile);
        txt_speciality = findViewById(R.id.txt_speciality);
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
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);



        loadInfo();

        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DoctorUpdateProfileActivity.this,ChangePasswordActivity. class);
                startActivity(intent);
            }
        });


        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DoctorUpdateProfileActivity.this,SelectLocationActivity.class);
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

                String str_txt_speciality = txt_speciality.getText().toString();
                String str_txt_first_name =txt_first_name.getText().toString();
                String str_txt_last_name =txt_last_name.getText().toString();
                String str_txt_phone =txt_phone.getText().toString();




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


                if(str_txt_speciality.isEmpty())
                {
                    txt_speciality.setError("Required Field");
                    return;
                }




                if(str_txt_phone.isEmpty())
                {
                    txt_phone.setError("Required Field");
                    return;
                }





                progressDialog.show();


                                firestore.collection("accounts").document(firebaseAuth.getUid()).
                                        update("first_name",str_txt_first_name,
                                                "last_name",str_txt_last_name,
                                                "phone",str_txt_phone,
                                                "speciality",str_txt_speciality,
                                                "location", clinic_location)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                SharedPreferences sharedPreferences=DoctorUpdateProfileActivity.this.getSharedPreferences("befine",MODE_PRIVATE);
                                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                                editor.putString("type","Doctor");
                                                editor.putString("full_name",str_txt_first_name+" "+str_txt_last_name);
                                                editor.putString("speciality",str_txt_speciality);
                                                editor.apply();


                                                progressDialog.dismiss();
                                                Toast.makeText(DoctorUpdateProfileActivity.this,"Account updated successfully" , Toast.LENGTH_LONG).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                makeText(DoctorUpdateProfileActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
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
                        clinic_location=account.getLocation();
                        txt_speciality.setText(account.getSpeciality());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(DoctorUpdateProfileActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
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
                makeText(DoctorUpdateProfileActivity.this,"Location set successfully", LENGTH_LONG).show();
                double lat=data.getDoubleExtra("lat",0.0);
                double lng=data.getDoubleExtra("lng",0.0);
                clinic_location =new GeoPoint(lat,lng);
            }
    }
}