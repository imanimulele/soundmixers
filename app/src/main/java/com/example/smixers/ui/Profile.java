package com.example.smixers.ui;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.smixers.R;
import com.example.smixers.models.UserProfile;
import com.example.smixers.utils.AppSettings;
import com.example.smixers.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import com.example.smixers.utils.AppSettings;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private   String firstName;
    private  String lastName;
    private  String middleName;
    private  String mobileNumber;
    private  String address;
    private  String country;
    private  String city;
    private  String state;
    private  String image;
    private  String userTitle;

    private String TAG="PROFILE_PAGe";

    FirebaseFirestore db ;

   @BindView(R.id.profile_image)
   CircularImageView userProfileImage;
   FloatingActionButton editProfile;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View rootView = null;
    private NavController navController;
    UserProfile profile;


    public Profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @SuppressLint("WrongThread")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Log.w(TAG, "Listen failed. 2");
        rootView = view;
        userProfileImage = rootView.findViewById(R.id.profile_image);
        TextView userName=rootView.findViewById(R.id.text_username);
        TextView userMail=rootView.findViewById(R.id.text_userEmail);

        TextView txtLocation=rootView.findViewById(R.id.txt_location);
        editProfile=rootView.findViewById(R.id.fab_editMembership);
        TextView txtMobileNumber=rootView.findViewById(R.id.mobileNumber);
        TextView txtUser=rootView.findViewById(R.id.userTitle);
        TextView userAdd=rootView.findViewById(R.id.txtState);
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);

        db = FirebaseFirestore.getInstance();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] imageBytes = baos.toByteArray();




            //userProfileImage.setImageDrawable(Resources.getSystem().getDrawable(R.drawable.upeo));

            //decode base64 string to image
            imageBytes = Base64.decode(AppSettings.getUserImage(), Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            userProfileImage.setImageBitmap(decodedImage);





//        Picasso.with(getContext())
//                .load(AppSettings.getUserImage())
//                .resize(800, 800).error(R.drawable.ic_error)
//                .placeholder(R.drawable.photo_male_1).into(decodedImage);

        userName.setText(AppSettings.getUserName());
        userMail.setText(AppSettings.getUserEmail());
        txtMobileNumber.setText(AppSettings.getUserMobile());
        txtLocation.setText(AppSettings.getLocation());
        txtUser.setText(AppSettings.getUserTitle());


        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle=new Bundle();
                bundle.putString("userName",AppSettings.getUserName());
                bundle.putString("mobileNumber",AppSettings.getUserMobile());
                bundle.putString("location",AppSettings.getLocation());
                bundle.putString("userTitle",AppSettings.getUserTitle());
                //bundle.putString("userImage",AppSettings.getUserImage());

                Utils.navigateToFragment(navController, R.id.nav_membership, bundle, getActivity());


            }
        });


    }


}