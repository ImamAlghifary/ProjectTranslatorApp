package com.example.projecttranslatorapp;

public class Destination {
    private String title;
    private String description;
    private int imageResource; // Drawable resource ID

    // Empty constructor needed for Firebase
    public Destination() {
    }

    public Destination(String title, String description, int imageResource) {
        this.title = title;
        this.description = description;
        this.imageResource = imageResource;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}

