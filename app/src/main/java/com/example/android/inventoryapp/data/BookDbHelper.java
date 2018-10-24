package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.inventoryapp.data.BookContract.BookEntry;


/**
 * Database helper for the Inventory App. Manages book database creation and version.
 */
@SuppressWarnings("ALL")
public class BookDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = BookDbHelper.class.getSimpleName();

    // Name of database file
    private static final String DATABASE_NAME = "bookstore.db";

    // Database version-- increment if changing schema
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs new instance of {@link BookDbHelper}.
     * @param context of app [cursor = null because set to default]
     */
    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when database is first created
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create String with SQL statement to create books table
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + " ("
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_BOOK_TITLE + " TEXT NOT NULL, "
                + BookEntry.COLUMN_BOOK_AUTHOR + " TEXT NOT NULL, "
                + BookEntry.COLUMN_BOOK_PRICE + " DOUBLE NOT NULL DEFAULT 0, "
                + BookEntry.COLUMN_BOOK_TYPE + " INTEGER NOT NULL, "
                + BookEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + BookEntry.COLUMN_BOOK_SUPPLIER_NAME + " TEXT, "
                + BookEntry.COLUMN_BOOK_SUPPLIER_NUMBER + " TEXT)";

        // Execute SQL statement
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
        Log.v(LOG_TAG, SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do nothing here since version is still at 1
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
