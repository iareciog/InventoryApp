package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.ProductContact.ProductEntry;

public class ProductCursorAdapter extends CursorAdapter {

    String productQuantity;

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.productName);
        TextView priceTextView = view.findViewById(R.id.price);
        final TextView quantityTextView = view.findViewById(R.id.productQuantity);

        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);

        final int columnID = cursor.getColumnIndex(ProductEntry._ID);

        final String productName = cursor.getString(nameColumnIndex);
        final String productPrice = cursor.getString(priceColumnIndex);
        productQuantity = cursor.getString(quantityColumnIndex);

        if (TextUtils.isEmpty(productQuantity)) {
            productQuantity = "0";
        }

        //Get the position of the cursor
        final int position = cursor.getPosition();

        Button sellButton = view.findViewById(R.id.sellButton);
        sellButton.setTag(cursor.getPosition());

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ContentValues values = new ContentValues();

                cursor.moveToPosition(position);

                int oldQuantity = (cursor.getInt(quantityColumnIndex));
                int newQuantity;

                // check if has a negative value or not
                if (oldQuantity < 1){
                    newQuantity = 0;
                }else{
                    //assign the new value for quantity
                    newQuantity = --oldQuantity;
                }

                values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);

                String selection = ProductEntry._ID + "=?";

                int item_id = cursor.getInt(columnID);

                String itemIdArgs = Integer.toString(item_id);

                String[] selectionArgs = {itemIdArgs};

                // if quantity is 0, delete the product from db
                if (newQuantity == 0) {
                    int rowsDeleted = context.getContentResolver().delete(ContentUris.withAppendedId(ProductEntry.CONTENT_URI, item_id), null, null);
                } else {
                    // update the current product
                    int updateProduct = context.getContentResolver().update(Uri.withAppendedPath(
                            ProductEntry.CONTENT_URI, Integer.toString(item_id)),
                            values, selection, selectionArgs);
                }
            }
        });

        nameTextView.setText(productName);
        priceTextView.setText(productPrice);
        quantityTextView.setText(productQuantity);
    }

}
