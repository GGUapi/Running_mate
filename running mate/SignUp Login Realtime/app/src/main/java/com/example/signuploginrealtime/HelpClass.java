package com.example.signuploginrealtime;

public class HelpClass {
    private static HelpClass instance;
    private UserProfile profile;

    private HelpClass() {
        // 建構子設成private，防止別人建立實例
    }

    public static HelpClass getInstance() {
        if (instance == null) {
            instance = new HelpClass();
        }
        return instance;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    public UserProfile getProfile() {
        return profile;
    }
}
