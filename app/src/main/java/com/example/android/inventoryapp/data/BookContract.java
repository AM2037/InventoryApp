package com.example.android.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Schema and contract class {@link BookContract} for the book store database
 */
public final class BookContract {

    // So as to not accidentally instantiate the class
    private BookContract() {}

    // Define constants for database
    public static final class BookEntry implements BaseColumns {

        // Table name
        public final static String TABLE_NAME = "books";

        // 8 columns: _id, title, author, price, type, quantity, supplier name, supplier phone
        // String = data types for constants, not the values
        public final static String _ID = BaseColumns._ID; // Type: INTEGER PRIMARY KEY AUTOINCREMENT
        public final static String COLUMN_BOOK_TITLE = "title"; // Type: TEXT NOT NULL
        public final static String COLUMN_BOOK_AUTHOR = "author"; // Type: TEXT NOT NULL
        public final static String COLUMN_BOOK_PRICE = "price"; // Type: DOUBLE
        public final static String COLUMN_BOOK_TYPE = "book_type"; // Type: INTEGER NOT NULL
        public final static String COLUMN_BOOK_QUANTITY = "quantity"; // Type: INTEGER NOT NULL DEFAULT 0
        public final static String COLUMN_BOOK_SUPPLIER_NAME = "supplier"; // Type: TEXT
        public final static String COLUMN_BOOK_SUPPLIER_NUMBER = "number"; // Type: TEXT

        // Book type constants:
        public final static int BOOK_TYPE_UNKNOWN = 0;
        public final static int BOOK_TYPE_HARDCOVER = 1;
        public final static int BOOK_TYPE_PAPERBACK = 2;
    }
}
