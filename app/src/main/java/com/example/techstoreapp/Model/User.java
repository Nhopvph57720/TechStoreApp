package com.example.techstoreapp.Model;

public class User {
    public String name;
    public String email;
    public boolean isAdmin; // <-- thêm dòng này

    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.isAdmin = false; // <-- mặc định là false
    }
}
