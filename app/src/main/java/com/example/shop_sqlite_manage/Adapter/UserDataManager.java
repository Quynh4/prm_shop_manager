package com.example.shop_sqlite_manage.Adapter;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.shop_sqlite_manage.Entity.User;
import com.example.shop_sqlite_manage.Provider.UserContract;

import java.util.ArrayList;
import java.util.List;

public class UserDataManager {
    private static final String TAG = "UserDataManager";
    private Context context;
    private ContentResolver contentResolver;

    public UserDataManager(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
    }

    /**
     * Lấy tất cả user từ app khác
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = contentResolver.query(
                    UserContract.CONTENT_URI,
                    null, // projection - lấy tất cả columns
                    null, // selection
                    null, // selectionArgs
                    null  // sortOrder
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String account = cursor.getString(
                            cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_USERNAME));
                    String password = cursor.getString(
                            cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_PASSWORD));

                    users.add(new User(account, password));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting users: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return users;
    }

    /**
     * Lấy user theo account
     */
    public User getUserByAccount(String account) {
        Cursor cursor = null;
        User user = null;

        try {
            cursor = contentResolver.query(
                    UserContract.CONTENT_URI,
                    null,
                    UserContract.UserEntry.COLUMN_USERNAME + " = ?",
                    new String[]{account},
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                String userAccount = cursor.getString(
                        cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_USERNAME));
                String password = cursor.getString(
                        cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_PASSWORD));

                user = new User(userAccount, password);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by account: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return user;
    }

    /**
     * Kiểm tra xem có thể truy cập Content Provider không
     */
    public boolean isProviderAvailable() {
        try {
            Cursor cursor = contentResolver.query(
                    UserContract.CONTENT_URI,
                    new String[]{"1"}, // chỉ lấy 1 column để test
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                cursor.close();
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Provider not available: " + e.getMessage());
        }
        return false;
    }
}