package com.example.donghee.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegActivity extends AppCompatActivity {

    private Button RegSend, Regcancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //+++++++++++++++++++Button Object++++++++++++++++++++++
        RegSend = (Button)findViewById(R.id.regsubmit);
        Regcancel = (Button)findViewById(R.id.regCancel);
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++

        //++++++++++++++++++++OnclickListner+++++++++++++++++++++++++
        RegSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Regcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regCancel = new Intent(RegActivity.this, MainActivity.class);
                startActivity(regCancel);
            }
        });
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    }
}
