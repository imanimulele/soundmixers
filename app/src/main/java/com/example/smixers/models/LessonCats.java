package com.example.smixers.models;

public class LessonCats {
    private String message;
    private String name;
    private String uid;
    private String classId;
    private String description;




    public LessonCats() {

    }

    public LessonCats(String message, String name, String uid, String classId,String description) {
        this.message = message;
        this.name = name;
        this.uid = uid;
        this.classId=classId;
        this.description=description;

    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
