package com.ksacp2022t3.befine.adapters;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.befine.MedicineViewActivity;
import com.ksacp2022t3.befine.R;
import com.ksacp2022t3.befine.models.Medicine;
import com.ksacp2022t3.befine.services.AlarmHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MedicinesAdapter extends RecyclerView.Adapter<MedicineItem>{
    List<Medicine> medicineItems;
    Context context;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;


    public MedicinesAdapter(List<Medicine> medicineItems, Context context) {
        this.medicineItems = medicineItems;
        this.context = context;
        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(context);
                progressDialog.setTitle("Deleting");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public MedicineItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicine,parent,false);
        return new MedicineItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineItem holder, int position) {
            Medicine medicine= medicineItems.get(position);
            holder.txt_name.setText(medicine.getName());
          holder.txt_doctor.setText(medicine.getDoctor_name());
          holder.txt_form.setText(medicine.getPharmaceutical_form());
//          holder.txt_desc.setText("Intake:"+(int)medicine.getRegular_intake()+" "+medicine.getIntake_unit()+" Every "+medicine.getDose_time()+" "+medicine.getInterval_unit());

          SharedPreferences sharedPreferences=context.getSharedPreferences("befine",Context.MODE_PRIVATE);
        String accout_type=sharedPreferences.getString("type","");
       //this delete will only be shown for the doctor
        if(!accout_type.equals("Doctor"))
        {
            holder.btn_delete.setVisibility(View.GONE);
        }

        if(!medicine.getPatient_id().equals(firebaseAuth.getUid()))
        {
            holder.layout_patient_control.setVisibility(View.GONE);
        }


        holder.txt_current.setText(String.valueOf((int)medicine.getCurrent_quantity()));

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
//        if(medicine.getExpiry_date()!=null) {
//            holder.txt_period.setText(simpleDateFormat.format(medicine.getExpiry_date()));
//        }

        if(medicine.getStart_date()!=null) {
            holder.btn_update_start.setVisibility(View.GONE);
            holder.switch_alarm.setEnabled(true);
        }
        else
            holder.switch_alarm.setEnabled(false);

        if (medicine.getStart_date()!=null)
        {
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(medicine.getStart_date());
            if(medicine.getPeriod_unit().equals("Days"))
            {
                calendar.add(Calendar.DAY_OF_MONTH,medicine.getPeriod());
            }
            else if(medicine.getPeriod_unit().equals("Weeks"))
            {
                calendar.add(Calendar.WEEK_OF_YEAR,medicine.getPeriod());
            }
            else {
                calendar.add(Calendar.MONTH,medicine.getPeriod());
            }
            holder.txt_period.setText("from "+simpleDateFormat.format(medicine.getStart_date())+" to "+simpleDateFormat.format(calendar.getTime()));
        }

        holder.switch_alarm.setChecked(medicine.isReminder_active());
        if(medicine.isReminder_active())
            holder.switch_alarm.setText("ON");
        else
            holder.switch_alarm.setText("OFF");


        holder.btn_update_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Select Start Date");

                final DatePicker datePicker = new DatePicker(context);


                builder.setView(datePicker);


                datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());


                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Calendar calendar=Calendar.getInstance();
                        calendar.set(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth());

                        progressDialog.setTitle("Updating");
                        progressDialog.show();
                        firestore.collection("accounts")
                                .document(medicine.getPatient_id())
                                .collection("prescription")
                                .document(medicine.getId())
                                .update("start_date",calendar.getTime())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        medicine.setStart_date(calendar.getTime());
                                        MedicinesAdapter.this.notifyDataSetChanged();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                   progressDialog.dismiss();
                                   makeText(context,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                    }
                                });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });


        holder.switch_alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b==true)
                {
                  if(medicine.getCurrent_quantity()==0)
                  {
                      makeText(context,"Cannot set reminder for empty medicine" , LENGTH_LONG).show();
                      holder.switch_alarm.setChecked(false);
                      return;
                  }

                    Calendar calendar=Calendar.getInstance();
                    progressDialog.show();
                    progressDialog.setTitle("Setting Reminder Alarm");
                    firestore.collection("accounts")
                            .document(medicine.getPatient_id())
                            .collection("prescription")
                            .document(medicine.getId())
                            .update("reminder_active",true,"alarm_id",(int)calendar.getTimeInMillis())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    medicine.setReminder_active(true);
                                    medicine.setAlarm_id((int)calendar.getTimeInMillis());
                                    AlarmHelper alarmHelper=new AlarmHelper(context, medicine.getPatient_id(), medicine.getId(),medicine.getStart_date());
                                    alarmHelper.setAlarmForMedicine(medicine.getAlarm_id(),medicine.getDose_time() ,medicine.getInterval_unit());
                                    alarmHelper.setAutoAlarmCancel(medicine.getAlarm_id(),medicine.getPeriod() ,medicine.getPeriod_unit());

                                    MedicinesAdapter.this.notifyDataSetChanged();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    holder.switch_alarm.setChecked(false);
                                    progressDialog.dismiss();
                                    makeText(context,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                }
                            });


                }
                else
                {
                    progressDialog.show();
                    progressDialog.setTitle("Canceling Reminder Alarm");
                    firestore.collection("accounts")
                            .document(medicine.getPatient_id())
                            .collection("prescription")
                            .document(medicine.getId())
                            .update("reminder_active",false,"alarm_id",0)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    medicine.setReminder_active(false);
                                    AlarmHelper alarmHelper=new AlarmHelper(context, medicine.getPatient_id(), medicine.getId(),medicine.getStart_date());
                                    alarmHelper.cancelAlarm(medicine.getAlarm_id());
                                    MedicinesAdapter.this.notifyDataSetChanged();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    holder.switch_alarm.setChecked(false);
                                    progressDialog.dismiss();
                                    makeText(context,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                }
                            });

                }

            }
        });





        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                firestore.collection("accounts")
                        .document(medicine.getPatient_id())
                        .collection("prescription")
                        .document(medicine.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                medicineItems.remove(medicine);
                                MedicinesAdapter.this.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                 makeText(context,"Error :"+e.getMessage() , LENGTH_LONG).show();
                            }
                        });
            }
        });

        holder.medicine_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MedicineViewActivity. class);
                intent.putExtra("medicine_id",medicine.getId());
                intent.putExtra("patient_id",medicine.getPatient_id());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return medicineItems.size();
    }
}

class MedicineItem extends RecyclerView.ViewHolder
{

    TextView txt_name,txt_form,txt_doctor,txt_current, txt_period;
    ImageView btn_delete;
    CardView medicine_card;
    LinearLayoutCompat layout_patient_control;
    Switch switch_alarm;
    AppCompatButton btn_update_start;

    public MedicineItem(@NonNull View itemView) {
        super(itemView);
        txt_form=itemView.findViewById(R.id.txt_form);
        txt_name=itemView.findViewById(R.id.txt_name);
        txt_current=itemView.findViewById(R.id.txt_current);
        txt_period =itemView.findViewById(R.id.txt_start_date);
        layout_patient_control=itemView.findViewById(R.id.layout_patient_control);
        switch_alarm=itemView.findViewById(R.id.switch_alarm);
        btn_update_start =itemView.findViewById(R.id.btn_update_expiry);
        txt_doctor=itemView.findViewById(R.id.txt_doctor);
        btn_delete = itemView.findViewById(R.id.btn_delete);
        medicine_card = itemView.findViewById(R.id.medicine_card);
    }
}

