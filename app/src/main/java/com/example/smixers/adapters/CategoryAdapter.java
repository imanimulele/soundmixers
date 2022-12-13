package com.example.smixers.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smixers.R;
import com.example.smixers.models.LessonCategory;

import com.example.smixers.models.LessonCats;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class CategoryAdapter extends FirestoreRecyclerAdapter<LessonCats, CategoryAdapter.LessonHolder> {

    private OnItemClickListener listener;

    public CategoryAdapter(@NonNull FirestoreRecyclerOptions<LessonCats> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull LessonHolder holder, @SuppressLint("RecyclerView") int position, @NonNull LessonCats model) {

        holder.mCategoryName.setText(model.getName());
        holder.mTextField.setText(model.getMessage());
        holder.mTextClass.setText(model.getClassId());

    }



    @NonNull
    @Override
    public LessonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cate_mate,parent,false);

        return new LessonHolder(view);
    }


    class LessonHolder extends RecyclerView.ViewHolder{
         TextView mCategoryName;
         TextView mTextField;
         TextView mTextClass;
        public View lyt_parent;

        public LessonHolder(@NonNull View itemView) {
            super(itemView);
            mCategoryName = itemView.findViewById(R.id.text_view_title);
            mTextField = itemView.findViewById(R.id.text_view_description);
            mTextClass=itemView.findViewById(R.id.text_view_classId);
            lyt_parent = itemView.findViewById(R.id.lyt_parent);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
