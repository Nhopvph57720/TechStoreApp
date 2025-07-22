package com.example.techstoreapp.FirebaseHelper;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireBaseHelper {
    public static DatabaseReference getProductsRef() {
        return FirebaseDatabase
                .getInstance("https://techstoreapp-de25c-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("products");
    }
}
