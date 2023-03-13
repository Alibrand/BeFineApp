package com.ksacp2022t3.befine;

import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.befine.models.Account;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatientRegisterActivity extends AppCompatActivity {

    EditText txt_national_id,txt_first_name,txt_last_name,
            txt_email,txt_password,txt_confirm_password,
            txt_phone,txt_age,txt_chronic_diseases;
    Spinner sp_relative_relation;
    AppCompatButton btn_login,btn_register;

    String [] relations=new String[]{"My Self","Son","Daughter","Mother","Father","Sibling"};

    ImageView btn_back;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_register);
        txt_national_id = findViewById(R.id.txt_national_id);
        txt_first_name = findViewById(R.id.txt_first_name);
        txt_last_name = findViewById(R.id.txt_last_name);
        txt_email = findViewById(R.id.txt_email);
        txt_password = findViewById(R.id.txt_password);
        txt_confirm_password = findViewById(R.id.txt_confirm_password);
        txt_phone = findViewById(R.id.txt_phone);
        sp_relative_relation = findViewById(R.id.sp_relative_relation);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        btn_back = findViewById(R.id.btn_back);
        txt_age = findViewById(R.id.txt_age);
        txt_chronic_diseases = findViewById(R.id.txt_chronic_diseases);


         ArrayAdapter adapter=new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,relations);
         sp_relative_relation.setAdapter(adapter);


        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Creating New Account");
        progressDialog.setMessage("Please wait");

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_national_id =txt_national_id.getText().toString();
                String str_txt_first_name =txt_first_name.getText().toString();
                String str_txt_last_name =txt_last_name.getText().toString();
                String str_txt_email =txt_email.getText().toString();
                String str_txt_password =txt_password.getText().toString();
                String str_txt_confirm_password =txt_confirm_password.getText().toString();
                String str_txt_phone =txt_phone.getText().toString();
                String str_txt_relative_relation =sp_relative_relation.getSelectedItem().toString();
                String str_txt_age =txt_age.getText().toString();
                String str_txt_chronic_diseases =txt_chronic_diseases.getText().toString();



                 if(str_txt_national_id.isEmpty())
                 {
                      txt_national_id.setError("Required Field");
                      return;
                 }
                 if(str_txt_first_name.isEmpty())
                 {
                      txt_first_name.setError("Required Field");
                      return;
                 }
                 if(str_txt_last_name.isEmpty())
                 {
                      txt_last_name.setError("Required Field");
                      return;
                 }

                  if(str_txt_age.isEmpty())
                  {
                       txt_age.setError("Required Field");
                       return;
                  }
                  if(Integer.parseInt(str_txt_age)<=0 || Integer.parseInt(str_txt_age)>150)
                  {
                      txt_age.setError("Please enter a valid age");
                      return;
                  }


                 if(str_txt_email.isEmpty())
                 {
                      txt_email.setError("Required Field");
                      return;
                 }
                 if(!isValidEmail(str_txt_email))
                 {
                     txt_email.setError("Email should be like example@mail.com");
                     return;
                 }


                 if(str_txt_password.isEmpty())
                 {
                      txt_password.setError("Required Field");
                      return;
                 }

                if(!isValidPassword(str_txt_password))
                {
                    txt_password.setError("Weak password..it should be at least 8 characters and contains mix of numbers and letters");
                    return;
                }

                 if(str_txt_confirm_password.isEmpty())
                 {
                      txt_confirm_password.setError("Required Field");
                      return;
                 }

                 if(!str_txt_password.equals(str_txt_confirm_password))
                {
                    txt_confirm_password.setError("Passwords don't match");
                    txt_password.setError("Passwords don't match");
                    return;
                }
                if(str_txt_phone.isEmpty())
                {
                    txt_phone.setError("Required Field");
                    return;
                }
                if(!isValidPhone(str_txt_phone))
                {
                    txt_phone.setError("Invalid phone format.It should be like 05X XXX XXXX");
                    return;
                }





                 progressDialog.show();
                 firebaseAuth.createUserWithEmailAndPassword(str_txt_email,str_txt_password)
                         .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                             @Override
                             public void onSuccess(AuthResult authResult) {
                                 FirebaseUser user= authResult.getUser();
                                 Account account=new Account();
                                 account.setId(user.getUid());
                                 account.setFirst_name(str_txt_first_name);
                                 account.setLast_name(str_txt_last_name);
                                 account.setType("Patient");
                                 account.setPhone(str_txt_phone);
                                 account.setRelative_relation(str_txt_relative_relation);
                                 account.setNational_id(str_txt_national_id);
                                 account.setAge(str_txt_age);
                                 account.setChronic_diseases(str_txt_chronic_diseases);

                                 firestore.collection("accounts").document(user.getUid()).set(account)
                                         .addOnSuccessListener(new OnSuccessListener<Void>() {
                                             @Override
                                             public void onSuccess(Void unused) {
                                                 SharedPreferences sharedPreferences=PatientRegisterActivity.this.getSharedPreferences("befine",MODE_PRIVATE);
                                                 SharedPreferences.Editor editor=sharedPreferences.edit();
                                                 editor.putString("type","Patient");
                                                 editor.putString("full_name",account.getFirst_name()+" "+account.getLast_name());
                                                 editor.apply();
                                                 Intent intent = new Intent(PatientRegisterActivity.this, MessagePendingActivity.
                                                 class);
                                                 startActivity(intent);

                                                 progressDialog.dismiss();
                                                 finish();

                                             }
                                         }).addOnFailureListener(new OnFailureListener() {
                                             @Override
                                             public void onFailure(@NonNull Exception e) {
                                                  Toast.makeText(PatientRegisterActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                                             progressDialog.dismiss();
                                             }
                                         });
                             }
                         }).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 progressDialog.dismiss();
                                 Toast.makeText(PatientRegisterActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                             }
                         });







            }
        });


    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z]).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public boolean isValidEmail(final String email) {

        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,6}$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);

        return matcher.matches();

    }

    public static boolean isValidPhone(final String phone) {

        Pattern pattern;
        Matcher matcher;
        final String SA_PHONE_PATTERN = "^(5|05)(5|0|3|6|4|9|1|8|7)([0-9]{7})$";
        pattern = Pattern.compile(SA_PHONE_PATTERN);
        matcher = pattern.matcher(phone);

        return matcher.matches();

    }
}