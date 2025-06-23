package com.example.shop_sqlite_manage.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop_sqlite_manage.Entity.User;
import com.example.shop_sqlite_manage.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onEditUser(User user, int position);
        void onDeleteUser(User user, int position);
    }

    public UserAdapter(List<User> users, OnUserClickListener listener) {
        this.userList = users;
        this.listener = listener;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUsername, tvPassword;
        public Button btnEdit, btnDelete;

        public UserViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvPassword = itemView.findViewById(R.id.tv_password);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvUsername.setText("Username: " + user.getAccount());
        holder.tvPassword.setText("Password: " + user.getPassword());

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditUser(user, position);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteUser(user, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateUserList(List<User> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();
    }
}