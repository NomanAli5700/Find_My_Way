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

public class login_vic extends AppCompatActivity {
    Button btn_fp,btn_fppin;
    ImageView img;
    FirebaseAuth auth;
    EditText email2,pass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_vic);

        Boolean e = false, p = false;
        //EditText email2,pass2;
        TextView reg;
        Button btn;


        //
        img=findViewById(R.id.vc);
        btn_fp = findViewById(R.id.btn_fp);
        btn_fppin  = findViewById(R.id.btn_fppin);
        //

        email2=findViewById(R.id.email2);
        pass2=findViewById(R.id.pass2);
        reg=findViewById(R.id.reg2);
        btn=findViewById(R.id.sign2);
        auth = FirebaseAuth.getInstance();

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(login_vic.this,sign_vic.class);
                startActivity(it);

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });

        //create new method to check whether support or not
        checkBioMetricSupported();
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(login_vic.this,
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
                startActivity(new Intent(login_vic.this, organization.class));
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

        String Email = email2.getText().toString().trim();
        String Pass = pass2.getText().toString().trim();

        if (Email.isEmpty()) {
            email2.setError("Enter email!");
            email2.requestFocus();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            email2.setError("Please valid email!");
            email2.requestFocus();
        }
        else if (Pass.isEmpty()) {
            pass2.setError("Enter password!");
            pass2.requestFocus();
        }

        else if (!(Email.isEmpty() && Pass.isEmpty())) {

            auth.signInWithEmailAndPassword(Email, Pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@Nonnull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                startActivity(new Intent(login_vic.this, organization.class));
                            } else {
                                Toast.makeText(login_vic.this, "email or password is incorrect!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(login_vic.this, "something went wrong!", Toast.LENGTH_LONG).show();
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


        Toast.makeText(login_vic.this,info,Toast.LENGTH_SHORT).show();

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





