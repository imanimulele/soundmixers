package com.example.smixers.lessons;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.smixers.R;
import com.example.smixers.models.Chat;
import com.example.smixers.models.ChatHolder;
import com.example.smixers.utils.Utils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CourseCategories#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseCategories extends Fragment  implements FirebaseAuth.AuthStateListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
//
//    TextView emptyTextView,messageEdit;
    LinearLayoutManager manager;



    @BindView(R.id.messagesList)
    RecyclerView messagesList;

    @BindView(R.id.emptyTextView)
    TextView emptyTextView;

    private View rootView = null;
    private Unbinder unbinder = null;

    private static final CollectionReference sChatCollection =
            FirebaseFirestore.getInstance().collection("lessonCategories");
    /** Get the last 50 chat messages ordered by timestamp . */
    private static final Query sChatQuery =
            sChatCollection.orderBy("classId", Query.Direction.DESCENDING).limit(50);

    static {
        FirebaseFirestore.setLoggingEnabled(true);
    }

    public CourseCategories() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CourseCategories.
     */
    // TODO: Rename and change types and number of parameters
    public static CourseCategories newInstance(String param1, String param2) {
        CourseCategories fragment = new CourseCategories();
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
        return inflater.inflate(R.layout.fragment_course_categories, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView = view;

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

        RecyclerView recyclerView = rootView.findViewById(R.id.messagesList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        recyclerView.addOnLayoutChangeListener((rootView, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                recyclerView.postDelayed(() -> recyclerView.smoothScrollToPosition(
                        0), 100);
            }
        });


        unbinder = ButterKnife.bind(this, view);



    }


    @Override
    public void onStart() {
        super.onStart();
        attachRecyclerViewAdapter();
        if (isSignedIn()) {

        }
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    private boolean isSignedIn() {

        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }


    @Override
    public void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    private void attachRecyclerViewAdapter() {


        final RecyclerView.Adapter adapter = newAdapter();

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                messagesList.smoothScrollToPosition(0);
            }
        });



        messagesList.setAdapter(adapter);
    }

    private RecyclerView.Adapter newAdapter() {


        FirestoreRecyclerOptions<Chat> options =
                new FirestoreRecyclerOptions.Builder<Chat>()
                        .setQuery(sChatQuery, Chat.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirestoreRecyclerAdapter<Chat, ChatHolder>(options) {
            @NonNull
            @Override
            public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ChatHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_2, parent, false));
            }


            @Override
            public void onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
                emptyTextView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            protected void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull Chat model) {
                holder.bind(model);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("message", model.getMessage());
                        bundle.putString("name",model.getName());
                        bundle.putString("categoryId",model.getCategory_id());
                        Log.w("CategoryIDname",model.getCategory_id());
                       Utils.navigateToFragment(Navigation.findNavController(rootView), R.id.action_nav_inbox_to_nav_classes, bundle, getActivity());
                    }
                });
            }
        };
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

    }
}