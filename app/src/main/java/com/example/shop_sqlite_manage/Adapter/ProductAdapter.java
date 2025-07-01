package com.example.shop_sqlite_manage.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop_sqlite_manage.Entity.Product;
import com.example.shop_sqlite_manage.R;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> originalList;
    private List<Product> filteredList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(long productId);
    }

    public ProductAdapter(List<Product> productList, OnItemClickListener listener) {
        this.originalList = new ArrayList<>(productList);
        this.filteredList = new ArrayList<>(productList);
        this.listener = listener;
    }

    public void filter(String query) {
        if (query == null || query.equalsIgnoreCase("all") || query.isEmpty()) {
            filteredList = new ArrayList<>(originalList);
        } else {
            String q = query.toLowerCase();
            filteredList = new ArrayList<>();
            for (Product p : originalList) {
                if (p.name.toLowerCase().contains(q)) {
                    filteredList.add(p);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = filteredList.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textPrice;

        public ProductViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textPrice = itemView.findViewById(R.id.textPrice);
        }

        void bind(Product product, OnItemClickListener listener) {
            textName.setText(product.name);
            textPrice.setText(String.format("%.0f VND", product.price));
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(product.id);
                }
            });
        }
    }
}
