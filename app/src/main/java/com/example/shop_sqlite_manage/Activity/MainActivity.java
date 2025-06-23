package com.example.shop_sqlite_manage.Activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop_sqlite_manage.Adapter.UserAdapter;
import com.example.shop_sqlite_manage.Adapter.UserDataManager;
import com.example.shop_sqlite_manage.Entity.User;
import com.example.shop_sqlite_manage.Provider.UserContract;
import com.example.shop_sqlite_manage.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener {

    private ActivityResultLauncher<Intent> userFormLauncher;
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

        userFormLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadAllUsers(); // refresh lại danh sách khi quay về
                    }
                }
        );

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
        btnAddUser.setOnClickListener(v -> showAddUserScreen());
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

    private void showAddUserScreen() {
        Intent intent = new Intent(this, UserFormActivity.class);
        userFormLauncher.launch(intent);
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
        showEditUserScreen(user);
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
    private void showEditUserScreen(User user) {
        Intent intent = new Intent(this, UserFormActivity.class);
        intent.putExtra("username", user.getAccount());
        intent.putExtra("password", user.getPassword());
        userFormLauncher.launch(intent);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadAllUsers();
        }
    }
}