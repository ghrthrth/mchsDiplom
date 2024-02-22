package com.example.mchs.ui.gallery;

public class Card {
    private String photoUrl;
    private String username;
    private String msg;
    private String category;
    private String categIncident;

    public Card(String photoUrl, String username, String msg, String category, String categIncident) {
        this.photoUrl = photoUrl;
        this.username = username;
        this.msg = msg;
        this.category = category;
        this.categIncident = categIncident;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getMsg() {
        return msg;
    }

    public String getCategory() {
        return category;
    }

    public String getCategIncident() {
        return categIncident;
    }
}

