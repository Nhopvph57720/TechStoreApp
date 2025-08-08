package com.example.techstoreapp.FirebaseHelper;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireBaseHelper {
    public static DatabaseReference getProductsRef() {
        return FirebaseDatabase
                .getInstance("https://techstoreapp-de25c-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("products");
    }

    public static DatabaseReference getTrendingRef() {
        return FirebaseDatabase
                .getInstance("https://techstoreapp-de25c-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("trending");
    }

    public static DatabaseReference getUsersRef() {
        return FirebaseDatabase
                .getInstance("https://techstoreapp-de25c-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users");
    }

    public static DatabaseReference getCartRef(String uid) {
        return FirebaseDatabase
                .getInstance("https://techstoreapp-de25c-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Cart").child(uid);
    }

    public static DatabaseReference getBillsRef() {
        return FirebaseDatabase
                .getInstance("https://techstoreapp-de25c-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Bill");
    }


}
