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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {
    LinearLayout layout_login;
    private String TAG="RegisterUsers";

    private FirebaseAuth mAuth;
    private final static int LOADING_DURATION = 2000;
    TextInputEditText txtUsername,txtPassword,txtConfirmPassword;
    Button btnUserRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        txtConfirmPassword=findViewById(R.id.textPasswordConfirm);
        txtPassword=findViewById(R.id.textPassword);
        txtUsername=findViewById(R.id.text_username);

        btnUserRegister=findViewById(R.id.btnRegister);

        layout_login=findViewById(R.id.layout_login);
        layout_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_register=new Intent(Register.this,MainActivity.class);
                startActivity(intent_register);
            }
        });

        if (txtPassword.getText().toString().equals(txtConfirmPassword.getText().toString()))

        {


        }else {
            Toast.makeText(getApplicationContext(), "Password doenot match", Toast.LENGTH_LONG).show();

        }

        btnUserRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(txtUsername.getText().toString().isEmpty()||txtPassword.getText().toString().isEmpty()){



                    Toast.makeText(getApplicationContext(),"Please fill the details",Toast.LENGTH_LONG).show();
                }else{
                    if(txtPassword.getText().toString().equals(txtConfirmPassword.getText().toString())){
                        loadingAndDisplayContent();
                    }else{

                        Toast.makeText(getApplicationContext(),"Password not match",Toast.LENGTH_LONG).show();
                    }

                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

        }
    }

    //for progress activity *Mulele
    private void loadingAndDisplayContent() {
        btnUserRegister.setEnabled(false);
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
                mailSignUp();
                btnUserRegister.setEnabled(true);
            }
        }, LOADING_DURATION + 400);
    }


    public void mailSignUp(){


            mAuth.createUserWithEmailAndPassword(txtUsername.getText().toString(), txtPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                Intent intent=new Intent(Register.this,MainActivity.class);
                                startActivity(intent);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(Register.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });





    }

}