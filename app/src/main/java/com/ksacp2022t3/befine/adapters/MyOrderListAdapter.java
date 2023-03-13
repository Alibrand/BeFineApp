package com.ksacp2022t3.befine.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ksacp2022t3.befine.OrderEditActivity;
import com.ksacp2022t3.befine.R;
import com.ksacp2022t3.befine.ViewOrderActivity;
import com.ksacp2022t3.befine.models.Order;

import java.text.SimpleDateFormat;
import java.util.List;

public class MyOrderListAdapter extends RecyclerView.Adapter<OrderItem>{
    List<Order> orderList;
    Context context;






    public MyOrderListAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;


    }

    @NonNull
    @Override
    public OrderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_order,parent,false);
        return new OrderItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItem holder, int position) {
            Order order= orderList.get(position);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

            holder.txt_date.setText(simpleDateFormat.format(order.getCreated_at()));
          holder.txt_pharmacy.setText(order.getPharmacy_name());
          holder.txt_status.setText(order.getStatus());

          holder.order_card.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  if(order.getStatus().equals("Pending")) {
                      Intent intent = new Intent(context, OrderEditActivity.class);
                      intent.putExtra("order_id", order.getId());
                        context.startActivity(intent);
                  }
                  else
                  {
                      Intent intent = new Intent(context, ViewOrderActivity.class);
                      intent.putExtra("order_id", order.getId());
                      context.startActivity(intent);
                  }
              }
          });





    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}

class OrderItem extends RecyclerView.ViewHolder
{

    TextView txt_date,txt_pharmacy,txt_status;
    CardView order_card;


    public OrderItem(@NonNull View itemView) {
        super(itemView);
        txt_date=itemView.findViewById(R.id.txt_date);
        txt_pharmacy=itemView.findViewById(R.id.txt_pharmacy);
       // txt_desc=itemView.findViewById(R.id.txt_desc);
        txt_status=itemView.findViewById(R.id.txt_status);
        order_card = itemView.findViewById(R.id.order_card);

    }
}

