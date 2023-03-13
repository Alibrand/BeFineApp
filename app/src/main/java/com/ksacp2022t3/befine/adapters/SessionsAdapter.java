package com.ksacp2022t3.befine.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.befine.EditSessionActivity;
import com.ksacp2022t3.befine.MedicineViewActivity;
import com.ksacp2022t3.befine.R;
import com.ksacp2022t3.befine.SessionViewActivity;
import com.ksacp2022t3.befine.models.ClinicSession;

import java.text.SimpleDateFormat;
import java.util.List;

public class SessionsAdapter extends RecyclerView.Adapter<SessionItem>{
    List<ClinicSession> clinicSessionList;
    Context context;

    FirebaseAuth firebaseAuth;



    public SessionsAdapter(List<ClinicSession> clinicSessionList, Context context) {
        this.clinicSessionList = clinicSessionList;
        this.context = context;

        firebaseAuth=FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public SessionItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session,parent,false);
        return new SessionItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionItem holder, int position) {
            ClinicSession clinicSession= clinicSessionList.get(position);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

            holder.txt_date.setText(simpleDateFormat.format(clinicSession.getCreated_at()));
          holder.txt_doctor.setText(clinicSession.getDoctor_name());
          holder.txt_diagnosis.setText(clinicSession.getDiagnosis());

          SharedPreferences sharedPreferences=context.getSharedPreferences("befine",Context.MODE_PRIVATE);
        String accout_type=sharedPreferences.getString("type","");
       //this delete will only be shown for the doctor who created this session
        if(!firebaseAuth.getUid().equals(clinicSession.getDoctor_uid()))
        {
            holder.btn_edit.setVisibility(View.GONE);
        }

        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditSessionActivity. class);
                intent.putExtra("patient_id",clinicSession.getPatient_id());
                intent.putExtra("session_id",clinicSession.getId());
                context.startActivity(intent);
            }
        });

        holder.session_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SessionViewActivity. class);
                intent.putExtra("session_id",clinicSession.getId());
                intent.putExtra("patient_id",clinicSession.getPatient_id());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return clinicSessionList.size();
    }
}

class SessionItem extends RecyclerView.ViewHolder
{

    TextView txt_date,txt_diagnosis,txt_doctor;
    ImageView btn_edit;
    CardView session_card;

    public SessionItem(@NonNull View itemView) {
        super(itemView);
        txt_date=itemView.findViewById(R.id.txt_date);
        txt_diagnosis=itemView.findViewById(R.id.txt_diagnosis);
       // txt_desc=itemView.findViewById(R.id.txt_desc);
        txt_doctor=itemView.findViewById(R.id.txt_doctor);
        btn_edit = itemView.findViewById(R.id.btn_edit);
        session_card = itemView.findViewById(R.id.session_card);
    }
}

