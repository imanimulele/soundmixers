package com.example.smixers.adapters;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * RecyclerView adapter for displaying the results of a Firestore {@link Query}.
 *
 * Note that this class forgoes some efficiency to gain simplicity. For example, the result of
 * {@link DocumentSnapshot#toObject(Class)} is not cached so the same object may be deserialized
 * many times as the user scrolls.
 */
public abstract class FirestoreAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>
        implements EventListener<QuerySnapshot>
{

    private static final String TAG = "FirestoreAdapter";

    private Query mQuery;
    private ListenerRegistration mRegistration;

    protected ArrayList<DocumentSnapshot> mSnapshots = new ArrayList<>();
    //protected boolean mSkipInsertNotifications = false;

    public FirestoreAdapter(Query query) {
        mQuery = query;
    }



    @Override
    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e)
    {
        if (e != null)
        {
            Log.w(TAG, "onEvent error.", e);
            onError(e);
            return;
        }

        // Dispatch the event
        //Log.d(TAG, "onEvent numChanges:" + documentSnapshots.getDocumentChanges().size());
        for (DocumentChange change : documentSnapshots.getDocumentChanges())
        {
            try
            {
                switch (change.getType()) {
                    case ADDED:
                        onDocumentAdded(change);
                        break;
                    case MODIFIED:
                        onDocumentModified(change);
                        break;
                    case REMOVED:
                        onDocumentRemoved(change);
                        break;
                }
            }
            catch (Exception ex)
            {
                String errMessage = "Doc change error - chgType: " + change.getType() + ", docId: " + change.getDocument().getId() +
                        ", oldIndex: " + change.getOldIndex() + ", newIndex: " + change.getNewIndex();
                Log.w(TAG, errMessage, ex);
            }
        }

        onDataChanged();
    }

    public void startListening()
    {

        if (mQuery != null && mRegistration == null)
        {
            mRegistration = mQuery.addSnapshotListener(this);
        }
    }

    public void stopListening()
    {


        if (mRegistration != null)
        {
            mRegistration.remove();
            mRegistration = null;
        }

        mSnapshots.clear();
        notifyDataSetChanged();
    }

    public void setQuery(Query query)
    {
        // Stop listening
        stopListening();

        // Clear existing data
        mSnapshots.clear();
        notifyDataSetChanged();

        // Listen to new query
        mQuery = query;
        startListening();
    }

    @Override
    public int getItemCount()
    {
        return mSnapshots.size();
    }

    protected DocumentSnapshot getSnapshot(int index)
    {
        return mSnapshots.get(index);
    }

    protected void onDocumentAdded(DocumentChange change)
    {
        if(change.getNewIndex() <= mSnapshots.size())
        {
            mSnapshots.add(change.getNewIndex(), change.getDocument());

            //if(!mSkipInsertNotifications)
            notifyItemInserted(change.getNewIndex());
        }
    }

    protected void onDocumentModified(DocumentChange change)
    {
        if (change.getOldIndex() == change.getNewIndex())
        {
            // Item changed but remained in same position
            if(change.getOldIndex() < mSnapshots.size())
            {
                mSnapshots.set(change.getOldIndex(), change.getDocument());
                notifyItemChanged(change.getOldIndex());
            }
        }
        else
        {
            // Item changed and changed position
            if(change.getOldIndex() < mSnapshots.size())
                mSnapshots.remove(change.getOldIndex());

            if(change.getNewIndex() <= mSnapshots.size())
            {
                mSnapshots.add(change.getNewIndex(), change.getDocument());
                notifyItemMoved(change.getOldIndex(), change.getNewIndex());
            }
        }
    }

    protected void onDocumentRemoved(DocumentChange change)
    {
        if(change.getOldIndex() < mSnapshots.size())
        {
            mSnapshots.remove(change.getOldIndex());
            notifyItemRemoved(change.getOldIndex());
        }
    }

    protected void onError(FirebaseFirestoreException e)
    {
        Log.w(TAG, "onError", e);
    }

    protected void onDataChanged()
    {

    }

}

