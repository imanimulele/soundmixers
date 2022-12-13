package com.example.smixers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.smixers.utils.ViewAnimation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    private String TAG="ForgotPassword";
    LinearLayout layout_login;
    TextInputEditText textUser;
    Button btnReset;
    private FirebaseAuth mAuth;
    private final static int LOADING_DURATION = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
         textUser=findViewById(R.id.txtUsernamePassword);
         btnReset=findViewById(R.id.btnResetPassword);
         mAuth=FirebaseAuth.getInstance();

        layout_login=findViewById(R.id.layout_login);
        layout_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_register=new Intent(ForgotPassword.this,MainActivity.class);
                startActivity(intent_register);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingAndDisplayContent();
            }
        });


    }

    //for progress activity *Mulele
    private void loadingAndDisplayContent() {

        if(textUser.getText().toString().isEmpty()){

            Toast.makeText(getApplicationContext(),"Email is Requiered",Toast.LENGTH_SHORT).show();
        }else{

            btnReset.setEnabled(false);
            final LinearLayout lyt_progress = findViewById(R.id.lyt_progress);
            lyt_progress.setVisibility(View.VISIBLE);
            lyt_progress.setAlpha(1.0f);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ViewAnimation.fadeOut(lyt_progress);

                }
            }, LOADING_DURATION);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    resetPassword();
                    btnReset.setEnabled(true);
                }
            }, LOADING_DURATION + 400);
        }


    }

    private void resetPassword() {
String emailAddress=textUser.getText().toString();
        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                           Intent intent =new Intent(ForgotPassword.this, MainActivity.class);
                           startActivity(intent);
                        }else{

                            Log.d(TAG, "Email failed sent.");
                        }
                    }
                });

    }
}