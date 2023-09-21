package com.example.findmyway01;

import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executor;

import javax.annotation.Nonnull;

//import androidx.annotation.RequiresApi;

public class login extends AppCompatActivity {

    Button btn_fp,btn_fppin;
    FirebaseAuth auth;
    ImageView img;
    EditText email,pass;
  //  @RequiresApi(api = Build.VERSION_CODES.R)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Boolean e = false, p = false;
       // EditText email, pass;
        TextView reg;
        Button btn;
        //  DBHelper dbHelper;

        //
        img = findViewById(R.id.img);
        btn_fp = findViewById(R.id.btn_fp);
        btn_fppin = findViewById(R.id.btn_fppin);
        //
        reg = findViewById(R.id.reg);
        btn = findViewById(R.id.sign);
        email = findViewById(R.id.email1);
        pass = findViewById(R.id.pass1);
        auth = FirebaseAuth.getInstance();

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(login.this, Signup.class);
                startActivity(it);

            }
        });
        //dbHelper = new DBHelper(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });

        //create new method to check whether support or not
        checkBioMetricSupported();
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(login.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            // this method will automatically call when it is succeed verify fingerprint
            @Override
            public void onAuthenticationSucceeded(
                    BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!" , Toast.LENGTH_SHORT).show();
                startActivity(new Intent(login.this, commu_map.class));
            }

            // this method will automatically call when it is failed verify fingerprint
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                //attempt not regconized fingerprint
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
        // perform action button only fingerprint
        img.setOnClickListener(view -> {

            // call method launch dialog fingerprint
            BiometricPrompt.PromptInfo.Builder promptInfo = dialogMetric();
            promptInfo.setNegativeButtonText("Cancel");

            //activate callback if it succeed
            biometricPrompt.authenticate(promptInfo.build());
        });

        //perform action button fingerprint with PIN code input
        btn_fppin.setOnClickListener(view -> {

            // call method launch dialog fingerprint
            BiometricPrompt.PromptInfo.Builder promptInfo = dialogMetric();
            promptInfo.setDeviceCredentialAllowed(true);

            //activate callback if it succeed
            biometricPrompt.authenticate(promptInfo.build());
        });
    }
    private void Login(){

        String Email = email.getText().toString().trim();
        String Pass = pass.getText().toString().trim();

        if (Email.isEmpty()) {
            email.setError("Enter email!");
            email.requestFocus();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            email.setError("Please valid email!");
            email.requestFocus();
        }
        else if (Pass.isEmpty()) {
            pass.setError("Enter password!");
            pass.requestFocus();
        }

        else if (!(Email.isEmpty() && Pass.isEmpty())) {

            auth.signInWithEmailAndPassword(Email, Pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@Nonnull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        startActivity(new Intent(login.this, commu_map.class));
                    } else {
                        Toast.makeText(login.this, "email or password is incorrect!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(login.this, "something went wrong!", Toast.LENGTH_LONG).show();
        }

    }



    BiometricPrompt.PromptInfo.Builder dialogMetric()
    {
        //Show prompt dialog
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login")
                .setSubtitle("Log in using your biometric credential");
    }

    //must running android 6
    void checkBioMetricSupported()
    {
        BiometricManager manager = BiometricManager.from(this);
        String info="";
        switch (manager.canAuthenticate())
        {
            case BiometricManager.BIOMETRIC_SUCCESS:
                info = "App can authenticate using biometrics.";
                enableButton(true);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                info = "No biometric features available on this device.";
                enableButton(false);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                info = "Biometric features are currently unavailable.";
                enableButton(false);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                info = "Need register at least one finger print";
                enableButton(false,true);
                break;
            default:
                info= "Unknown cause";
                enableButton(false);
        }


   Toast.makeText(login.this,info,Toast.LENGTH_SHORT).show();

    }

    void enableButton(boolean enable)
    {
        //  just enable or disable button
        btn_fp.setEnabled(enable);
        btn_fppin.setEnabled(true);
    }
    void enableButton(boolean enable,boolean enroll)
    {
        enableButton(enable);

        if(!enroll) return;
        // Prompts the user to create credentials that your app accepts.
        //Open settings to set credential fingerprint or PIN
        final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
        enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
        startActivity(enrollIntent);
    }
}





