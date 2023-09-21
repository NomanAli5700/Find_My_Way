package com.example.findmyway01;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Delete_user extends AppCompatActivity {

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);

        // Get reference to Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("officers");

        EditText userInput = findViewById(R.id.emp_id);
        Button deleteButton = findViewById(R.id.del);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valueToDelete = userInput.getText().toString();

                // Delete data from Firebase Realtime Database
                databaseReference.child(valueToDelete).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(Delete_user.this, "User data deleted", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(Delete_user.this, "Failed to delete user data", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}
