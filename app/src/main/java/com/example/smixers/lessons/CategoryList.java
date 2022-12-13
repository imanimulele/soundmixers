
package com.example.smixers.lessons;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smixers.R;
import com.example.smixers.adapters.CategoryAdapter;
import com.example.smixers.models.LessonCats;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CategoryList extends AppCompatActivity{



    private String TAG="MyLessons";
    private Button sendButton;
    TextView emptyTextView,messageEdit;
    private RecyclerView messagesList;
    LinearLayoutManager manager;


    private static final CollectionReference sChatCollection =
            FirebaseFirestore.getInstance().collection("lessonCategories");
    /** Get the last 50 chat messages ordered by timestamp . */
    private static final Query sChatQuery =
            sChatCollection.orderBy("classId", Query.Direction.ASCENDING).limit(50);

    static {
        FirebaseFirestore.setLoggingEnabled(true);
    }




    private CategoryAdapter adapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("lessonCategories");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        setUpRecycleview();


    }

    private void setUpRecycleview() {


        Query query = notebookRef.orderBy("classId", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<LessonCats> options = new FirestoreRecyclerOptions.Builder<LessonCats>()
                .setQuery(query, LessonCats.class)
                .build();

        adapter = new CategoryAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                LessonCats note = documentSnapshot.toObject(LessonCats.class);

                Bundle bundle = new Bundle();
                bundle.putString("classId", note.getClassId());
                bundle.putString("message", note.getMessage());
                bundle.putString("name",note.getName());
                bundle.putString("docId",documentSnapshot.getId());
                Toast.makeText(CategoryList.this,
                        "Position: " + position + " ID: " + note.getClassId(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}