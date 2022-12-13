package com.example.smixers.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.smixers.models.LessonCategory;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;


import com.example.smixers.R;
import com.example.smixers.utils.*;


import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterListLessonCategory extends FirestoreAdapter<AdapterListLessonCategory.ViewHolder>
{

    public interface OnHouseholdSelectedListener
    {
        void onHouseholdSelected(DocumentSnapshot household);
    }


    private OnHouseholdSelectedListener mListener;
    private String mSearchQuery;


    public AdapterListLessonCategory(Query query, OnHouseholdSelectedListener listener)
    {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Log.w("ListCreated","listCretaed4");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_lesson_category, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Log.w("ListCreated","listCretaed7");
        DocumentSnapshot snapshot = getSnapshot(position);
        holder.bind(snapshot, mListener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.image)
        ImageView itemImageView;

        @BindView(R.id.title)
        TextView itemNameView;

        @BindView(R.id.brief)
        TextView itemIdView;




        public ViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot, final OnHouseholdSelectedListener listener)
        {
            Log.w("ListCreated","listCretaed5");
            LessonCategory household = snapshot.toObject(LessonCategory.class);
            if(household == null)
                return;

            // Load image if available
            ImageManager.loadThumbImageView(itemView.getContext(), household.getLessonId(), itemImageView);

            String sHouseholdId = Utils.getHouseholdId(household.getLessonId(), "-");
            itemIdView.setText(sHouseholdId);

            itemNameView.setText(household.getTitle());


            // Click listener
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (listener != null)
                    {
                        listener.onHouseholdSelected(snapshot);
                    }
                }
            });
        }

    }
}
