package com.example.smixers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.smixers.models.AboutUs;
import com.example.smixers.utils.AppSettings;
import com.example.smixers.utils.ViewAnimation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MainActivity extends AppCompatActivity {
LinearLayout layout_register;
TextView txt_forgotPassword;
Button btnUserSign;
    private final static int LOADING_DURATION = 2000;
private String TAG="MainActivityLogs";

// firebase attributes

    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db ;

    private GoogleSignInClient mGoogleSignInClient;

    //ui components

//    MaterialButton btn_google;

    TextInputEditText textPassword,textUsername;

    private static final CollectionReference sChatCollection =
            FirebaseFirestore.getInstance().collection("about_us");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        db = FirebaseFirestore.getInstance();

        layout_register=findViewById(R.id.layout_register);
        txt_forgotPassword=findViewById(R.id.text_forgot_password);
        //btn_google=findViewById(R.id.btn_googleSign);
        btnUserSign=findViewById(R.id.btnLogin);

//        btn_google.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                googleLogin();
//            }
//        });

        //firebase initialized
        mAuth = FirebaseAuth.getInstance();

        layout_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_register=new Intent(MainActivity.this,Register.class);
                startActivity(intent_register);
            }
        });
        txt_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentForgotPassword=new Intent(MainActivity.this,ForgotPassword.class);
                startActivity(intentForgotPassword);
            }
        });


        textPassword=findViewById(R.id.txtPassword);
        textUsername=findViewById(R.id.txtUsername);

        btnUserSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                loadingAndDisplayContent();
            }
        });

    }

    //for progress activity *Mulele
    private void loadingAndDisplayContent() {

        if(textUsername.getText().toString().isEmpty()||textPassword.getText().toString().isEmpty()){

            Toast.makeText(getApplicationContext(),"Please fill details ",Toast.LENGTH_SHORT).show();

        }else{

            btnUserSign.setEnabled(false);
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



                    emailLogin();


                    btnUserSign.setEnabled(true);
                }
            }, LOADING_DURATION + 400);
        }



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // [START handleSignInResult]
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.


            updateUI(account);


        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);

        }
    }
    // [END handleSignInResult]

//    @Override
//    protected void onStart() {
//        super.onStart();
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        currentUser = mAuth.getCurrentUser();
//
//        if(currentUser != null){
//           Intent intent=new Intent(MainActivity.this,MainPage.class);
//           startActivity(intent);
//        }
//
//    }

    public void emailLogin(){



            mAuth.signInWithEmailAndPassword(textUsername.getText().toString(), textPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                aboutUs();
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUser(user);


                            } else {

                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });



    }

    private void aboutUs() {

        sChatCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for (DocumentSnapshot d : list) {
                        AboutUs aboutUs=d.toObject(AboutUs.class);


                        Log.w("companyAddress",aboutUs.getCompanyAddress());

                        AppSettings.setComapanyVision(d.getString("comapanyVision"));
                        AppSettings.setAboutUstitle(d.getString("aboutUstitle"));
                        AppSettings.setCompanyAddress(d.getString("companyAddress"));

                        // contents=d.getString("description");





                    }
                }

            }
        });

    }

    private void updateUser(FirebaseUser user) {


        if (user != null) {

            getUserData();
            AppSettings.setUserEmail(user.getEmail());
            AppSettings.setUserName(user.getDisplayName());
            AppSettings.setUserID(user.getUid());

            Intent intent =new Intent(MainActivity.this,MainPage.class);
            intent.putExtra("accountName",user.getDisplayName());
            intent.putExtra("userEmail",user.getEmail());
            intent.putExtra("imageUrl",String.valueOf(user.getPhotoUrl()));

            Log.w("UserIdImani", String.valueOf(user.getUid()));
            startActivity(intent);

        } else {

        }
    }

    public void googleLogin(){

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {

        if (account != null) {

            getUserData();

            Intent intent =new Intent(MainActivity.this,MainPage.class);
            intent.putExtra("accountName",account.getDisplayName());
            intent.putExtra("userID",account.getId());
            intent.putExtra("userEmail",account.getEmail());
            intent.putExtra("imageUrl",String.valueOf(account.getPhotoUrl()));

            AppSettings.setUserEmail(account.getEmail());
            AppSettings.setUserName(account.getDisplayName());
            AppSettings.setUserImage(String.valueOf(account.getPhotoUrl()));
            AppSettings.setUserMobile(String.valueOf(account.getAccount()));
            Log.w("UserNameUse", String.valueOf(account.getId()));
            startActivity(intent);

        } else {

        }

    }

    //user this to set AppSettings
    public void getUserData(){

        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for (DocumentSnapshot d : list) {
                      //  AppSettings appSettings=d.toObject(AppSettings.class);

                        String doc_ID=d.getString("firstName");

                        AppSettings.setUserName(d.getString("firstName"));
                        AppSettings.setLocation(d.getString("city"));
                        AppSettings.setUserMobile(d.getString("mobileNumber"));
                        AppSettings.setUserImage(d.getString("image"));
                        AppSettings.setUserTitle(d.getString("userTitle"));

                        Log.d("DocID Meter",doc_ID);

                    }


                }

            }
        });

//        db.collection("users")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//
//
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                AppSettings appSettings=new AppSettings();
//
//
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
//                        }
//                    }});
    }

}