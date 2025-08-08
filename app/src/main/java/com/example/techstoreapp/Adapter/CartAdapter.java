package com.example.techstoreapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.techstoreapp.FirebaseHelper.FireBaseHelper;
import com.example.techstoreapp.Model.CartItem;
import com.example.techstoreapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.tvProductName.setText(item.getName());
        holder.tvUnitPrice.setText("Giá: " + String.format("%,d VNĐ", item.getPrice()));
        holder.tvQuantity.setText("Số lượng: " + item.getQuantity());
        holder.tvItemTotal.setText("Tổng tiền: " + String.format("%,d VNĐ", item.getTotalPrice()));

        Glide.with(context).load(item.getImageUrl()).into(holder.imgProductThumb);

        holder.btnCancelOrder.setOnClickListener(v -> {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FireBaseHelper.getCartRef(uid)
                    .orderByChild("productId").equalTo(item.getProductId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                child.getRef().removeValue(); // ✅ Chỉ cần xóa Firebase
                            }

                            Toast.makeText(context, "Đã huỷ đơn hàng", Toast.LENGTH_SHORT).show();
                            // ❌ KHÔNG cần gọi: cartItems.remove(position), notifyItemRemoved(position)
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, "Lỗi khi huỷ đơn hàng", Toast.LENGTH_SHORT).show();
                        }
                    });
        });


    }


    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProductThumb;
        TextView tvProductName, tvUnitPrice, tvQuantity, tvItemTotal;
        Button btnCancelOrder;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProductThumb = itemView.findViewById(R.id.imgProductThumb);
            tvProductName = itemView.findViewById(R.id.tvProductNameCart);
            tvUnitPrice = itemView.findViewById(R.id.tvUnitPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantityCart);
            tvItemTotal = itemView.findViewById(R.id.tvItemTotal);
            btnCancelOrder = itemView.findViewById(R.id.btnCancelOrder);
        }
    }
}


