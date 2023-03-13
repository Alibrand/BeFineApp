package com.ksacp2022t3.befine.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ksacp2022t3.befine.AdminAccountsActivity;
import com.ksacp2022t3.befine.AdminReviewAccountActivity;
import com.ksacp2022t3.befine.R;
import com.ksacp2022t3.befine.models.Account;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class AccountsAdapter extends RecyclerView.Adapter<AccountItem>{
    List<Account> accountList;
    Context context;

    public AccountsAdapter(List<Account> accountList, Context context) {
        this.accountList = accountList;
        this.context = context;
    }

    @NonNull
    @Override
    public AccountItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_account,parent,false);
        return new AccountItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountItem holder, int position) {


            Account account=accountList.get(position);
            holder.txt_name.setText(account.getFirst_name()+" "+account.getLast_name());
            holder.account_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, AdminReviewAccountActivity.class);
                    intent.putExtra("user_id",account.getId());
                    context.startActivity(intent);

                }
            });

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");

        holder.txt_date.setText(simpleDateFormat.format(account.getCreated_at()));



    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }
}

class AccountItem extends RecyclerView.ViewHolder
{
    CardView account_card;
    TextView txt_name,txt_date;


    public AccountItem(@NonNull View itemView) {
        super(itemView);
        account_card=itemView.findViewById(R.id.account_card);
        txt_name=itemView.findViewById(R.id.txt_name);
        txt_date=itemView.findViewById(R.id.txt_date);


    }
}

