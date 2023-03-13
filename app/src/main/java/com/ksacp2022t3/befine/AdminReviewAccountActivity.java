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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.befine.models.Account;

import java.nio.channels.AcceptPendingException;

public class AdminReviewAccountActivity extends AppCompatActivity {
    TextView txt_national_id,txt_first_name,txt_last_name,
            txt_relative_relation,
            txt_phone,txt_title;
    EditText txt_notes;
    ImageView btn_back;
    AppCompatButton btn_approve,btn_reject,btn_call;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_review_account);
        txt_national_id = findViewById(R.id.txt_national_id);
        txt_first_name = findViewById(R.id.txt_first_name);
        txt_last_name = findViewById(R.id.txt_last_name);
        txt_phone = findViewById(R.id.txt_phone);
        txt_relative_relation = findViewById(R.id.txt_relative_relation);
        btn_approve = findViewById(R.id.btn_approve);
        btn_reject = findViewById(R.id.btn_reject);
        txt_notes = findViewById(R.id.txt_notes);
        btn_call = findViewById(R.id.btn_call);
        txt_title = findViewById(R.id.txt_title);
        btn_back = findViewById(R.id.btn_back);





        String user_id=getIntent().getStringExtra("user_id");


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

        //load info from fire store
        progressDialog.show();

        firestore.collection("accounts")
                .document(user_id)
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
                        txt_notes.setText(account.getNotes());
                        txt_title.setText(account.getFirst_name()+" "+account.getLast_name());
                        btn_call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent=new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+account.getPhone()));
                                startActivity(intent);
                            }
                        });
                        if(account.getStatus().equals("Active"))
                            btn_approve.setVisibility(View.GONE);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                         makeText(AdminReviewAccountActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                    }
                });


        btn_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setTitle("Approving");
                progressDialog.show();
                firestore.collection("accounts")
                        .document(user_id)
                        .update("status","Active")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                makeText(AdminReviewAccountActivity.this,"Account approved successfully" , LENGTH_LONG).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                makeText(AdminReviewAccountActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                            }
                        });
            }
        });

        btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setTitle("Rejecting");
                progressDialog.show();
                String notes=txt_notes.getText().toString();
                firestore.collection("accounts")
                        .document(user_id)
                        .update("status","Rejected","notes",notes)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                makeText(AdminReviewAccountActivity.this,"Account rejected successfully" , LENGTH_LONG).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                makeText(AdminReviewAccountActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                            }
                        });
            }
        });




    }
}