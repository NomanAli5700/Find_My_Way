package com.example.findmyway01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Police_admin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_admin);

        Button btn,btn1,btn2,btn3,btn4;

        btn=findViewById(R.id.add1);
        btn1=findViewById(R.id.update1);
        btn2=findViewById(R.id.delete1);
        btn3=findViewById(R.id.view1);
        btn4=findViewById(R.id.map1);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(Police_admin.this,sign_pol.class);
                startActivity(it);
            }
        });



        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(Police_admin.this,sign_pol.class);
                startActivity(it);
            }
        });


        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(Police_admin.this,Delete_user.class);
                startActivity(it);
            }
        });


        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(Police_admin.this,view_employee.class);
                startActivity(it);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(Police_admin.this,commu_map.class);
                startActivity(it);
            }
        });


    }
}