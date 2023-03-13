package com.ksacp2022t3.befine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    AppCompatButton btn_login,btn_register;
    RadioGroup type_group;

    String type="Patient";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        type_group = findViewById(R.id.type_group);




        check_user();







        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton selected=findViewById(type_group.getCheckedRadioButtonId()) ;
                type=selected.getText().toString();
                if(type.equals("Patient"))
                {
                    Intent intent = new Intent(MainActivity.this,PatientRegisterActivity. class);
                    startActivity(intent);
                }
                else if (type.equals("Pharmacy")){
                    Intent intent = new Intent(MainActivity.this,PharmacistRegisterActivity. class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(MainActivity.this,DoctorRegisterActivity. class);
                    startActivity(intent);
                }
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LoginActivity. class);
                startActivity(intent);
            }
        });






    }

    private void check_user(){
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser logged_user=firebaseAuth.getCurrentUser();
        if(logged_user!=null)
        {
            if(logged_user.getEmail().equals("admin@befine.com"))
            {
                Intent intent = new Intent(MainActivity.this,AdminHomeActivity. class);
                startActivity(intent);
            }
            else{
                SharedPreferences sharedPreferences=MainActivity.this.getSharedPreferences("befine",MODE_PRIVATE);
                String type=sharedPreferences.getString("type","empty");

                Intent intent;
                if(type.equals("Patient"))
                {
                    intent = new Intent(MainActivity.this, PatientHomeActivity.class);
                }
                else if(type.equals("Pharmacy")){
                    intent = new Intent(MainActivity.this, PharmacistHomeActivity.class);
                }
                else {
                    intent = new Intent(MainActivity.this, DoctorHomeActivity.class);
                }
                startActivity(intent);
            }
            finish();
        }

    }
}