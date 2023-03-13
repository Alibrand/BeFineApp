package com.ksacp2022t3.befine.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class AlarmHelper {
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    Intent alarmIntent;
    Context context;
    Calendar calendar;
    int SECOND=1000;
    int MINUTE=60*SECOND;
    int HOUR=60*MINUTE;
    int DAY=24*HOUR;
    int WEEK=DAY*7;
    int MONTH=30 * DAY;
    String patient_id,medicine_id;
    Date start_date;

    public AlarmHelper(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    }

    public AlarmHelper(Context context, String patient_id, String medicine_id, Date start_date) {
        this.context = context;
        this.patient_id = patient_id;
        this.medicine_id = medicine_id;
        alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.start_date=start_date;
    }

    public void setAlarmForMedicine(int alarm_id, int interval, String unit )
    {
        int delay=10000;
        alarmIntent.putExtra("medicine_id",medicine_id);
        alarmIntent.putExtra("patient_id",patient_id);
        calendar=Calendar.getInstance();
        calendar.setTime(start_date);
        pendingIntent = PendingIntent.getBroadcast(context, alarm_id, alarmIntent, 0);
        int interavlMilis=0;
        if(unit.equals("Minutes"))
            interavlMilis=interval*MINUTE;
        else if(unit.equals("Hours"))
            interavlMilis=interval*HOUR;
        else if(unit.equals("Days"))
            interavlMilis=interval*DAY;
        else
            interavlMilis=interval*WEEK;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+30*SECOND,
                interavlMilis, pendingIntent);

        Toast.makeText(context, "The system will remind you every "+interval+" "+unit, Toast.LENGTH_LONG).show();



    }

    public  void  cancelAlarm(int alarm_id){
        pendingIntent = PendingIntent.getBroadcast(context, alarm_id, alarmIntent, 0);
        alarmManager.cancel(pendingIntent);


        Toast.makeText(context, "Alarm canceled successfully" , Toast.LENGTH_LONG).show();
    }

    public  void  autoCancelAlarm(int alarm_id){
        Intent cancelIntent=new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, alarm_id, cancelIntent, 0);
        alarmManager.cancel(pendingIntent);
        Log.d("alarmm","auto cancel");
        Toast.makeText(context, "Alarm canceled successfully" , Toast.LENGTH_LONG).show();
    }

    public   void setAutoAlarmCancel(int alarm_id,int period,String unit){
        Intent cancelIntent=new Intent(context, AlarmReceiver.class);
        cancelIntent.putExtra("alarm_id",alarm_id);
        calendar=Calendar.getInstance();
        pendingIntent = PendingIntent.getBroadcast(context, (int)calendar.getTimeInMillis(), cancelIntent, 0);
        int interavlMilis=0;
        if(unit.equals("Days"))
            interavlMilis=period*DAY;
        else if(unit.equals("Weeks"))
            interavlMilis=period*WEEK;
        else
            interavlMilis=period*MONTH;
        //interavlMilis=2*MINUTE;

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+interavlMilis,
                 pendingIntent);

    }






}
