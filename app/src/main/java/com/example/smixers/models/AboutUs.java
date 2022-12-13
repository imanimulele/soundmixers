package com.example.smixers.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class AboutUs extends AbstractAbout {
    private String mName;
    private String mUid;
    private Date mTimestamp;
    private String mDescription;
    private String mThumbnail;
    private String mLocalAddress;
    private String mMissionStatement;
    private String mVisionStatement;

    public String getComapanyVision() {
        return comapanyVision;
    }

    public void setComapanyVision(String comapanyVision) {
        this.comapanyVision = comapanyVision;
    }

    public String getAboutUstitle() {
        return aboutUstitle;
    }

    public void setAboutUstitle(String aboutUstitle) {
        this.aboutUstitle = aboutUstitle;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    private String comapanyVision;
    private  String aboutUstitle;
    private  String companyAddress;

    public AboutUs() {
        // Needed for Firebase
    }

    public AboutUs(@Nullable String name, @NonNull String uid, @NonNull String description, @NonNull String thumbnail, @NonNull String localAddress, @NonNull String missionStatement, @NonNull String visionStatement) {
        mName = name;
        mUid = uid;
        mDescription=description;
        mThumbnail=thumbnail;
        mLocalAddress=localAddress;
        mMissionStatement=missionStatement;
        mVisionStatement=visionStatement;

    }

    @Override
    @Nullable
    public String getName() {
        return mName;
    }

    @Override
    public void setName(@Nullable String name) {
        mName = name;
    }

    @Nullable
    @Override
    public String getThumbnail() {
        return mThumbnail;
    }

    @Override
    public void setThumbnail(@Nullable String thumbnail) {
        mThumbnail=thumbnail;
    }

    @Nullable
    @Override
    public String getMission() {
        return mMissionStatement;
    }

    @Override
    public void setMission(@Nullable String missionStatement) {
mMissionStatement=missionStatement;
    }

    @Nullable
    @Override
    public String getVision() {
        return mVisionStatement;
    }

    @Override
    public void setVision(@Nullable String visionStatement) {
mVisionStatement=visionStatement;
    }

    @Nullable
    @Override
    public String getLocalAddress() {
        return mLocalAddress;
    }

    @Override
    public void setLocalAddress(@Nullable String localAddress) {
mLocalAddress=localAddress;
    }


    @Nullable
    @Override
    public String getDescription() {
        return mDescription;
    }

    @Override
    public void setDescription(@Nullable String description) {
        mDescription=description;
    }

    @Override
    @NonNull
    public String getUid() {
        return mUid;
    }



    @Override
    public void setUid(@NonNull String uid) {
        mUid = uid;
    }

    @ServerTimestamp
    @Nullable
    public Date getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(@Nullable Date timestamp) {
        mTimestamp = timestamp;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AboutUs chat = (AboutUs) o;

        return mTimestamp.equals(chat.mTimestamp)
                && mUid.equals(chat.mUid)
                && (mName == null ? chat.mName == null : mName.equals(chat.mName));

    }



    @Override
    public int hashCode() {
        int result = mName == null ? 0 : mName.hashCode();

        result = 31 * result + mUid.hashCode();
        result = 31 * result + mTimestamp.hashCode();
        return result;
    }


    @NonNull
    @Override
    public String toString() {
        return "Chat{" +
                "mName='" + mName + '\'' +
                ", mUid='" + mUid + '\'' +
                ", mTimestamp=" + mTimestamp +
                '}';
    }


}
