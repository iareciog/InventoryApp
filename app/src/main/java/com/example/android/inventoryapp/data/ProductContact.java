package com.example.android.inventoryapp.data;

import android.provider.BaseColumns;

public class ProductContact {

    public static abstract class ProductEntry implements BaseColumns{

        public static final String TABLE_NAME = "product";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "Product_Name";
        public static final String COLUMN_PRODUCT_PRICE = "Price";
        public static final String COLUMN_PRODUCT_QUANTITY = "Quantity";
        public static final String COLUMN_PRODUCT_SUPPLIER_NAME = "Supplier_Name";
        public static final String COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER = "Supplier_Phone_Number";

    }

}
