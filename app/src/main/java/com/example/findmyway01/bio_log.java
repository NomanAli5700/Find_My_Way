package com.example.findmyway01;

import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class bio_log extends AppCompatActivity {

    Button btn_fp,btn_fppin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio_log);
        btn_fp = findViewById(R.id.btn_fp);
        btn_fppin  = findViewById(R.id.btn_fppin);

        //create new method to check whether support or not
        checkBioMetricSupported();
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(bio_log.this,
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
        //perform action button only fingerprint
        btn_fp.setOnClickListener(view -> {

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

        //set message to text view so we can see what happen with sensor device
        TextView txinfo =  findViewById(R.id.tx_info);
        txinfo.setText(info);
    }

    void enableButton(boolean enable)
    {
        //just enable or disable button
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
