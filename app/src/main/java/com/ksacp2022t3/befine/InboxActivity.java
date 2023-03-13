package com.ksacp2022t3.befine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.befine.adapters.ChatsListAdapter;
import com.ksacp2022t3.befine.models.Chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InboxActivity extends AppCompatActivity {
    ImageView btn_back;
    RecyclerView recycler_chats;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        recycler_chats = findViewById(R.id.recycler_chats);
        btn_back = findViewById(R.id.btn_back);


        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();


        String uid=firebaseAuth.getUid();

        progressDialog =new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        firestore.collection("inbox")
                .whereArrayContains("users_ids",uid)
                .orderBy("last_update", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        List<Chat> chatList=new ArrayList<>();
                        for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments()
                        ) {
                            Chat chat=doc.toObject(Chat.class);
                            chatList.add(chat);

                        }

                        ChatsListAdapter adapter=new ChatsListAdapter(chatList,InboxActivity.this);
                        recycler_chats.setAdapter(adapter);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(InboxActivity.this,"Failed to load chats" , Toast.LENGTH_LONG).show();

                    }
                });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




    }
}