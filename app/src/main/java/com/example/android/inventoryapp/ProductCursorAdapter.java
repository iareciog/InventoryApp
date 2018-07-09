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

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product data as its data source. This adapter knows
 * how to create list items for each row of product data in the {@link Cursor}.
 */

public class ProductCursorAdapter extends CursorAdapter {

    String productQuantity;

    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param viewGroup  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.productName);
        TextView priceTextView = view.findViewById(R.id.price);
        final TextView quantityTextView = view.findViewById(R.id.productQuantity);

        // Find the columns of product attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        final int columnID = cursor.getColumnIndex(ProductEntry._ID);

        // Read the product attributes from the Cursor for the current product
        final String productName = cursor.getString(nameColumnIndex);
        final String productPrice = cursor.getString(priceColumnIndex);
        productQuantity = cursor.getString(quantityColumnIndex);

        //Check if productQuantity is Empty. In that case, productQuantity obtain the value "0"
        if (TextUtils.isEmpty(productQuantity)) {
            productQuantity = "0";
        }

        //Get the position of the cursor
        final int position = cursor.getPosition();


        /**
         * All this code if for the functionality of the sellButton on selected position
         * */
        //Find the sellButton
        Button sellButton = view.findViewById(R.id.sellButton);
        // Set a tag to sellButton with the position to use it later
        sellButton.setTag(cursor.getPosition());

        //Create a OnClickListener for the sellButton
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create a contentValues object for update or delete
                // the product
                ContentValues values = new ContentValues();

                //Move the cursor to the actual position.
                cursor.moveToPosition(position);

                // Initialize oldQuantity variable with the value on the index of the cursor
                // And other int for next use
                int oldQuantity = (cursor.getInt(quantityColumnIndex));
                int newQuantity;

                // check if has a negative value or not
                // if is true, newQuantity = 0
                // if not subtract 1 from the quantity
                if (oldQuantity < 1){
                    newQuantity = 0;
                }else{
                    //assign the new value for quantity
                    newQuantity = --oldQuantity;
                }

                // put the value in the position of COLUMN_PRODUCT_QUANTITY
                values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);

                //Selection claus which will point to the item_sold_id which will be updated
                String selection = ProductEntry._ID + "=?";

                //Get the item id which should be updated
                int item_id = cursor.getInt(columnID);
                String itemIdArgs = Integer.toString(item_id);

                //Selection args claus
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
