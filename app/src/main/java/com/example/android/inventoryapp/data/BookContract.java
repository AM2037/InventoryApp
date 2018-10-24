package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/** Schema and contract class {@link BookContract} for the book store database */
@SuppressWarnings("WeakerAccess")
public final class BookContract {

    // So as to not accidentally instantiate the class
    private BookContract() {}

    /**
     * The ContentProvider, also known as the content authority (i.e. website and domain)
     * For uniqueness, used package name.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";

    /** Using CONTENT_AUTHORITY to create URI foundation allowing apps to communicate with ContentProvider. */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /** Path for potentially looking at data about books */
    public static final String PATH_BOOKS = "books";

    // Define constants for database using this nested class
    public static final class BookEntry implements BaseColumns {

        // Content URI [access data in provider]
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * Content URI MIME type for book list.
         * CURSOR_DIR_BASE_TYPE maps to "vnd.android.cursor.dir"
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * MIME type of {@link #CONTENT_URI} for a single pet.
         * Maps to constant "vnd.android.cursor.item"
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        // Table name
        public final static String TABLE_NAME = "books";

        // 8 columns: _id, title, author, price, type, quantity, supplier name, supplier phone
        // String = data types for constants, not the values
        public final static String _ID = BaseColumns._ID; // Type: INTEGER PRIMARY KEY AUTOINCREMENT
        public final static String COLUMN_BOOK_TITLE = "title"; // Type: TEXT NOT NULL
        public final static String COLUMN_BOOK_AUTHOR = "author"; // Type: TEXT NOT NULL
        public final static String COLUMN_BOOK_PRICE = "price"; // Type: DOUBLE changed to real
        public final static String COLUMN_BOOK_TYPE = "type"; // Type: INTEGER NOT NULL
        public final static String COLUMN_BOOK_QUANTITY = "quantity"; // Type: INTEGER NOT NULL DEFAULT 0
        public final static String COLUMN_BOOK_SUPPLIER_NAME = "supplier"; // Type: TEXT
        public final static String COLUMN_BOOK_SUPPLIER_NUMBER = "number"; // Type: TEXT

        // Book type constants:
        public final static int BOOK_TYPE_UNKNOWN = 0;
        public final static int BOOK_TYPE_HARDCOVER = 1;
        public final static int BOOK_TYPE_PAPERBACK = 2;

        /** Determine whether given type is {@link #BOOK_TYPE_UNKNOWN}, {@link #BOOK_TYPE_HARDCOVER}, or {@link #BOOK_TYPE_PAPERBACK} */
        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        public static boolean isValidType(int type) {
            return type == BOOK_TYPE_UNKNOWN || type == BOOK_TYPE_HARDCOVER || type == BOOK_TYPE_PAPERBACK;
        }
    }
}
