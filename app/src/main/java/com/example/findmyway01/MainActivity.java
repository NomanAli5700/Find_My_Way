package com.example.findmyway01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView comm,vic,rescue,police;

        comm=findViewById(R.id.comm);
        vic=findViewById(R.id.vc);
        rescue=findViewById(R.id.res);
        police=findViewById(R.id.pol);

        comm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(MainActivity.this,login.class);
                startActivity(it);

            }
        });
        vic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(MainActivity.this,login_vic.class);
                startActivity(it);

            }
        });
        rescue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(MainActivity.this,login_res.class);
                startActivity(it);

            }
        });
        police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(MainActivity.this,login_pol.class);
                startActivity(it);
            }
        });
    }
}