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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.befine.adapters.AdminOrdersListAdapter;
import com.ksacp2022t3.befine.adapters.PharmacyOrderListAdapter;
import com.ksacp2022t3.befine.models.Order;

import java.util.List;

public class AdminPatientsOrdersActivity extends AppCompatActivity {
    ImageView btn_back;
    RecyclerView recycler_orders;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_patients_orders);
        btn_back = findViewById(R.id.btn_back);
        recycler_orders = findViewById(R.id.recycler_orders);

        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();


        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });






    }

    private void load_orders() {
        progressDialog.show();
        firestore.collection("orders")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        List<Order> orderList=queryDocumentSnapshots.toObjects(Order.class);

                        AdminOrdersListAdapter adapter=new AdminOrdersListAdapter(orderList,AdminPatientsOrdersActivity.this);
                        recycler_orders.setAdapter(adapter);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AdminPatientsOrdersActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                        finish();

                    }

                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_orders();
    }
}