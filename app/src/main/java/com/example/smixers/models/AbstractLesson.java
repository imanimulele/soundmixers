package com.example.smixers.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class AbstractLesson {

    @Nullable
    public abstract String getName();

    public abstract void setName(@Nullable String name);

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
