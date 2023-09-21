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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.annotation.Nonnull;

public class sign_res extends AppCompatActivity {
    public EditText fname,lname,email,des,org,contact_no,pass,cpass,year,month,day,district,city;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference RefAll;
    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_res);


        Button sign_up;

        fname=findViewById(R.id.fname7);
        lname=findViewById(R.id.lname7);
        email=findViewById(R.id.emp7);
        des=findViewById(R.id.des2);
        org=findViewById(R.id.org);
        pass=findViewById(R.id.pass7);
        contact_no=findViewById(R.id.cn7);
        cpass=findViewById(R.id.cpass7);
        year=findViewById(R.id.yy7);
        month=findViewById(R.id.mm7);
        day=findViewById(R.id.dd7);
        district=findViewById(R.id.dis7);
        city=findViewById(R.id.city7);
        sign_up=findViewById(R.id.up7);


        mAuth= FirebaseAuth.getInstance();

         mStorageRef= FirebaseStorage.getInstance().getReference("profiles");
        mDatabaseRef= FirebaseDatabase.getInstance().getReference().child("officers");
          RefAll= FirebaseDatabase.getInstance().getReference().child("Uploads");


        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Fname_1 = fname.getText().toString().trim();
                String Lname_1 = lname.getText().toString().trim();
                String Email = email.getText().toString().trim();
                String Des_1 = des.getText().toString().trim();
                String Org_1=org.getText().toString().trim();
                String Pass_1 = pass.getText().toString().trim();
                String Cpass_1 = cpass.getText().toString().trim();
                String cont_1=contact_no.getText().toString().trim();
                String Year_1 = year.getText().toString().trim();
                String Month_1 = month.getText().toString().trim();
                String Day_1 = day.getText().toString().trim();
                String Dist_1 = district.getText().toString().trim();
                String City_1 = city.getText().toString().trim();


                if (Fname_1.isEmpty()) {
                    fname.setError("Enter name!");
                    fname.requestFocus();
                }

                if (Lname_1.isEmpty()) {
                    lname.setError("Enter name!");
                    lname.requestFocus();
                } else if (Email.isEmpty()) {
                    email.setError("Enter email!");
                    email.requestFocus();
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                    email.setError("Please valid email address!");
                    contact_no.requestFocus();
                }
                else if (Pass_1.isEmpty()) {
                    pass.setError("Enter password!");
                    pass.requestFocus();
                } else if (Pass_1.length() < 8) {
                    pass.setError("Minimum length should be 8!");
                    pass.requestFocus();
                } else if (Cpass_1.isEmpty()) {
                    cpass.setError("Enter confirm password!");
                    cpass.requestFocus();
                }// else if (!pass.equals(cpass)) {
                   // cpass.setError("Password not matched!");
                   // cpass.requestFocus();
               // }
                else if (cont_1.isEmpty()) {
                    contact_no.setError("Enter phone!");
                    contact_no.requestFocus();
                }else if (Des_1.isEmpty()) {
                    des.setError("Enter Your Designation!");
                    des.requestFocus();
                }
                else if (Org_1.isEmpty()) {
                    org.setError("Enter your Organization!");
                    org.requestFocus();
                }
                else if (cont_1.length() < 13) {
                    contact_no.setError("Minimum length should be 13!");
                    contact_no.requestFocus();
                }else if (Year_1.isEmpty()) {
                    year.setError("Enter Year!");
                    year.requestFocus();
                } else if (Month_1.isEmpty()) {
                    month.setError("Enter Month!");
                    month.requestFocus();
                } else if (Day_1.isEmpty()) {
                    day.setError("Enter Day!");
                    day.requestFocus();
                } else if (Dist_1.isEmpty()) {
                    district.setError("Enter Day!");
                    district.requestFocus();
                } else if (City_1.isEmpty()) {
                    city.setError("Enter Day!");
                    city.requestFocus();
                }
                mAuth.createUserWithEmailAndPassword(Email,Pass_1)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@Nonnull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    // User registration successful
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    if (currentUser != null) {
                                        String userId = currentUser.getUid();

                                        // Create a user object with the provided data
                                        officers user = new officers(Fname_1, Lname_1, Email, Pass_1, Cpass_1, cont_1,Org_1,Des_1, Year_1, Month_1, Day_1, Dist_1, City_1);

                                        // Store the user data in the Firebase Realtime Database under the user's UID
                                        mDatabaseRef.child(userId).setValue(user)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@Nonnull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(sign_res.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                                            Intent it=new Intent(sign_res.this,login_res.class);
                                                            startActivity(it);
                                                            // TODO: Handle successful registration, navigate to the main activity, etc.
                                                        } else {
                                                            Toast.makeText(sign_res.this, "Failed to register, please try again!", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    // User registration failed
                                    Toast.makeText(sign_res.this, "Failed to register, please try again! " + task.getException(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }
}


