package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContact.ProductEntry;

public class EditProductActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    /** Identifier for the product data loader */
    private static final int EXISTING_PRODUCT_LOADER = 0;

    /** Content URI for the existing product */
    private Uri mCurrentProductUri;

    /** EditText field to enter the product's name*/
    private EditText mProductNameEditText;

    /** EditText field to enter the product's price*/
    private EditText mProductPriceEditText;

    /** EditText field to enter the product's quantity*/
    private EditText mProductQuantityEditText;

    /** EditText field to enter the supplier name */
    private EditText mSupplierNameEditText;

    /** EditText field to enter the supplier phone */
    private EditText mSupplierPhoneEditText;

    /** Boolean flag that keeps */
    private boolean mProductHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're adding a new product or editing an existing one.
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        // if the intent does not contain a product content URI, we are creating a product insert
        if (mCurrentProductUri == null){
            // set title for the activity to "Add a product"
            setTitle(getString(R.string.edit_product_title_new_product));
            // Invalidate the Options menu because doesn't have nothing to delete
            invalidateOptionsMenu();
        } else {
            // set title to "Edit a product" because the product already exist
            setTitle(getString(R.string.edit_product_title_edit_product));
            // Initialize a loader to read the product data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mProductNameEditText = findViewById(R.id.edit_product_name);
        mProductPriceEditText = findViewById(R.id.edit_product_price);
        mProductQuantityEditText = findViewById(R.id.edit_product_quantity);
        mSupplierNameEditText = findViewById(R.id.edit_supplier_name);
        mSupplierPhoneEditText = findViewById(R.id.edit_supplier_phone);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mProductNameEditText.setOnTouchListener(mTouchListener);
        mProductPriceEditText.setOnTouchListener(mTouchListener);
        mProductQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);

        // A quantity Add and Reduce "buttons"
        ImageView quantityAdd = findViewById(R.id.quantityAdd);
        ImageView quantityReduce = findViewById(R.id.quantityReduce);

        // A onClickListener for quantityAdd ImageView for use as a button
        // I prefer to use ImageView instead ImageButton because look well
        quantityAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // obtain the string from the product quantity editText
                String quantityString = mProductQuantityEditText.getText().toString();
                int quantity;

                // Check if quantityString have "" value and change to 0 if is true.
                // if not, get the integer for the string and sum 1 to the value or quantity
                if (quantityString.equals("")){
                    quantity = 0;
                } else {
                    quantity = Integer.parseInt(quantityString);
                    quantity = ++quantity;
                }

                // Set the Text to the Quantity editText
                mProductQuantityEditText.setText(Integer.toString(quantity));
            }
        });

        // The same as the quantityAdd but for subtraction
        quantityReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantityString = mProductQuantityEditText.getText().toString().trim();
                int quantity;

                // Check if quantityString have "" value and change to 0 if is true.
                // if not, get the integer for the string and subtract 1 to the value or quantity
                if (quantityString.equals("") || Integer.parseInt(quantityString) < 1){
                    quantity = 0;
                } else {
                    quantity = Integer.parseInt(quantityString);
                    quantity = --quantity;
                }

                // Set the Text to the Quantity editText
                mProductQuantityEditText.setText(Integer.toString(quantity));
            }
        });

        // Find the callSupplier button
        Button callSupplier = findViewById(R.id.call_supplier);
        callSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the supplier phone number
                String supplierPhone = mSupplierPhoneEditText.getText().toString().trim();

                // Create intent for a call using the supplier phone number
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.fromParts("tel", supplierPhone, null));

                // start the new activity using intent
                startActivity(intent);
            }
        });

    }

    // Create a function for save a product
    private void saveProduct() {

        // get all the values for the product
        String productNameString = mProductNameEditText.getText().toString().trim();
        String productPriceString = mProductPriceEditText.getText().toString().trim();
        String productQuantityString = mProductQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();

        // check if the uri is null and all the values exist
        // because all info is needed for create the product
        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(productNameString) || TextUtils.isEmpty(productPriceString) ||
                TextUtils.isEmpty(supplierNameString) || TextUtils.isEmpty(supplierPhoneString)) {
            Toast.makeText(this, "You need to fill all the text boxes",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // check if the uri is null and all the values exist
        // because all info is needed for edit the product
        if (mCurrentProductUri != null &&
                TextUtils.isEmpty(productNameString) || TextUtils.isEmpty(productPriceString) ||
                TextUtils.isEmpty(supplierNameString) || TextUtils.isEmpty(supplierPhoneString)) {
            Toast.makeText(this, "You need to fill all the text boxes",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Create contentValues to put the values for the database
        ContentValues values = new ContentValues();

        String productName = "";
        if(!TextUtils.isEmpty(productNameString)){
            productName = productNameString;
        }
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, productName);

        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, productPriceString);

        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierNameString);

        //Check if productQuantity isn't empty. And if is empty use 1 has value
        int quantity = 1;
        if(!TextUtils.isEmpty(productQuantityString)) {
            quantity = Integer.parseInt(productQuantityString);
        }
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);

        //Check if supplierPhone isn't empty and if the value are positive. And if is empty use 0 has value
        int supplierPhone = 0;
        if(!TextUtils.isEmpty(supplierPhoneString) && Integer.parseInt(supplierPhoneString) >= 0) {
            supplierPhone = Integer.parseInt(supplierPhoneString);
        }
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, supplierPhone);

        // Determine if is a new product if mCurrentProductUri == null
        if (mCurrentProductUri == null) {
            // This is a NEW product, so insert a new product into the provider.
            // returning the content URI for the new product.
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            // Show a toast depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Error saving product",
                        Toast.LENGTH_LONG).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Product saved",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING product, so update the product with content URI: mCurrentProductUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentProductUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Error updating product",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this,"Product updated",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save product to database
                saveProduct();
                // Exit activity
                finish();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditProductActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditProductActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged){
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all products attributes, define a projection that contains
        // all columns from the products table
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getColumnCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int productNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int productPriceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int productQuantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String productName = cursor.getString(productNameColumnIndex);
            float productPrice = cursor.getLong(productPriceColumnIndex);
            int productQuantity = cursor.getInt(productQuantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            int supplierPhone = cursor.getInt(supplierPhoneColumnIndex);

            // Update the views on the screen with the values from the database
            mProductNameEditText.setText(productName);
            mProductPriceEditText.setText(Float.toString(productPrice));
            mProductQuantityEditText.setText(Integer.toString(productQuantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(Integer.toString(supplierPhone));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mProductNameEditText.setText("");
        mProductPriceEditText.setText("");
        mProductQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the products.
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this product?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if(dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {

        if (mCurrentProductUri != null) {
            // Call the ContentResolver to delete the products at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentProductUri
            // content URI already identifies the product that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "Error with deleting product",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "Product deleted",
                        Toast.LENGTH_SHORT).show();
            }

            // Close the activity
            finish();
        }
    }

    /**
     * Helper method to delete all products in the database.
     */
    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        Log.v("EditorActivity", rowsDeleted + " rows deleted from products database");
    }
    
}
