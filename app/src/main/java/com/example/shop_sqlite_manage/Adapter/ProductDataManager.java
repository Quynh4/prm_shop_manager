package com.example.shop_sqlite_manage.Adapter;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.shop_sqlite_manage.Entity.Product;
import com.example.shop_sqlite_manage.Provider.ProductContract;

import java.util.ArrayList;
import java.util.List;

public class ProductDataManager {
    private static final String TAG = "ProductDataManager";
    private ContentResolver contentResolver;

    public ProductDataManager(Context context) {
        this.contentResolver = context.getContentResolver();
    }
    public List<Product> getAllProductsAsList() {
        List<Product> productList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(
                    ProductContract.ProductEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null  && cursor.moveToFirst()) {
                do{
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry._ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_NAME));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRICE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_DESCRIPTION));
                    int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_QUANTITY));
                    String image = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_IMAGE));

                    productList.add(new Product(id, name, price, description, quantity, image));
                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting users: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return productList;
    }

    public static Cursor searchProducts(Context context, String keyword) {
        String selection = ProductContract.ProductEntry.COLUMN_NAME + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + keyword.replace(" ", "%") + "%"};

        return context.getContentResolver().query(
                ProductContract.ProductEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null
        );
    }

    public static Cursor getProductById(Context context, long id) {
        Uri uri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, id);
        return context.getContentResolver().query(uri, null, null, null, null);
    }
}
