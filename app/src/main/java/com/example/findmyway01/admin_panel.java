package com.example.findmyway01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class admin_panel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        Button btn,btn1,btn2,btn3,btn4;

        btn=findViewById(R.id.add);
        btn1=findViewById(R.id.update);
        btn2=findViewById(R.id.delete);
        btn3=findViewById(R.id.view);
        btn4=findViewById(R.id.map);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(admin_panel.this,sign_res.class);
                startActivity(it);
            }
        });



        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(admin_panel.this,sign_res.class);
                startActivity(it);
            }
        });


        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(admin_panel.this,Delete_user.class);
                startActivity(it);
            }
        });


        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(admin_panel.this,view_employee.class);
                startActivity(it);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(admin_panel.this,commu_map.class);
                startActivity(it);
            }
        });


    }
}