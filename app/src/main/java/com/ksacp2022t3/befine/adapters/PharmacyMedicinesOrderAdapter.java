package com.ksacp2022t3.befine.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.ksacp2022t3.befine.MedicineViewActivity;
import com.ksacp2022t3.befine.R;
import com.ksacp2022t3.befine.models.Medicine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PharmacyMedicinesOrderAdapter extends RecyclerView.Adapter<PharmacyMedicineOrderItem>{
     public List<Medicine> medicineItems;
    Context context;
    FirebaseAuth firebaseAuth;





    public PharmacyMedicinesOrderAdapter(List<Medicine> medicineItems, Context context) {
        this.medicineItems = medicineItems;
        this.context = context;
        firebaseAuth=FirebaseAuth.getInstance();


    }

    @NonNull
    @Override
    public PharmacyMedicineOrderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_medicine,parent,false);
        return new PharmacyMedicineOrderItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PharmacyMedicineOrderItem holder, int position) {
            Medicine medicine= medicineItems.get(position);
            holder.txt_name.setText(medicine.getName());
          holder.txt_doctor.setText(medicine.getDoctor_name());
          holder.txt_form.setText(medicine.getPharmaceutical_form());

          if(medicine.isAvailable())
          {
              holder.btn_yes.setChecked(true);
          }
          else
              holder.btn_no.setChecked(true);

          if(firebaseAuth.getUid().equals(medicine.getPatient_id())||firebaseAuth.getCurrentUser().getEmail().equals("admin@befine.com"))
          {
              holder.btn_no.setClickable(false);
              holder.btn_yes.setClickable(false);
          }

          holder.txt_current.setText(String.valueOf((int)medicine.getCurrent_quantity()));





          holder.group_available.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
              @Override
              public void onCheckedChanged(RadioGroup radioGroup, int i) {
                  if(holder.group_available.getCheckedRadioButtonId()==holder.btn_yes.getId())
                      medicine.setAvailable(true);
                  else
                      medicine.setAvailable(false);




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

class PharmacyMedicineOrderItem extends RecyclerView.ViewHolder
{

    TextView txt_name,txt_form,txt_doctor,txt_current;
    RadioGroup group_available;
    RadioButton btn_yes,btn_no;
    CardView medicine_card;

    public PharmacyMedicineOrderItem(@NonNull View itemView) {
        super(itemView);
        txt_form=itemView.findViewById(R.id.txt_form);
        txt_name=itemView.findViewById(R.id.txt_name);
       // txt_desc=itemView.findViewById(R.id.txt_desc);
        txt_doctor=itemView.findViewById(R.id.txt_doctor);
        group_available = itemView.findViewById(R.id.group_available);
        btn_yes = itemView.findViewById(R.id.btn_yes);
        btn_no = itemView.findViewById(R.id.btn_no);
        medicine_card = itemView.findViewById(R.id.medicine_card);
        txt_current = itemView.findViewById(R.id.txt_current);
    }
}

