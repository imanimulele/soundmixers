package com.example.smixers.utils;

public class AppSettings {

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        AppSettings.userName = userName;
    }

    public static String getLocation() {
        return location;
    }

    public static void setLocation(String location) {
        AppSettings.location = location;
    }

    public static String getUserMobile() {
        return userMobile;
    }

    public static void setUserMobile(String userMobile) {
        AppSettings.userMobile = userMobile;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static void setUserEmail(String userEmail) {
        AppSettings.userEmail = userEmail;
    }

    public static String getUserTitle() {
        return userTitle;
    }

    public static void setUserTitle(String userTitle) {
        AppSettings.userTitle = userTitle;
    }

    public static String userName;
    public static String location;
    public static String userMobile;
    public static  String userEmail;
    public static String userTitle;

    public static String getComapanyVision() {
        return comapanyVision;
    }

    public static void setComapanyVision(String comapanyVision) {
        AppSettings.comapanyVision = comapanyVision;
    }

    public static String getCompanyAddress() {
        return companyAddress;
    }

    public static void setCompanyAddress(String companyAddress) {
        AppSettings.companyAddress = companyAddress;
    }

    public static String getAboutUstitle() {
        return aboutUstitle;
    }

    public static void setAboutUstitle(String aboutUstitle) {
        AppSettings.aboutUstitle = aboutUstitle;
    }

    public static String comapanyVision;
    public static String companyAddress;
    public static String aboutUstitle;

    public static String getUserID() {
        return userID;
    }

    public static void setUserID(String userID) {
        AppSettings.userID = userID;
    }

    private static String userID;

    public static String getUserImage() {
        return userImage;
    }

    public static void setUserImage(String userImage) {
        AppSettings.userImage = userImage;
    }

    public static String userImage;



    public AppSettings() {
    }



}
