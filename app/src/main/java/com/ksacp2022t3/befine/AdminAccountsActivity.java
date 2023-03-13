package com.ksacp2022t3.befine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.befine.adapters.AccountsAdapter;
import com.ksacp2022t3.befine.models.Account;

import java.util.ArrayList;
import java.util.List;

public class AdminAccountsActivity extends AppCompatActivity {

    ImageView btn_back;
    RecyclerView recycler_accounts;
    TextView txt_title;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    String status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_accounts);
        btn_back = findViewById(R.id.btn_back);
        recycler_accounts = findViewById(R.id.recycler_accounts);
        txt_title = findViewById(R.id.txt_title);



        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        status=getIntent().getStringExtra("status");

        txt_title.setText(status+" Accounts");



        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading Accounts");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        load_accounts();








    }

    private void load_accounts() {
        progressDialog.show();
        firestore.collection("accounts")
                .whereEqualTo("type","Patient")
                .whereEqualTo("status",status)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        List<Account> accountList=new ArrayList<>();
                        for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments()
                        ) {
                            accountList.add(doc.toObject(Account.class));
                        }
                        AccountsAdapter adapter=new AccountsAdapter(accountList,AdminAccountsActivity.this);
                        recycler_accounts.setAdapter(adapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AdminAccountsActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_accounts();
    }
}