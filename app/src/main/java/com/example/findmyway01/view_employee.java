package com.example.findmyway01;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class view_employee extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_employee);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get reference to Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("officers");

        // Fetch employee data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@Nonnull DataSnapshot dataSnapshot) {
                List<EmployeeModelClass> employees = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    EmployeeModelClass employee = snapshot.getValue(EmployeeModelClass.class);
                    employees.add(employee);
                }

                EmployeeAdapterClass adapter = new EmployeeAdapterClass(employees);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@Nonnull DatabaseError databaseError) {
                Log.e("view_employee", "Database Error: " + databaseError.getMessage());
            }
        });
    }
}
