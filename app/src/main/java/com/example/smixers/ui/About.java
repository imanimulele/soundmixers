package com.example.smixers.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.example.smixers.R;
import com.example.smixers.models.AboutUs;
import com.example.smixers.utils.AppSettings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link About#newInstance} factory method to
 * create an instance of this fragment.
 */
public class About extends Fragment {



    private String TAG="AboutUs";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Create a reference to the cities collection
    CollectionReference sChatCollection = db.collection("about_us");




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View rootView = null;




    public About() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment About.
     */
    // TODO: Rename and change types and number of parameters
    public static About newInstance(String param1, String param2) {
        About fragment = new About();
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
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;




        WebView webView=rootView.findViewById(R.id.text_mission);
        WebView webAdress=rootView.findViewById(R.id.text_address);

        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadData(AppSettings.getComapanyVision(), "text/html", "utf-8");

        webAdress.setWebViewClient(new WebViewClient());
        webAdress.getSettings().setJavaScriptEnabled(true);
        webAdress.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webAdress.getSettings().setPluginState(WebSettings.PluginState.ON);
        webAdress.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webAdress.setWebChromeClient(new WebChromeClient());
        webAdress.loadData(AppSettings.getCompanyAddress(), "text/html", "utf-8");

sChatCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
    @Override
    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

        if (!queryDocumentSnapshots.isEmpty()) {
            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

            for (DocumentSnapshot d : list) {
                AboutUs aboutUs=d.toObject(AboutUs.class);

                Log.d("MembershipId",aboutUs.getComapanyVision());



//        WebSettings webSettings = webView.getSettings();
//        webSettings.setJavaScriptEnabled(true);



            }


        }

    }
});

    }
}