package com.ksacp2022t3.befine.services;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.Notification.BigTextStyle;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.befine.MainActivity;
import com.ksacp2022t3.befine.R;
import com.ksacp2022t3.befine.ReminderActivity;
import com.ksacp2022t3.befine.models.Medicine;

public class AlarmReceiver extends BroadcastReceiver {
    FirebaseFirestore firestore;

    @Override
    public void onReceive(Context context, Intent intent) {
        int alarm_id=intent.getIntExtra("alarm_id",0);
        if(alarm_id!=0)
        {
            AlarmHelper alarmHelper=new AlarmHelper(context);
            alarmHelper.autoCancelAlarm(alarm_id);
            return;
        }


        String medicine_id=intent.getStringExtra("medicine_id");
        String patient_id=intent.getStringExtra("patient_id");

        Log.d("alll",medicine_id);

        firestore=FirebaseFirestore.getInstance();

        firestore.collection("accounts")
                .document(patient_id)
                .collection("prescription")
                .document(medicine_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Intent intent1=new Intent(context, ReminderActivity.class);
                        Medicine medicine=documentSnapshot.toObject(Medicine.class);
                        intent1.putExtra("medicine_id",medicine.getId());
                        intent1.putExtra("patient_id",medicine.getPatient_id());
                        intent1.putExtra("intake",medicine.getRegular_intake());
                        intent1.putExtra("unit",medicine.getIntake_unit());
                        intent1.putExtra("name",medicine.getName());

                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        //context.startActivity(intent1);

                        String title="Be Fine App Reminder";
                        String msg="You should take "+medicine.getRegular_intake()+" "+medicine.getIntake_unit()
                                +" of "+medicine.getName();


                        make_notification(context,title,msg,intent1);

                    }
                }) ;





    }
    void make_notification(Context context,String title,String msg,Intent intent){
        String CHANNEL_ID = "channel_02";
        PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        //Assign BigText style notification
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.setBigContentTitle(msg);
        bigText.setSummaryText("Medicine Reminder");


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText( "Tap here to complete the action")
                .setStyle(bigText)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .setContentIntent(activityPendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(" Tap here to complete the action")
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


}