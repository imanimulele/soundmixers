package com.example.smixers.models;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smixers.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CourseHolder extends RecyclerView.ViewHolder {
    private final TextView mNameField;
    private final TextView mTextField;
    private final FrameLayout mLeftArrow;

    private final RelativeLayout mMessageContainer;
    private final LinearLayout mMessage;
    private final int mGreen300;
    private final int mGray300;

    public CourseHolder(@NonNull View itemView) {
        super(itemView);
        mNameField = itemView.findViewById(R.id.name_text);
        mTextField = itemView.findViewById(R.id.message_text);
        mLeftArrow = itemView.findViewById(R.id.left_arrow);

        mMessageContainer = itemView.findViewById(R.id.message_container);
        mMessage = itemView.findViewById(R.id.message);
        mGreen300 = ContextCompat.getColor(itemView.getContext(), R.color.material_green_300);
        mGray300 = ContextCompat.getColor(itemView.getContext(), R.color.material_gray_300);


    }

    public void bind(@NonNull AbstractCourse chat) {
        setName(chat.getName());
        setMessage(chat.getMessage());

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
       // setIsSender(currentUser != null && chat.getUid().equals(currentUser.getUid()));
    }

    private void setName(@Nullable String name) {
        mNameField.setText(name);
    }

    private void setMessage(@Nullable String text) {
        mTextField.setText(text);
    }


}
