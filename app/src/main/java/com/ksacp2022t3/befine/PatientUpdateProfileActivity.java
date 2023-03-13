package com.ksacp2022t3.befine;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.befine.models.Account;

import java.util.Arrays;

public class PatientUpdateProfileActivity extends AppCompatActivity {
    EditText txt_first_name,txt_last_name,
            txt_phone,txt_age,txt_chronic_diseases;
    Spinner sp_relative_relation;
    AppCompatButton btn_save,btn_change_password;

    String [] relations=new String[]{"My Self","Son","Daughter","Mother","Father","Sibling"};

    ImageView btn_back;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_update_profile);
        txt_first_name = findViewById(R.id.txt_first_name);
        txt_last_name = findViewById(R.id.txt_last_name);
         txt_phone = findViewById(R.id.txt_phone);
        sp_relative_relation = findViewById(R.id.sp_relative_relation);
        btn_save = findViewById(R.id.btn_save);
        btn_back = findViewById(R.id.btn_back);
        txt_age = findViewById(R.id.txt_age);
        txt_chronic_diseases = findViewById(R.id.txt_chronic_diseases);
        btn_change_password = findViewById(R.id.btn_change_password);
        

        ArrayAdapter adapter=new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,relations);
        sp_relative_relation.setAdapter(adapter);


        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading Account");
        progressDialog.setMessage("Please wait");

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loadInfo();

        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientUpdateProfileActivity.this,ChangePasswordActivity. class);
                startActivity(intent);
            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_first_name =txt_first_name.getText().toString();
                String str_txt_last_name =txt_last_name.getText().toString();
                String str_txt_phone =txt_phone.getText().toString();
                String str_txt_relative_relation =sp_relative_relation.getSelectedItem().toString();
                String str_txt_age =txt_age.getText().toString();
                String str_txt_chronic_diseases =txt_chronic_diseases.getText().toString();




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
                if(str_txt_age.isEmpty())
                {
                    txt_age.setError("Required Field");
                    return;
                }
                if(Integer.parseInt(str_txt_age)<=0 || Integer.parseInt(str_txt_age)>150)
                {
                    txt_age.setError("Please enter a valid age");
                    return;
                }



                if(str_txt_phone.isEmpty())
                {
                    txt_phone.setError("Required Field");
                    return;
                }




                progressDialog.show();
                progressDialog.setTitle("Updating");


                                firestore.collection("accounts").document(firebaseAuth.getUid())
                                        .update("first_name",str_txt_first_name,
                                                "last_name",str_txt_last_name,
                                                "phone",str_txt_phone,
                                                "relative_relation",str_txt_relative_relation,
                                                "age",str_txt_age,
                                                "chronic_diseases",str_txt_chronic_diseases)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                SharedPreferences sharedPreferences=PatientUpdateProfileActivity.this.getSharedPreferences("befine",MODE_PRIVATE);
                                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                                editor.putString("full_name",str_txt_first_name+" "+str_txt_last_name);
                                                editor.apply();
                                                makeText(PatientUpdateProfileActivity.this,"Account Update successfully" , LENGTH_LONG).show();
                                                progressDialog.dismiss();


                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                makeText(PatientUpdateProfileActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
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
                        int selected_relation= Arrays.asList(relations).indexOf(account.getRelative_relation());
                        sp_relative_relation.setSelection(selected_relation);
                        txt_age.setText(account.getAge());
                        txt_chronic_diseases.setText(account.getChronic_diseases());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(PatientUpdateProfileActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                    finish();
                    }
                });
    }
}