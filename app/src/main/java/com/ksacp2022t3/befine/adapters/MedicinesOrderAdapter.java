package com.ksacp2022t3.befine.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ksacp2022t3.befine.R;
import com.ksacp2022t3.befine.models.Medicine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class MedicinesOrderAdapter extends RecyclerView.Adapter<MedicineOrderItem>{
    List<Medicine> medicineItems;
    Context context;
    public  List<Medicine> orderList=new ArrayList<>();
    List<Medicine> checkedList;



    public MedicinesOrderAdapter(List<Medicine> medicineItems, Context context,List<Medicine> checkedList) {
        this.medicineItems = medicineItems;
        this.context = context;
        this.checkedList=  checkedList;
        this.orderList=new ArrayList<>(checkedList);


    }
    public MedicinesOrderAdapter(List<Medicine> medicineItems, Context context) {
        this.medicineItems = medicineItems;
        this.context = context;


    }

    @NonNull
    @Override
    public MedicineOrderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_order_medicine,parent,false);
        return new MedicineOrderItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineOrderItem holder, int position) {
            Medicine medicine= medicineItems.get(position);
            holder.txt_name.setText(medicine.getName());
          holder.txt_doctor.setText(medicine.getDoctor_name());
          holder.txt_form.setText(medicine.getPharmaceutical_form());
        holder.txt_current.setText(String.valueOf(medicine.getCurrent_quantity()));

          if(!medicine.getStatus().equals("Not Ordered")&&!medicine.isRefillable())
          {
              holder.check_box.setEnabled(false);
              holder.txt_current.setText("Not Refillable");
          }

          if(checkedList!=null)
          {
              for (Medicine m:checkedList
                   ) {
                  if(m.getId().equals(medicine.getId()))
                      holder.check_box.setChecked(true);
              }
          }


          holder.check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
              @Override
              public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                  if(b==true)
                  orderList.add(medicine);
                  else
                   orderList.removeIf(new Predicate<Medicine>() {
                       @Override
                       public boolean test(Medicine med) {
                           return medicine.getId().equals(med.getId());
                       }
                   });
//                  Log.d("dddd",orderList.size()+"k");
              }
          });






    }

    @Override
    public int getItemCount() {
        return medicineItems.size();
    }
}

class MedicineOrderItem extends RecyclerView.ViewHolder
{

    TextView txt_name,txt_form,txt_doctor,txt_current;
    AppCompatCheckBox check_box;
    CardView medicine_card;

    public MedicineOrderItem(@NonNull View itemView) {
        super(itemView);
        txt_form=itemView.findViewById(R.id.txt_form);
        txt_name=itemView.findViewById(R.id.txt_name);
        txt_current=itemView.findViewById(R.id.txt_current);
        txt_doctor=itemView.findViewById(R.id.txt_doctor);
        check_box = itemView.findViewById(R.id.check_box);
        medicine_card = itemView.findViewById(R.id.medicine_card);
    }
}

