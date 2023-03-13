package com.ksacp2022t3.befine;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    AppCompatButton btn_save;
    ImageView btn_back;
    EditText txt_current_password,txt_new_password,txt_confirm_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        txt_current_password = findViewById(R.id.txt_current_password);
        txt_new_password = findViewById(R.id.txt_new_password);
        txt_confirm_password = findViewById(R.id.txt_confirm_password);
        btn_save = findViewById(R.id.btn_save);
        btn_back = findViewById(R.id.btn_back);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Updating");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_current_password =txt_current_password.getText().toString();
                String str_txt_new_password =txt_new_password.getText().toString();
                String str_txt_confirm_password =txt_confirm_password.getText().toString();


                if(str_txt_current_password.isEmpty())
                {
                    txt_current_password.setError("Required Field");
                    return;
                }

                if(str_txt_new_password.isEmpty())
                {
                     txt_new_password.setError("Required Field");
                     return;
                }


                if(!isValidPassword(str_txt_new_password))
                {
                    txt_new_password.setError("Weak password..it should be at least 8 characters and contains mix of numbers and letters");
                    return;
                }



                if(str_txt_confirm_password.isEmpty())
                {
                    txt_confirm_password.setError("Required Field");
                    return;
                }

                if(!str_txt_new_password.equals(str_txt_confirm_password))
                {
                    txt_confirm_password.setError("Passwords don't match");
                    txt_new_password.setError("Passwords don't match");
                    return;
                }

                progressDialog.show();
                FirebaseUser currentUser=firebaseAuth.getCurrentUser();

                AuthCredential credential= EmailAuthProvider.getCredential(currentUser.getEmail(),str_txt_current_password);

                currentUser.reauthenticate(credential)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                currentUser.updatePassword(str_txt_new_password)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                makeText(ChangePasswordActivity.this,"Password changed successfully" , LENGTH_LONG).show();
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                makeText(ChangePasswordActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                makeText(ChangePasswordActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
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
}