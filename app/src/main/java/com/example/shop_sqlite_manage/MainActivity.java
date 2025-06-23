package com.example.shop_sqlite_manage;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop_sqlite_manage.Adapter.UserAdapter;
import com.example.shop_sqlite_manage.Adapter.UserDataManager;
import com.example.shop_sqlite_manage.Entity.User;
import com.example.shop_sqlite_manage.Provider.UserContract;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private UserDataManager userDataManager;
    private List<User> userList;

    private Button btnAddUser, btnRefresh;
    private EditText etSearchUser;
    private Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        setupListeners();

        userDataManager = new UserDataManager(this);
        loadAllUsers();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_users);
        btnAddUser = findViewById(R.id.btn_add_user);
        btnRefresh = findViewById(R.id.btn_refresh);
        etSearchUser = findViewById(R.id.et_search_user);
        btnSearch = findViewById(R.id.btn_search);
    }

    private void setupRecyclerView() {
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);
    }

    private void setupListeners() {
        btnAddUser.setOnClickListener(v -> showAddUserDialog());
        btnRefresh.setOnClickListener(v -> loadAllUsers());
        btnSearch.setOnClickListener(v -> searchUser());
    }

    private void loadAllUsers() {
        if (!userDataManager.isProviderAvailable()) {
            Toast.makeText(this, "Content Provider không khả dụng", Toast.LENGTH_SHORT).show();
            return;
        }

        List<User> users = userDataManager.getAllUsers();
        userList.clear();
        userList.addAll(users);
        userAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Đã tải " + users.size() + " users", Toast.LENGTH_SHORT).show();
    }

    private void searchUser() {
        String account = etSearchUser.getText().toString().trim();
        if (account.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = userDataManager.getUserByAccount(account);
        if (user != null) {
            userList.clear();
            userList.add(user);
            userAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Tìm thấy user: " + user.getAccount(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Không tìm thấy user", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_user, null);

        EditText etUsername = dialogView.findViewById(R.id.et_dialog_username);
        EditText etPassword = dialogView.findViewById(R.id.et_dialog_password);

        builder.setView(dialogView)
                .setTitle("Thêm User Mới")
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String username = etUsername.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();

                    if (username.isEmpty() || password.isEmpty()) {
                        Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    addUser(username, password);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void addUser(String username, String password) {
        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_USERNAME, username);
        values.put(UserContract.UserEntry.COLUMN_PASSWORD, password);

        try {
            Uri result = getContentResolver().insert(UserContract.CONTENT_URI, values);
            if (result != null) {
                Toast.makeText(this, "Thêm user thành công", Toast.LENGTH_SHORT).show();
                loadAllUsers(); // Refresh danh sách
            } else {
                Toast.makeText(this, "Thêm user thất bại", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEditUser(User user, int position) {
        showEditUserDialog(user, position);
    }

    @Override
    public void onDeleteUser(User user, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa user: " + user.getAccount() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteUser(user, position))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showEditUserDialog(User user, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_user, null);

        EditText etUsername = dialogView.findViewById(R.id.et_dialog_username);
        EditText etPassword = dialogView.findViewById(R.id.et_dialog_password);

        // Đổ dữ liệu sẵn vào
        etUsername.setText(user.getAccount());
        etPassword.setText(user.getPassword());

        // Không cho chỉnh sửa username
        etUsername.setEnabled(false);
        etUsername.setFocusable(false);
        etUsername.setCursorVisible(false);

        builder.setView(dialogView)
                .setTitle("Sửa User")
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String password = etPassword.getText().toString().trim();

                    if (password.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Username không đổi
                    updateUser(user.getAccount(), user.getAccount(), password, position);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }


    private void updateUser(String oldUsername, String newUsername, String newPassword, int position) {
        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_USERNAME, newUsername);
        values.put(UserContract.UserEntry.COLUMN_PASSWORD, newPassword);

        try {
            int rowsUpdated = getContentResolver().update(
                    UserContract.CONTENT_URI,
                    values,
                    UserContract.UserEntry.COLUMN_USERNAME + " = ?",
                    new String[]{oldUsername}
            );

            if (rowsUpdated > 0) {
                Toast.makeText(this, "Cập nhật user thành công", Toast.LENGTH_SHORT).show();
                loadAllUsers(); // Refresh danh sách
            } else {
                Toast.makeText(this, "Cập nhật user thất bại", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteUser(User user, int position) {
        try {
            int rowsDeleted = getContentResolver().delete(
                    UserContract.CONTENT_URI,
                    UserContract.UserEntry.COLUMN_USERNAME + " = ?",
                    new String[]{user.getAccount()}
            );

            if (rowsDeleted > 0) {
                Toast.makeText(this, "Xóa user thành công", Toast.LENGTH_SHORT).show();
                userList.remove(position);
                userAdapter.notifyItemRemoved(position);
            } else {
                Toast.makeText(this, "Xóa user thất bại", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}