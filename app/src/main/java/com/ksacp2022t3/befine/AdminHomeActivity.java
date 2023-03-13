package com.ksacp2022t3.befine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.befine.adapters.AdminOrdersListAdapter;
import com.ksacp2022t3.befine.models.Account;

import java.util.List;

public class AdminHomeActivity extends AppCompatActivity {
    ImageView btn_logout;
    FirebaseAuth firebaseAuth;
    AppCompatButton btn_pending_accounts,btn_active_accounts,btn_rejected_accounts
            ,btn_find_patient,btn_inbox,btn_patients_orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        btn_logout = findViewById(R.id.btn_logout);
        btn_pending_accounts = findViewById(R.id.btn_pending_accounts);
        btn_active_accounts = findViewById(R.id.btn_active_accounts);
        btn_rejected_accounts = findViewById(R.id.btn_rejected_accounts);
        btn_find_patient = findViewById(R.id.btn_find_patient);
        btn_inbox = findViewById(R.id.btn_my_inbox);
        btn_patients_orders = findViewById(R.id.btn_patients_orders);



        firebaseAuth=FirebaseAuth.getInstance();


        btn_inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this,InboxActivity. class);
                startActivity(intent);
            }
        });

        btn_find_patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this,FindPatientActivity. class);
                startActivity(intent);
            }
        });


        btn_patients_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminPatientsOrdersActivity. class);
                startActivity(intent);
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent intent = new Intent(AdminHomeActivity.this,MainActivity. class);
                startActivity(intent);
                if(isNotificationVisible())
                {
                    NotificationManager notificationManager =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.cancel(4);
                }
                finish();
            }
        });

        btn_pending_accounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this,AdminAccountsActivity. class);
                intent.putExtra("status","Pending");
                startActivity(intent);
            }
        });

        btn_active_accounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this,AdminAccountsActivity. class);
                intent.putExtra("status","Active");
                startActivity(intent);
            }
        });

        btn_rejected_accounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this,AdminAccountsActivity. class);
                intent.putExtra("status","Rejected");
                startActivity(intent);
            }
        });



    }

    private  void  check_new_messages(){
        btn_inbox.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_mail_24,0,0,0);
        FirebaseFirestore.getInstance().collection("inbox")
                .whereArrayContains("users_ids",firebaseAuth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments()) {
                            doc.getReference()
                                    .collection("messages")
                                    .whereEqualTo("to",firebaseAuth.getUid())
                                    .whereEqualTo("status","unseen")
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            int new_messages_count= queryDocumentSnapshots.getDocuments().size();
                                            if(new_messages_count>0)
                                            {
                                                btn_inbox.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_mark_email_unread_24,0,0,0);
                                                Toast.makeText(AdminHomeActivity.this, "You have new messages..Check your Inbox", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    })
                            ;
                        }
                    }
                });


    }

    private void check_new_accounts(){
        FirebaseFirestore.getInstance()
                .collection("accounts")
                .whereEqualTo("type","Patient")
                .whereEqualTo("status",
                        "Pending")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Account> accounts=queryDocumentSnapshots.toObjects(Account.class);
                        if(accounts.size()>0 && !isNotificationVisible())
                        {
                            Intent intent = new Intent(AdminHomeActivity.this,AdminAccountsActivity. class);
                            intent.putExtra("status","Pending");

                            make_notification(AdminHomeActivity.this,"New Accounts have been created",
                                    "There are "+accounts.size()+" Pending accounts waiting tobe reviewed..Tap here",intent);
                        }
                        else
                        if(isNotificationVisible())
                        {
                            NotificationManager notificationManager =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            notificationManager.cancel(4);
                        }
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        check_new_messages();
        check_new_accounts();
    }



    void make_notification(Context context, String title, String msg, Intent intent){
        String CHANNEL_ID = "channel_02";
        PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        //Assign BigText style notification
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(msg);
        bigText.setSummaryText("Medicine Checker");


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText( "Tap here to get your prescription")
                .setStyle(bigText)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .setContentIntent(activityPendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(" Tap here to your prescription")
                .setFullScreenIntent(activityPendingIntent,true)
                .setWhen(System.currentTimeMillis())
                .setChannelId(CHANNEL_ID)
                .setAutoCancel(true);

        NotificationManager notificationManager =  (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminders";
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{500,1000,500,1000});

            // Set the Notification Channel for the Notification Manager.
            notificationManager.createNotificationChannel(mChannel);
        }

        Notification notification=builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
// notificationId is a unique int for each notification that you must define
        notificationManager.notify(4, builder.build());
    }

    private boolean isNotificationVisible () {
        NotificationManager mNotificationManager =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        StatusBarNotification[] notifications =
                mNotificationManager.getActiveNotifications();
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == 4) {
                return true;
            }
        }
        return  false;
    }
}