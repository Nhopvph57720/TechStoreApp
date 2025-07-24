package com.example.techstoreapp.Adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.techstoreapp.Model.Trending;
import com.example.techstoreapp.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.TrendingViewHolder> {

    private List<Trending> trendingList;

    public TrendingAdapter(List<Trending> trendingList) {
        this.trendingList = trendingList;
    }

    @NonNull
    @Override
    public TrendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trending, parent, false);  // sử dụng layout bạn vừa đặt tên
        return new TrendingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingViewHolder holder, int position) {
        Trending trending = trendingList.get(position);
        holder.pb.setVisibility(View.VISIBLE);
        holder.txtName.setText("Giảm giá sốc!"); // hoặc trending.title nếu bạn có

        Glide.with(holder.itemView.getContext())
                .load(trending.imageUrl)
                .centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        holder.pb.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, Object model,
                                                   @NonNull Target<Drawable> target, @NonNull DataSource dataSource,
                                                   boolean isFirstResource) {
                        holder.pb.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return trendingList.size();
    }

    public static class TrendingViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imgProduct;
        ProgressBar pb;
        TextView txtName;

        public TrendingViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product_thumbnail);
            pb = itemView.findViewById(R.id.pb_load_img);
            txtName = itemView.findViewById(R.id.tv_product_name);
        }
    }
}


