package com.ksacp2022t3.befine.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.ksacp2022t3.befine.ChatActivity;
import com.ksacp2022t3.befine.R;
import com.ksacp2022t3.befine.models.Chat;

import java.text.SimpleDateFormat;
import java.util.List;

public class ChatsListAdapter extends RecyclerView.Adapter<ChatCard> {
    List<Chat> chatList;
    Context context;
    FirebaseAuth firebaseAuth;

    public ChatsListAdapter(List<Chat> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
        firebaseAuth=FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ChatCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat,parent,false);
        return new ChatCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatCard holder, int position) {
        Chat chat= chatList.get(position);
        String uid= firebaseAuth.getUid();
        //determine the other participant
        List<String> chat_users_ids=chat.getUsers_ids();
        List<String> chat_users_names=chat.getUsers_names();
        List<String> chat_users_types=chat.getUsers_types();
        String receiver_name;
        String receiver_id;
        String receiver_type;
        int indx=chat_users_ids.indexOf(uid);
        //the index should be either 0 or 1 only
        if(indx==0) {
            receiver_name = chat_users_names.get(1);
            receiver_id=chat_users_ids.get(1);
            receiver_type=chat_users_types.get(1);
        }
        else {
            receiver_name = chat_users_names.get(0);
            receiver_id=chat_users_ids.get(0);
            receiver_type=chat_users_types.get(0);
        }

        if(receiver_type.equals("Doctor"))
            holder.profile_image.setImageResource(R.drawable.doctor_profile);
        else if(receiver_type.equals("Pharmacist"))
            holder.profile_image.setImageResource(R.drawable.pharma_profile);





        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm aa");
        holder.time.setText(sdf.format(chat.getLast_update()) );
        holder.name.setText(receiver_name);

        holder.chat_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity. class);
                intent.putExtra("chat_id",chat.getId());
                intent.putExtra("receiver_name",receiver_name);
                intent.putExtra("receiver_id",receiver_id);
                intent.putExtra("receiver_type",receiver_type);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}

class ChatCard extends RecyclerView.ViewHolder{
    TextView time,name;
    LinearLayoutCompat chat_card;
    ImageView profile_image;

    public ChatCard(@NonNull View itemView) {
        super(itemView);
        time=itemView.findViewById(R.id.time);
        name=itemView.findViewById(R.id.name);
        profile_image=itemView.findViewById(R.id.profile_image);
        chat_card=itemView.findViewById(R.id.chat_card);
    }
}
