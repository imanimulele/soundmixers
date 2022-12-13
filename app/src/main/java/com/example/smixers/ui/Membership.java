package com.example.smixers.ui;

import static android.app.Activity.RESULT_OK;

import static com.example.smixers.utils.FirestoreManager.getFirestoreInstance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.smixers.R;
import com.example.smixers.utils.AppSettings;
import com.example.smixers.utils.ViewAnimation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Membership#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Membership extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "userName";
    private static final String ARG_PARAM2 = "mobileNumber";
    private static final String ARG_PARAM3 = "location";
    private static final String ARG_PARAM4 = "userTitle";
  //  private static final String ARG_PARAM5   = "userImage";

    FirebaseFirestore db ;

    private String TAG="Membership";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;
    private String mParam4;
    private String mParam5;

    int SELECT_PICTURE = 200;
    private CircularImageView profileImage;
    private int THUMBNAIL_SIZE=150;
    private  String imageString;

    private View rootView=null;

    TextView txtFirstname;
    TextView txtMiddleName;
    TextView txtLastName;
    TextView txtMobileNumber;
    TextView txtAddress;
    TextView txtCounty;
    TextView txtCity;
    TextView txtState;
    TextView txtTitle;
    TextView txtUsername;
    FloatingActionButton btnImage;
    MaterialRippleLayout materialRippleLayout;
   LinearLayout lyt_progress;
    private final static int LOADING_DURATION = 2000;

    public Membership() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Membership.
     */
    // TODO: Rename and change types and number of parameters
    public static Membership newInstance(String param1, String param2,String param3,String param4,String param5) {
        Membership fragment = new Membership();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getString(ARG_PARAM4);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_membership, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        rootView=view;

        btnImage=rootView.findViewById(R.id.changeImageProfile);
        profileImage=rootView.findViewById(R.id.imageview_account_profile);
        txtFirstname=rootView.findViewById(R.id.firstName);
        txtMiddleName=rootView.findViewById(R.id.middleName);
        txtLastName=rootView.findViewById(R.id.lastName);
        txtMobileNumber=rootView.findViewById(R.id.mobileNumber);
        txtAddress=rootView.findViewById(R.id.address);
        txtCounty=rootView.findViewById(R.id.country);
        txtCity=rootView.findViewById(R.id.city);
        txtState=rootView.findViewById(R.id.state);
        materialRippleLayout=rootView.findViewById(R.id.lyt_next);
        lyt_progress =rootView.findViewById(R.id.lyt_progress);
        txtTitle=rootView.findViewById(R.id.userTitle);
        txtUsername=rootView.findViewById(R.id.userName);

        txtUsername.setText(mParam1);
        txtMobileNumber.setText(mParam2);
        txtAddress.setText(mParam3);
        txtTitle.setText(mParam4);


//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        byte[] imageBytes = baos.toByteArray();
//
//
//
//        //decode base64 string to image
//        imageBytes = Base64.decode(mParam5, Base64.DEFAULT);
//        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//        profileImage.setImageBitmap(decodedImage);



        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageChooser();

            }
        });

        materialRippleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingAndDisplayContent();
            }
        });

    }




    public void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout

                    Uri pickedImage = data.getData();

                    try {
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), pickedImage);
//                        Log.w("IMAGESTRING", String.valueOf(bitmap));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Bitmap bitmap = getThumbnail(pickedImage);

                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                         imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);



                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                    profileImage.setImageURI(selectedImageUri);
                }
            }
        }



    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException{
        InputStream input = getContext().getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//
        input = getContext().getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }


    public void addProfile(){

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("firstName", txtFirstname.getText().toString());
        user.put("lastName", txtLastName.getText().toString());
        user.put("middleName", txtMiddleName.getText().toString());
        user.put("mobileNumber", txtMobileNumber.getText().toString());
        user.put("address", txtAddress.getText().toString());
        user.put("country", txtCounty.getText().toString());
        user.put("city", txtCity.getText().toString());
        user.put("state", txtState.getText().toString());
        user.put("image",imageString);
        user.put("userTitle",txtTitle.getText().toString());
        user.put("userID",AppSettings.getUserID());
        user.put("userName",AppSettings.getUserName());


        checkUserExist();

        DocumentReference docRef = getFirestoreInstance().collection("users").document(AppSettings.getUserID());

        docRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                AppSettings.setUserMobile(txtMobileNumber.getText().toString());
                AppSettings.setUserName(txtUsername.getText().toString());



            }
        });



    }

    private void checkUserExist() {


        DocumentReference docRef = db.collection("users").document(AppSettings.getUserID());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        updateData();
                        Log.d(TAG, AppSettings.getUserID());

                    } else {
                        addProfile();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void updateData() {


        // Create a new user with a first and last name
        DocumentReference user = db.collection("users").document(AppSettings.getUserID());
        user.update("firstName", txtFirstname.getText().toString());
        user.update("lastName", txtLastName.getText().toString());
        user.update("middleName", txtMiddleName.getText().toString());
        user.update("mobileNumber", txtMobileNumber.getText().toString());
        user.update("address", txtAddress.getText().toString());
        user.update("country", txtCounty.getText().toString());
        user.update("city", txtCity.getText().toString());
        user.update("state", txtState.getText().toString());
        user.update("image",imageString);
        user.update("userID",AppSettings.getUserID());
        user.update("userTitle",txtTitle.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(),"User Updated",Toast.LENGTH_SHORT).show();


            }
        });



    }

    //for progress activity *Mulele
    private void loadingAndDisplayContent() {
        materialRippleLayout.setEnabled(false);
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
                checkUserExist();
                materialRippleLayout.setEnabled(true);
            }
        }, LOADING_DURATION + 400);
    }
}