package com.example.smixers.models;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smixers.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CatHolder extends RecyclerView.ViewHolder {
    private final TextView mNameField;
    private final TextView mTextField;


    public CatHolder(@NonNull View itemView) {
        super(itemView);
        mNameField = itemView.findViewById(R.id.title);
        mTextField = itemView.findViewById(R.id.brief);

    }

    public void bind(@NonNull AbstractLesson chat) {
        setName(chat.getName());
        setMessage(chat.getMessage());

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    private void setName(@Nullable String name) {
        mNameField.setText(name);
    }

    private void setMessage(@Nullable String text) {
        mTextField.setText(text);
    }



}
