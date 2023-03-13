package com.ksacp2022t3.befine;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.befine.models.Account;

public class FindPatientActivity extends AppCompatActivity {
    EditText txt_patient_n_id;
    ImageView btn_back;
    AppCompatButton btn_search;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_patient);
        txt_patient_n_id = findViewById(R.id.txt_patient_id);
        btn_back = findViewById(R.id.btn_back);
        btn_search = findViewById(R.id.btn_search);

        firestore=FirebaseFirestore.getInstance();


        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("Searching");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);


                btn_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });



        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String p_id= txt_patient_n_id.getText().toString();
                if(p_id.isEmpty())
                {
                    txt_patient_n_id.setError("Empty Field");
                    return;
                }


                find_patient(p_id);

            }
        });


    }

    void find_patient(String national_id)
    {
        progressDialog.show();
        firestore.collection("accounts")
                .whereEqualTo("national_id",national_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if(queryDocumentSnapshots.getDocuments().isEmpty())
                        {

                            makeText(FindPatientActivity.this,"No record was found for this ID." , LENGTH_LONG).show();
                        }
                        else{
                            DocumentSnapshot doc=queryDocumentSnapshots.getDocuments().get(0);
                            Account account=doc.toObject(Account.class);
                            if(!account.getStatus().equals("Active")) {
                                makeText(FindPatientActivity.this, "Account is not active..contact support", LENGTH_LONG).show();
                            }
                            else
                            {
                                Intent intent = new Intent(FindPatientActivity.this,PatientFileActivity. class);
                                intent.putExtra("patient_id",account.getId());
                                startActivity(intent);


                            }
                        }
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(FindPatientActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                    }
                });

    }


}