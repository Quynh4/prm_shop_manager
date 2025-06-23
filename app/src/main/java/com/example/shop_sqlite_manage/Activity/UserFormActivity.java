package com.example.shop_sqlite_manage.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.shop_sqlite_manage.R;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shop_sqlite_manage.Provider.UserContract;

public class UserFormActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnSubmit;

    private boolean isEditMode = false;
    private String originalUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnSubmit = findViewById(R.id.btn_submit);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("username")) {
            isEditMode = true;
            originalUsername = intent.getStringExtra("username");
            etUsername.setText(originalUsername);
            etUsername.setEnabled(false);
            etPassword.setText(intent.getStringExtra("password"));
            btnSubmit.setText("Cập nhật");
        }

        btnSubmit.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEditMode) {
                updateUser(originalUsername, password);
            } else {
                if (isUsernameExists(username)) {
                    Toast.makeText(this, "Username đã tồn tại", Toast.LENGTH_SHORT).show();
                } else {
                    addUser(username, password);
                }
            }
        });

    }


    private boolean isUsernameExists(String username) {
        String[] projection = { UserContract.UserEntry.COLUMN_USERNAME };
        String selection = UserContract.UserEntry.COLUMN_USERNAME + " = ?";
        String[] selectionArgs = { username };

        try (Cursor cursor = getContentResolver().query(
                UserContract.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        )) {
            return cursor != null && cursor.moveToFirst(); // Có ít nhất 1 bản ghi trùng username
        }
    }

    private void addUser(String username, String password) {
        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_USERNAME, username);
        values.put(UserContract.UserEntry.COLUMN_PASSWORD, password);

        Uri result = getContentResolver().insert(UserContract.CONTENT_URI, values);
        if (result != null) {
            Toast.makeText(this, "Thêm user thành công", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Thêm user thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUser(String username, String newPassword) {
        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_PASSWORD, newPassword);

        int rowsUpdated = getContentResolver().update(
                UserContract.CONTENT_URI,
                values,
                UserContract.UserEntry.COLUMN_USERNAME + " = ?",
                new String[]{username}
        );

        if (rowsUpdated > 0) {
            Toast.makeText(this, "Cập nhật user thành công", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Cập nhật user thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}
