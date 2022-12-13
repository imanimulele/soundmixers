package com.example.smixers.lessons;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.smixers.R;
import com.example.smixers.models.Chat;
import com.example.smixers.models.ChatHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class Mylessons extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private String TAG="MyLessons";
    private Button sendButton;
    TextView emptyTextView,messageEdit;
    private RecyclerView messagesList;
    LinearLayoutManager manager;


    private static final CollectionReference sChatCollection =
            FirebaseFirestore.getInstance().collection("chats");
    /** Get the last 50 chat messages ordered by timestamp . */
    private static final Query sChatQuery =
            sChatCollection.orderBy("timestamp", Query.Direction.DESCENDING).limit(50);

    static {
        FirebaseFirestore.setLoggingEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylessons);

        messagesList=findViewById(R.id.messagesList);
        sendButton=findViewById(R.id.sendButton);
        emptyTextView=findViewById(R.id.emptyTextView);
        messageEdit=findViewById(R.id.messageEdit);

        manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

        messagesList.setHasFixedSize(true);
        messagesList.setLayoutManager(manager);

    }


    @Override
    public void onStart() {
        super.onStart();

        if (isSignedIn()) {
            attachRecyclerViewAdapter();
        }
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    private boolean isSignedIn() {

        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }


    @Override
    protected void onStop() {
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
                        .inflate(R.layout.message, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull Chat model) {
                holder.bind(model);
            }

            @Override
            public void onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
                emptyTextView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        };
    }




    private void onAddMessage(Chat chat) {
        sChatCollection.add(chat).addOnFailureListener(this,
                e -> Log.e(TAG, "Failed to write message", e));
    }





    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

    }
}