package com.example.otpgenratation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.sql.Time;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;

public class LoginActivity extends AppCompatActivity {
    private static final int YOUR_API_SITE_KEY = 221;
    EditText phone,otp;
Button btnsend,btnverify;
FirebaseAuth mAuth;
private String verificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phone = (EditText)findViewById(R.id.Phonenumber);
        otp = (EditText)findViewById(R.id.OTP);
        mAuth = FirebaseAuth.getInstance();
        btnsend= (Button)findViewById(R.id.sendotp);
        btnverify = (Button)findViewById(R.id.verifyotp);
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String OTP = otp.getText().toString();
                if (TextUtils.isEmpty(phone.getText().toString())){
                    Toast.makeText(LoginActivity.this,"Please enter valid number !!",Toast.LENGTH_SHORT).show();
                }
                else {
                    String Number ="+91"+ phone.getText().toString();
                    sendVerification(Number);
        //            verifyWithRecptcha();
                }
            }


        });
        btnverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(otp.getText().toString())){
                    Toast.makeText(LoginActivity.this,"Please Enter the Valid OTP !!",Toast.LENGTH_SHORT).show();
                }
                else {
                    verifyCode(otp.getText().toString());
                }
            }
        });
    }
/*
    private void verifyWithRecptcha() {
        SafetyNet.getClient(this).verifyWithRecaptcha(String.valueOf(YOUR_API_SITE_KEY))
                .addOnSuccessListener((Executor) this, new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse recaptchaTokenResponse) {
                        String userResponseToken = recaptchaTokenResponse.getTokenResult();
                        if (!userResponseToken.isEmpty()){

                        }
                    }
                });
    }
*/
    private void signInWithCredential(PhoneAuthCredential credential){
    mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent i = new Intent(LoginActivity.this,New.class);
                        startActivity(i);

                    } else {
                    Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
}
private void sendVerification(String number){
    PhoneAuthOptions options =
            PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(number)       // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this)                 // Activity (for callback binding)
                    .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                    .build();
    PhoneAuthProvider.verifyPhoneNumber(options);
}
private PhoneAuthProvider.OnVerificationStateChangedCallbacks
        mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
    @Override
    public void onCodeSent(String s,PhoneAuthProvider.ForceResendingToken forceResendingToken) {
        super.onCodeSent(s,forceResendingToken);
        verificationId = s;
    }

    @Override
    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
        final String code = phoneAuthCredential.getSmsCode();
        if (code!=null){
            otp.setText(code);
            verifyCode(code);
        }
    }

    @Override
    public void onVerificationFailed(@NonNull FirebaseException e) {
Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
    }
};
    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
        signInWithCredential(credential);
    }
}