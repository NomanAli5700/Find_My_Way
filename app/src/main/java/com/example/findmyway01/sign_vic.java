package com.example.findmyway01;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.annotation.Nonnull;

public class sign_vic extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;


    public EditText fname,lname,email,contact_no,pass,cpass,year,month,day,district,city;
    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_vic);




        Button sign_up;

        fname=findViewById(R.id.fname3);
        lname=findViewById(R.id.lname3);
        email=findViewById(R.id.email3);
        pass=findViewById(R.id.pass3);
        contact_no=findViewById(R.id.cn3);
        cpass=findViewById(R.id.cpass3);
        year=findViewById(R.id.yy3);
        month=findViewById(R.id.mm3);
        day=findViewById(R.id.dd3);
        district=findViewById(R.id.dis3);
        city=findViewById(R.id.city3);
        sign_up=findViewById(R.id.up2);

        mAuth= FirebaseAuth.getInstance();

       // mStorageRef= FirebaseStorage.getInstance().getReference("profiles");
        mDatabaseRef= FirebaseDatabase.getInstance().getReference().child("users");
      //  RefAll= FirebaseDatabase.getInstance().getReference().child("Uploads");



        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Fname = fname.getText().toString();
                String Lname = lname.getText().toString();
                String Email = email.getText().toString();
                String Pass = pass.getText().toString();
                String Cpass = cpass.getText().toString();
                String cont=contact_no.getText().toString();
                String Year = year.getText().toString();
                String Month = month.getText().toString();
                String Day = day.getText().toString();
                String Dist = district.getText().toString();
                String City = city.getText().toString();

                if (Fname.isEmpty()) {
                    fname.setError("Enter name!");
                    fname.requestFocus();
                }
                if (Lname.isEmpty()) {
                    lname.setError("Enter name!");
                    lname.requestFocus();
                } else if (Email.isEmpty()) {
                    email.setError("Enter email!");
                    email.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                 email.setError("Please valid email!");
                 email.requestFocus();
                 }
                else if (Pass.isEmpty()) {
                    pass.setError("Enter password!");
                    pass.requestFocus();
                } else if (Pass.length() < 8) {
                    pass.setError("Minimum length should be 8!");
                    pass.requestFocus();
                } else if (Cpass.isEmpty()) {
                    cpass.setError("Enter phone!");
                    cpass.requestFocus();
                } else if (!pass.equals(cpass)) {
                    cpass.setError("Password not matched!");
                    cpass.requestFocus();
                } else if (cont.isEmpty()) {
                    contact_no.setError("Enter phone!");
                    contact_no.requestFocus();
                } else if (cont.length() < 13) {
                    contact_no.setError("Minimum length should be 13!");
                    contact_no.requestFocus();
                } else if (Year.isEmpty()) {
                    year.setError("Enter Year!");
                    year.requestFocus();
                } else if (Month.isEmpty()) {
                    month.setError("Enter Month!");
                    month.requestFocus();
                } else if (Day.isEmpty()) {
                    day.setError("Enter Day!");
                    day.requestFocus();
                } else if (Dist.isEmpty()) {
                    district.setError("Enter Day!");
                    district.requestFocus();
                } else if (City.isEmpty()) {
                    city.setError("Enter Day!");
                    city.requestFocus();
                }
                mAuth.createUserWithEmailAndPassword(Email,Pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@Nonnull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    // User registration successful
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    if (currentUser != null) {
                                        String userId = currentUser.getUid();

                                        // Create a user object with the provided data
                                        users user = new users(Fname, Lname, Email, Pass, Cpass, cont, Year, Month, Day, Dist, City);

                                        // Store the user data in the Firebase Realtime Database under the user's UID
                                        mDatabaseRef.child(userId).setValue(user)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@Nonnull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(sign_vic.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                                            Intent it=new Intent(sign_vic.this,login_vic.class);
                                                            startActivity(it);
                                                        } else {
                                                            Toast.makeText(sign_vic.this, "Failed to register, please try again!", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    // User registration failed
                                    Toast.makeText(sign_vic.this, "Failed to register, please try again! " + task.getException(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }
}


