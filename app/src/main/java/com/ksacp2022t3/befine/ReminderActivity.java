package com.ksacp2022t3.befine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.befine.models.Medicine;
import com.ksacp2022t3.befine.services.AlarmHelper;

public class ReminderActivity extends AppCompatActivity {

    TextView txt_name,txt_intake;
    AppCompatButton btn_taken,btn_ignore;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        txt_name = findViewById(R.id.txt_name);
        txt_intake = findViewById(R.id.txt_intake);
        btn_taken = findViewById(R.id.btn_taken);
        btn_ignore = findViewById(R.id.btn_ignore);


        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Updating");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        if(isNotificationVisible())
        {
            NotificationManager notificationManager =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(4);
        }


        String name=getIntent().getStringExtra("name");
        double intake=getIntent().getDoubleExtra("intake",0);
        String unit=getIntent().getStringExtra("unit");
        String patient_id=getIntent().getStringExtra("patient_id");
        String medicine_id=getIntent().getStringExtra("medicine_id");

        txt_name.setText("of "+name);
        txt_intake.setText((int)intake+" "+unit);


        btn_taken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                firestore.collection("accounts")
                        .document(patient_id)
                        .collection("prescription")
                        .document(medicine_id)
                        .update("current_quantity", FieldValue.increment(-intake))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                firestore.collection("accounts")
                                        .document(patient_id)
                                        .collection("prescription")
                                        .document(medicine_id)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                Medicine medicine=documentSnapshot.toObject(Medicine.class);
                                                if(medicine.getCurrent_quantity()==0)
                                                {
                                                    AlarmHelper alarmHelper=new AlarmHelper(ReminderActivity.this,patient_id,medicine_id,medicine.getStart_date());
                                                    alarmHelper.cancelAlarm(medicine.getAlarm_id());
                                                    firestore.collection("accounts")
                                                            .document(patient_id)
                                                            .collection("prescription")
                                                            .document(medicine_id)
                                                            .update("reminder_active",false);
                                                }
                                                progressDialog.dismiss();
                                                Toast.makeText(ReminderActivity.this,"Medicine intake updated successfully" , Toast.LENGTH_LONG).show();
                                                finish();
                                            }
                                        }) ;



                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(ReminderActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
        
        btn_ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ReminderActivity.this)
                        .setTitle("Warning")
                        .setMessage("Ignoring your medicines would affect your health negatively.The system will continue to remind you at the exact time of every intake")
                        .setPositiveButton("Ignore", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).setNegativeButton("Return",null)
                        .show();
            }
        });


    }

    private boolean isNotificationVisible () {
        NotificationManager mNotificationManager =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        StatusBarNotification[] notifications =
                mNotificationManager.getActiveNotifications();
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == 4) {
                return true;
            }
        }
        return  false;
    }


    
     
}