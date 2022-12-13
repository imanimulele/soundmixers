package com.example.smixers.lessons;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smixers.R;
import com.example.smixers.models.Chat;
import com.example.smixers.utils.ViewAnimation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LessonArticle#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LessonArticle extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "categoryId";
    private static final String ARG_PARAM2 = "docId";
    private static final String ARG_PARAM3 = "message";
    private static final String ARG_PARAM4 = "name";
    private static final String ARG_PARAM5="description";
    private static final String ARG_PARAM6="classId";

    private static final CollectionReference sChatCollection =
            FirebaseFirestore.getInstance().collection("courseList");
    /** Get the last 50 chat messages ordered by timestamp . */
//    private static final Query sChatQuery =
//            sChatCollection.orderBy("classId", Query.Direction.DESCENDING).limit(50);
private String TAG="LessonA";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;
    private String mParam4;
    private String mParam5;
    private String mParam6;

    private String contents;

    TextView txt_lesson_title;
    TextView txt_lesson_description;
    ImageView txt_lesson_image;
    TextView txt_lesson_subtitle;
    WebView webView;
    private final static int LOADING_DURATION = 500;

    public LessonArticle() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LessonArticle.
     */
    // TODO: Rename and change types and number of parameters
    public static LessonArticle newInstance(String param1, String param2, String param3, String param4,String param5,String param6) {
        LessonArticle fragment = new LessonArticle();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5,param5);
        args.putString(ARG_PARAM6,param6);



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
            mParam5=getArguments().getString(ARG_PARAM5);
            mParam6=getArguments().getString(ARG_PARAM6);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getData();

        Log.w("SeeContentsw",mParam6);
    }

    private void getData() {

        Query sChatQuery = sChatCollection.whereEqualTo("classId", mParam6);//orderBy("classId",Query.Direction.ASCENDING).limit(50);



        sChatQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for (DocumentSnapshot d : list) {

                        contents=d.getString("description");


                    }
                }

            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.web_view_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final LinearLayout lyt_progress = view.findViewById(R.id.lyt_progress);
        lyt_progress.setVisibility(View.VISIBLE);
        lyt_progress.setAlpha(1.0f);

        Query sChatQuery = sChatCollection.whereEqualTo("classId", mParam6);//orderBy("classId",Query.Direction.ASCENDING).limit(50);

        Log.w("SeeContentsw",mParam1);






//        txt_lesson_description=view.findViewById(R.id.lesson_description);
//        txt_lesson_title=view.findViewById(R.id.lesson_title);
//        txt_lesson_subtitle=view.findViewById(R.id.lesson_subtitle);
//
//        String newDesc = mParam5.replace("_n","<br>");
//        txt_lesson_title.setText(mParam4);
//        txt_lesson_subtitle.setText(mParam3);
      // txt_lesson_description.setText(newDesc);
//
//        TextView questionValue = (TextView) findViewById(R.layout.TextView01);
///        questionValue.setTypeface(null, Typeface.BOLD);


        //txt_lesson_description.setText(HtmlCompat.fromHtml(newDesc, HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH));


//        Spanned styledText = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            styledText = Html.fromHtml(newDesc);
//        }
//
//        Log.w("ValuesImaniSpanned",styledText.toString());
//        txt_lesson_description.setText(styledText);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewAnimation.fadeOut(lyt_progress);
            }
        }, LOADING_DURATION);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                sChatQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : list) {

                                contents=d.getString("description");

                                webView=view.findViewById(R.id.web_desctip);
                                //webView.loadData(mParam5, "text/html", "utf-8");
//        WebSettings webSettings = webView.getSettings();
//        webSettings.setJavaScriptEnabled(true);


                                webView.clearView();
                                webView.setWebViewClient(new WebViewClient());
                                webView.getSettings().setJavaScriptEnabled(true);
                                //webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                                webView.getSettings().setPluginState(WebSettings.PluginState.ON);
                                webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
                                //webView.setWebChromeClient(new WebChromeClient());
                                webView.loadData(contents, "text/html", "utf-8");

                                Log.w("SeeContentsw",contents);

                            }
                        }

                    }
                });

            }
        }, LOADING_DURATION + 200);


    }


    class ImageGetterAsyncTask extends AsyncTask<TextView, Void, Bitmap> {

        private LevelListDrawable levelListDrawable;
        private Context context;
        private String source;
        private TextView t;

        public ImageGetterAsyncTask(Context context, String source, LevelListDrawable levelListDrawable) {
            this.context = context;
            this.source = source;
            this.levelListDrawable = levelListDrawable;
        }

        @Override
        protected Bitmap doInBackground(TextView... params) {
            t = params[0];
            try {
                Log.d(TAG, "Downloading the image from: " + source);

                return Picasso.with(context).load(source).get();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final Bitmap bitmap) {
            try {
                Drawable d = new BitmapDrawable(context.getResources(), bitmap);
                Point size = new Point();
                ((Activity) context).getWindowManager().getDefaultDisplay().getSize(size);
                // Lets calculate the ratio according to the screen width in px
                int multiplier = size.x / bitmap.getWidth();
                Log.d(TAG, "multiplier: " + multiplier);
                levelListDrawable.addLevel(1, 1, d);
                // Set bounds width  and height according to the bitmap resized size
                levelListDrawable.setBounds(0, 0, bitmap.getWidth() * multiplier, bitmap.getHeight() * multiplier);
                levelListDrawable.setLevel(1);
                t.setText(t.getText()); // invalidate() doesn't work correctly...
            } catch (Exception e) { /* Like a null bitmap, etc. */ }
        }
    }

}