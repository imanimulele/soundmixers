package com.example.smixers.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Common interface for chat messages, helps share code between RTDB and Firestore examples.
 */
public abstract class AbstractChat {



    @Nullable
    public abstract String getName();

    public abstract void setName(@Nullable String name);

    @Nullable
    public abstract String getClassId();

    public abstract void setClassId(@Nullable String classId);



    @Nullable
    public abstract String getCategory_id();

    public abstract void setCategory_id(@Nullable String category_id);

    @Nullable
    public abstract String getDescription();

    public abstract void setDescription(@Nullable String description);

    @Nullable
    public abstract String getMessage();

    public abstract void setMessage(@Nullable String message);

    @NonNull
    public abstract String getUid();

    public abstract void setUid(@NonNull String uid);

    @Override
    public abstract boolean equals(@Nullable Object obj);

    @Override
    public abstract int hashCode();
}
