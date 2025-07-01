package com.example.shop_sqlite_manage.Provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class ProductContract {
    private ProductContract() {}

    public static final String AUTHORITY = "com.example.login.login.shop_sqlite.product";
//    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";

    public static final class ProductEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/product");
        public static final String TABLE_NAME = "products";

        public static final String _ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_IMAGE = "image";
    }
}
