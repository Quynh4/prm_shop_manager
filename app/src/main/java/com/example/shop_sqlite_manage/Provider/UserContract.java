package com.example.shop_sqlite_manage.Provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class UserContract {
    private UserContract() {}

    // Authority từ app cũ
    public static final String AUTHORITY = "com.example.login.login.shop_sqlite";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/user");
    public static final String TABLE_NAME = "user";

    public static class UserEntry implements BaseColumns {
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";
    }
}