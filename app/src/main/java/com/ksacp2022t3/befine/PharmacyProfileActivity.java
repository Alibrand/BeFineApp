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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.befine.models.Account;
import com.ksacp2022t3.befine.models.Order;

public class PharmacyProfileActivity extends AppCompatActivity {

    TextView txt_name,txt_phone;
    AppCompatButton btn_call,btn_chat,btn_order;
    ImageView btn_back;

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    String uid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_profile);
        txt_name = findViewById(R.id.txt_name);
        txt_phone = findViewById(R.id.txt_phone);
        btn_call = findViewById(R.id.btn_call);
        btn_chat = findViewById(R.id.btn_chat);
        btn_back = findViewById(R.id.btn_back);
        btn_order = findViewById(R.id.btn_order);

        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
         uid=getIntent().getStringExtra("uid");


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



        load_profile();


    }

    private void load_profile() {
        progressDialog.setTitle("Loading");
        progressDialog.show();

        firestore.collection("accounts")
                .document(uid)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        Account account=documentSnapshot.toObject(Account.class);

                        firestore.collection("orders")
                                .whereEqualTo("patient_id",firebaseAuth.getUid())
                                .whereEqualTo("pharmacy_id",account.getId())
                                .whereNotEqualTo("status","Completed")
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if(queryDocumentSnapshots.size()>0)
                                        {
                                            Order order=queryDocumentSnapshots.getDocuments().get(0).toObject(Order.class);


                                                btn_order.setText("Review my Order");
                                                btn_order.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        Intent intent = new Intent(PharmacyProfileActivity.this,OrderEditActivity.
                                                        class);
                                                        intent.putExtra("order_id",order.getId());
                                                        startActivity(intent);
                                                    }
                                                });




                                        }
                                        else{

                                            btn_order.setText("Order my Prescription");
                                            btn_order.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(PharmacyProfileActivity.this,OrderSelectActivity. class);
                                                    intent.putExtra("pharma_id",uid);
                                                    intent.putExtra("pharma_name",account.getPharmacy_name());
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                         makeText(PharmacyProfileActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                    }
                                });



                        txt_phone.setText(account.getPhone());
                        txt_name.setText(account.getPharmacy_name());
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
                                Intent intent = new Intent(PharmacyProfileActivity.this,ChatActivity. class);
                                intent.putExtra("receiver_id",uid);
                                intent.putExtra("receiver_name",account.getPharmacy_name());
                                intent.putExtra("receiver_type","Pharmacist");
                                startActivity(intent);
                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(PharmacyProfileActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_profile();
    }
}