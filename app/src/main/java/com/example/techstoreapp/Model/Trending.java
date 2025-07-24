package com.example.techstoreapp.Model;

public class Trending {
    public String id;
    public String imageUrl;

    public Trending() {
        // Required for Firebase
    }

    public Trending(String id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }
}


