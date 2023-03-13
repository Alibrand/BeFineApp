package com.ksacp2022t3.befine;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import static com.ksacp2022t3.befine.PatientRegisterActivity.isValidPhone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.ksacp2022t3.befine.models.Account;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PharmacistRegisterActivity extends AppCompatActivity {
    EditText txt_pharmacy_name,txt_first_name,txt_last_name,
            txt_email,txt_password,txt_confirm_password,
            txt_phone;
    AppCompatButton btn_login,btn_register,btn_location;
    ImageView btn_back;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    GeoPoint pharmacy_location=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacist_register);
        txt_pharmacy_name = findViewById(R.id.txt_pharmacy_name);
        btn_location = findViewById(R.id.btn_location);
        txt_first_name = findViewById(R.id.txt_first_name);
        txt_last_name = findViewById(R.id.txt_last_name);
        txt_email = findViewById(R.id.txt_email);
        txt_password = findViewById(R.id.txt_password);
        txt_confirm_password = findViewById(R.id.txt_confirm_password);
        txt_phone = findViewById(R.id.txt_phone);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        btn_back = findViewById(R.id.btn_back);


        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Creating New Account");
        progressDialog.setMessage("Please wait");

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PharmacistRegisterActivity.this,SelectLocationActivity.class);
                startActivityForResult(intent,110);
            }
        });

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

                String str_txt_pharmacy_name =txt_pharmacy_name.getText().toString();
                String str_txt_first_name =txt_first_name.getText().toString();
                String str_txt_last_name =txt_last_name.getText().toString();
                String str_txt_email =txt_email.getText().toString();
                String str_txt_password =txt_password.getText().toString();
                String str_txt_confirm_password =txt_confirm_password.getText().toString();
                String str_txt_phone =txt_phone.getText().toString();



                if(str_txt_pharmacy_name.isEmpty())
                {
                    txt_pharmacy_name.setError("Required Field");
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
                if(pharmacy_location==null)
                {
                    makeText(PharmacistRegisterActivity.this,"You should select pharmacy location" , LENGTH_LONG).show();
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
                                account.setType("Pharmacy");
                                account.setPharmacy_name(str_txt_pharmacy_name);
                                account.setPhone(str_txt_phone);
                                account.setLocation(pharmacy_location);

                                firestore.collection("accounts").document(user.getUid()).set(account)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                SharedPreferences sharedPreferences=PharmacistRegisterActivity.this.getSharedPreferences("befine",MODE_PRIVATE);
                                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                                editor.putString("type","Pharmacy");
                                                editor.putString("full_name",account.getFirst_name()+" "+account.getLast_name());
                                                editor.putString("pharmacy_name",account.getPharmacy_name());
                                                editor.apply();

                                                Intent intent = new Intent(PharmacistRegisterActivity.this,PharmacistHomeActivity.
                                                        class);
                                                startActivity(intent);
                                                progressDialog.dismiss();
                                                finish();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                makeText(PharmacistRegisterActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                makeText(PharmacistRegisterActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==110)
            if(resultCode==RESULT_OK)
            {
                makeText(PharmacistRegisterActivity.this,"Location set successfully", LENGTH_LONG).show();
                double lat=data.getDoubleExtra("lat",0.0);
                double lng=data.getDoubleExtra("lng",0.0);
                pharmacy_location=new GeoPoint(lat,lng);
            }
    }
}