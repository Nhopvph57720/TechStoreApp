package com.example.techstoreapp.Model;

public class Product {
    public String id;
    public String name;
    public int price;
    public String imageUrl;

    public Product() {}

    public Product(String id, String name, int price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }
}

