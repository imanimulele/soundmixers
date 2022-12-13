package com.example.smixers.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Common interface for chat messages, helps share code between RTDB and Firestore examples.
 */
public abstract class AbstractAbout {

    @Nullable
    public abstract String getName();
    public abstract void setName(@Nullable String name);


    @Nullable
    public abstract String getThumbnail();
    public abstract void setThumbnail(@Nullable String thumbnail);


    @Nullable
    public abstract String getMission();
    public abstract void setMission(@Nullable String missionStatement);


    @Nullable
    public abstract String getVision();
    public abstract void setVision(@Nullable String visionStatement);


    @Nullable
    public abstract String getLocalAddress();
    public abstract void setLocalAddress(@Nullable String localAddress);

    @Nullable
    public abstract String getDescription();

    public abstract void setDescription(@Nullable String description);

    @NonNull
    public abstract String getUid();

    public abstract void setUid(@NonNull String uid);

    @Override
    public abstract boolean equals(@Nullable Object obj);

    @Override
    public abstract int hashCode();

}
