package com.ksacp2022t3.befine;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.befine.models.Medicine;

public class AddMedicineActivity extends AppCompatActivity {
    EditText txt_name,txt_package_quantity,
            txt_regular_intake,txt_intake_unit, txt_dose_time,txt_intake_period,
    txt_notes;
    Switch sw_refillable;
    Spinner sp_pharmaceutical_form, sp_dose_time_unit,sp_period_unit;
    AppCompatButton btn_save;

    ImageView btn_back;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);
        txt_name = findViewById(R.id.txt_name);
        txt_package_quantity = findViewById(R.id.txt_package_quantity);
        txt_regular_intake = findViewById(R.id.txt_regular_intake);
        txt_intake_unit = findViewById(R.id.txt_intake_unit);
        txt_dose_time = findViewById(R.id.txt_intake_interval);
        sw_refillable = findViewById(R.id.sw_refillable);
        sp_pharmaceutical_form = findViewById(R.id.sp_pharmaceutical_form);
        sp_dose_time_unit = findViewById(R.id.sp_interval_unit);
        btn_save = findViewById(R.id.btn_save);
        btn_back = findViewById(R.id.btn_back);
        txt_notes = findViewById(R.id.txt_notes);
        sp_period_unit = findViewById(R.id.sp_period_unit);
        txt_intake_period = findViewById(R.id.txt_intake_period);



        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String patient_id=getIntent().getStringExtra("patient_id");


        ArrayAdapter adapter_forms=new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, Medicine.pharmaceutical_forms);
        sp_pharmaceutical_form.setAdapter(adapter_forms);
        ArrayAdapter adapter_interval_units=new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, Medicine.interval_units);
        sp_dose_time_unit.setAdapter(adapter_interval_units);
        ArrayAdapter adapter_period_units=new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, Medicine.period_units);
        sp_period_unit.setAdapter(adapter_period_units);


        firebaseAuth =FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("Saving");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);



        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_name =txt_name.getText().toString();
                String str_txt_package_quantity =txt_package_quantity.getText().toString();
                String str_txt_regular_intake =txt_regular_intake.getText().toString();
                String str_txt_intake_unit =txt_intake_unit.getText().toString();
                String str_txt_dose_time = txt_dose_time.getText().toString();
                String str_txt_notes =txt_notes.getText().toString();
                boolean refillable=sw_refillable.isChecked();
                String pharmaceutical_form=sp_pharmaceutical_form.getSelectedItem().toString();
                String dose_unit=sp_dose_time_unit.getSelectedItem().toString();
                String str_txt_intake_period =txt_intake_period.getText().toString();
                String period_unit=sp_period_unit.getSelectedItem().toString();


                 if(str_txt_name.isEmpty())
                 {
                      txt_name.setError("Required Field");
                      return;
                 }
                 if(str_txt_package_quantity.isEmpty())
                 {
                      txt_package_quantity.setError("Required Field");
                      return;
                 }
                 if(str_txt_regular_intake.isEmpty())
                 {
                      txt_regular_intake.setError("Required Field");
                      return;
                 }
                 if(str_txt_intake_unit.isEmpty())
                 {
                      txt_intake_unit.setError("Required Field");
                      return;
                 }
                 if(str_txt_dose_time.isEmpty())
                 {
                      txt_dose_time.setError("Required Field");
                      return;
                 }

                 if(str_txt_intake_period.isEmpty())
                 {
                      txt_intake_period.setError("Required Field");
                      return;
                 }


                SharedPreferences sharedPreferences= AddMedicineActivity.this.getSharedPreferences("befine",MODE_PRIVATE);
                 String doctor_name=sharedPreferences.getString("full_name","doctor")+"-"+sharedPreferences.getString("speciality","speciality");
                 Medicine medicine=new Medicine();
                 medicine.setDoctor_name(doctor_name);
                 medicine.setName(str_txt_name);
                 medicine.setDoctor_uid(firebaseAuth.getUid());
                 double all_quantity=Double.parseDouble(str_txt_package_quantity) ;
                 medicine.setQuantity(all_quantity);
                 double regular_intake=Double.parseDouble(str_txt_regular_intake) ;
                 medicine.setRegular_intake(regular_intake);
                 int dose=Integer.parseInt(str_txt_dose_time);
                 medicine.setDose_time(dose);
                 medicine.setInterval_unit(dose_unit);
                int period=Integer.parseInt(str_txt_intake_period);
                medicine.setPeriod(period);
                medicine.setPeriod_unit(period_unit);
                 medicine.setPharmaceutical_form(pharmaceutical_form);
                 medicine.setRefillable(refillable);
                 medicine.setNotes(str_txt_notes);
                 medicine.setIntake_unit(str_txt_intake_unit);
                 medicine.setPatient_id(patient_id);

                 progressDialog.show();
                 DocumentReference new_doc=firestore.collection("accounts")
                         .document(patient_id)
                         .collection("prescription")
                         .document();
                 medicine.setId(new_doc.getId());

                 new_doc.set(medicine)
                         .addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void unused) {
                                 progressDialog.dismiss();
                                 makeText(AddMedicineActivity.this,"Medicine added to list successfully" , LENGTH_LONG).show();
                            finish();
                             }
                         }).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 progressDialog.dismiss();
                                  makeText(AddMedicineActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();

                             }
                         });





            }
        });






    }
}