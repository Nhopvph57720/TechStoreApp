package com.example.techstoreapp.Model;

public class CartItem {
    private String productId;
    private String name;
    private int price;
    private int quantity;
    private String imageUrl;

    public CartItem() {
        // Firebase cần constructor rỗng
    }

    public CartItem(String productId, String name, int price, int quantity, String imageUrl) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // ✅ Thêm hàm này để dùng trong CartAdapter
    public int getTotalPrice() {
        return price * quantity;
    }
}

