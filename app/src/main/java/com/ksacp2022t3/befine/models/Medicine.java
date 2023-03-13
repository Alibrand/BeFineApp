package com.ksacp2022t3.befine.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Medicine {
    String id;
    String name;
    String doctor_name;
    String doctor_uid;
    String patient_id;
    double quantity;
    double alert_quantity;
    double current_quantity;
    String pharmaceutical_form;
    @ServerTimestamp
    Date created_at;
    String notes;
    boolean reminder_active=false;
    boolean insufficient_quantity=false;
    boolean refillable=true;
    boolean available=false;
    int period=0;
    String period_unit="Days";
    Date expiry_date;
    Date start_date;
    //intake
    double regular_intake;
    String intake_unit;
    int dose_time;
    String interval_unit="Minutes";
    int alarm_id;
    String status="Not Ordered";




    public  static String[] pharmaceutical_forms=new String[]{"Tablets",
    "Capsules","Cream","Gel","Ointment","Syrup","Ampoule","Cartridge","Vial",
    "Tube","Drops","Oral Solution","Oral Paste","Elixir","Suspension","Suppositories"};
    public static String[] interval_units=new String[]{"Minutes","Hours","Days","Weeks"};
    public static String[] period_units=new String[]{"Days","Weeks","Months"};

    public Medicine() {
    }

    public String getStatus() {
        return status;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPeriod_unit() {
        return period_unit;
    }

    public void setPeriod_unit(String period_unit) {
        this.period_unit = period_unit;
    }

    public boolean isAvailable() {
        return available;
    }

    public int getAlarm_id() {
        return alarm_id;
    }

    public void setAlarm_id(int alarm_id) {
        this.alarm_id = alarm_id;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public Date getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(Date expiry_date) {
        this.expiry_date = expiry_date;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getDoctor_uid() {
        return doctor_uid;
    }

    public void setDoctor_uid(String doctor_uid) {
        this.doctor_uid = doctor_uid;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public double getCurrent_quantity() {
        return current_quantity;
    }

    public void setCurrent_quantity(double current_quantity) {
        this.current_quantity = current_quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getAlert_quantity() {
        return alert_quantity;
    }

    public void setAlert_quantity(double alert_quantity) {
        this.alert_quantity = alert_quantity;
    }

    public String getPharmaceutical_form() {
        return pharmaceutical_form;
    }

    public void setPharmaceutical_form(String pharmaceutical_form) {
        this.pharmaceutical_form = pharmaceutical_form;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isReminder_active() {
        return reminder_active;
    }

    public void setReminder_active(boolean reminder_active) {
        this.reminder_active = reminder_active;
    }

    public boolean isInsufficient_quantity() {
        return insufficient_quantity;
    }

    public void setInsufficient_quantity(boolean insufficient_quantity) {
        this.insufficient_quantity = insufficient_quantity;
    }

    public boolean isRefillable() {
        return refillable;
    }

    public void setRefillable(boolean refillable) {
        this.refillable = refillable;
    }

    public double getRegular_intake() {
        return regular_intake;
    }

    public void setRegular_intake(double regular_intake) {
        this.regular_intake = regular_intake;
    }

    public String getIntake_unit() {
        return intake_unit;
    }

    public void setIntake_unit(String intake_unit) {
        this.intake_unit = intake_unit;
    }

    public int getDose_time() {
        return dose_time;
    }

    public void setDose_time(int dose_time) {
        this.dose_time = dose_time;
    }

    public String getInterval_unit() {
        return interval_unit;
    }

    public void setInterval_unit(String interval_unit) {
        this.interval_unit = interval_unit;
    }
}
