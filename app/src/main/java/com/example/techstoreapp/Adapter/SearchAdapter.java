package com.example.techstoreapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.techstoreapp.Model.Product;
import com.example.techstoreapp.R;

import java.text.NumberFormat;
import java.util.List;

public class SearchAdapter extends ArrayAdapter<Product> {
    private Context context;
    private List<Product> products;

    public SearchAdapter(Context context, List<Product> products) {
        super(context, 0, products);
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Product product = products.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tvProductName);
        TextView tvPrice = convertView.findViewById(R.id.tvProductPrice);
        ImageView imgProduct = convertView.findViewById(R.id.imgProduct);

        tvName.setText(product.getName());

        // Format giá
        String formattedPrice = NumberFormat.getInstance().format(product.getPrice()) + "đ";
        tvPrice.setText(formattedPrice);

        // Load ảnh bằng Glide
        Glide.with(context)
                .load(product.getImageUrl()) // đảm bảo bạn có getImageUrl() trong Product
                .placeholder(R.drawable.default_avatar) // ảnh tạm khi loading
                .into(imgProduct);
        return convertView;


    }
}


