package com.ksacp2022t3.befine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MessageRejectedActivity extends AppCompatActivity {

    AppCompatButton btn_back;
        TextView txt_notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_rejected);
        btn_back = findViewById(R.id.btn_back);
        txt_notes = findViewById(R.id.txt_notes);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String notes=getIntent().getStringExtra("notes");
        txt_notes.setText(notes);
        if(notes.isEmpty())
            txt_notes.setVisibility(View.GONE);



    }
}