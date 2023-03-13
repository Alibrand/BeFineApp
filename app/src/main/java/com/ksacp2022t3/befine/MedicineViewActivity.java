package com.ksacp2022t3.befine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.ksacp2022t3.befine.models.Medicine;

public class MedicineViewActivity extends AppCompatActivity {

    TextView txt_name,sp_pharmaceutical_form,txt_package_quantity,
            sw_refillable,txt_regular_intake,txt_notes,txt_doctor_name;
    ImageView btn_call,btn_back;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_view);
        txt_name = findViewById(R.id.txt_name);
        sp_pharmaceutical_form = findViewById(R.id.sp_pharmaceutical_form);
        txt_package_quantity = findViewById(R.id.txt_package_quantity);
        sw_refillable = findViewById(R.id.sw_refillable);
        txt_regular_intake = findViewById(R.id.txt_regular_intake);
        txt_notes = findViewById(R.id.txt_notes);
        txt_doctor_name = findViewById(R.id.txt_doctor_name);
        btn_call = findViewById(R.id.btn_call);
        btn_back = findViewById(R.id.btn_back);





        firestore=FirebaseFirestore.getInstance();

        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("Loading");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);


         String medicine_id=getIntent().getStringExtra("medicine_id") ;
        String patient_id=getIntent().getStringExtra("patient_id") ;

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        progressDialog.show();
        firestore.collection("accounts")
                .document(patient_id)
                .collection("prescription")
                .document(medicine_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Medicine medicine=documentSnapshot.toObject(Medicine.class);
                        txt_name.setText(medicine.getName());
                        sp_pharmaceutical_form.setText(medicine.getPharmaceutical_form());
                        txt_package_quantity.setText(String.valueOf( medicine.getQuantity()));
                         txt_notes.setText(medicine.getNotes());
                         String intake="Regular Intake : "+(int)medicine.getRegular_intake()+" "+medicine.getIntake_unit()+
                                 " every "+medicine.getDose_time()+ " "+medicine.getInterval_unit()+" for "
                                 +medicine.getPeriod()+" "+medicine.getPeriod_unit();
                         txt_regular_intake.setText(intake);
                         sw_refillable.setText(medicine.isRefillable()?"Refillable":"");
                        txt_doctor_name.setText(medicine.getDoctor_name());
                         firestore.collection("accounts")
                                 .document(medicine.getDoctor_uid())
                                 .get()
                                 .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                     @Override
                                     public void onSuccess(DocumentSnapshot documentSnapshot) {
                                         progressDialog.dismiss();
                                         Account account=documentSnapshot.toObject(Account.class);
                                         btn_call.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                 Intent intent=new Intent(Intent.ACTION_DIAL);
                                                 intent.setData(Uri.parse("tel:"+account.getPhone()));
                                                 startActivity(intent);
                                             }
                                         });
                                     }
                                 });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                         Toast.makeText(MedicineViewActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                         finish();
                    }
                });

    }
}