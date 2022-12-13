package com.example.smixers.models;

import android.graphics.drawable.Drawable;

public class LessonCategory {


    public LessonCategory(String image, Drawable imageDrw, String title, String brief, int image_bg, String lessonId) {
        this.image = image;
        this.imageDrw = imageDrw;
        this.title = title;
        this.brief = brief;
        this.image_bg = image_bg;
        this.lessonId = lessonId;
    }

    public String image;
    public Drawable imageDrw;
    public String title;
    public String brief;
    public int image_bg;



    private String lessonId;

    public LessonCategory() {



    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }



    public Drawable getImageDrw() {
        return imageDrw;
    }

    public void setImageDrw(Drawable imageDrw) {
        this.imageDrw = imageDrw;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public int getImage_bg() {
        return image_bg;
    }

    public void setImage_bg(int image_bg) {
        this.image_bg = image_bg;
    }
}
