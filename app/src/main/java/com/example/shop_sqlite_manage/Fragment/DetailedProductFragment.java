package com.example.shop_sqlite_manage.Fragment;

import android.content.ContentUris;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.example.shop_sqlite_manage.Provider.ProductContract;
import com.example.shop_sqlite_manage.R;

public class DetailedProductFragment extends Fragment {
    private static final String ARG_PRODUCT_ID = "product_id";
    private long productId;

    public static DetailedProductFragment newInstance(long id) {
        DetailedProductFragment fragment = new DetailedProductFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PRODUCT_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_product, container, false);

        if (getArguments() != null) {
            productId = getArguments().getLong(ARG_PRODUCT_ID);
        }

        loadProductDetails(view);

        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void loadProductDetails(View view) {
        Cursor cursor = getContext().getContentResolver().query(
                ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, productId),
                null, null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            try {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_NAME));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRICE));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_QUANTITY));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_DESCRIPTION));

                ((TextView) view.findViewById(R.id.textName)).setText(name);
                ((TextView) view.findViewById(R.id.textPrice)).setText(String.format("%.0f VND", price));
                ((TextView) view.findViewById(R.id.textQuantity)).setText("Số lượng: " + quantity);
                ((TextView) view.findViewById(R.id.textDescription)).setText(description);

            } finally {
                cursor.close();
            }
        }
    }
}