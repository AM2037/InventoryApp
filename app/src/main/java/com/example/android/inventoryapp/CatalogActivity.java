package com.example.android.inventoryapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.inventoryapp.data.BookContract.BookEntry;
import com.example.android.inventoryapp.data.BookDbHelper;

/**
 * Displays list of books entered and stored in the database
 */
public class CatalogActivity extends AppCompatActivity {

    private BookDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Launch EditorActivity with Floating Action Button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        /**
         * Instantiates subclass of SQLiteOpenHelper to access DB
         * and pass in the current activity (aka context)
         */
        mDbHelper = new BookDbHelper(this);
    }

    /**
     * Overrides onStart() when returning to activity after the user saves an entry
     * Also increments row count/ID
     */
    @Override
    protected void onStart() {
        super.onStart();
        displayData();
    }

    /**
     * Helper method that temporarily displays info about the state of the books DB
     */
    @SuppressLint("SetTextI18n")
    private void displayData() {
        // Create/open DB to read from it (CLI command: ".open bookstore.db")
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // query method will return back our cursor
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_TITLE,
                BookEntry.COLUMN_BOOK_AUTHOR,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_TYPE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_NUMBER
        };

        Cursor cursor = db.query(
                BookEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        TextView displayView = findViewById(R.id.text_view_book);

        try {
            // Create header textview that resembles:
            // The books table contains the <number of rows in Cursor> books.
            // _id - title - author - price - type - quantity - supplier - number

            // Iterate through cursor rows with while loop to display info from columns
            displayView.setText("This books table contains " + cursor.getCount() + " books.\n\n");
            displayView.append(BookEntry._ID + " - " +
                    BookEntry.COLUMN_BOOK_TITLE + " - " +
                    BookEntry.COLUMN_BOOK_AUTHOR + " - " +
                    BookEntry.COLUMN_BOOK_PRICE + " - " +
                    BookEntry.COLUMN_BOOK_TYPE + " - " +
                    BookEntry.COLUMN_BOOK_QUANTITY + " - " +
                    BookEntry.COLUMN_BOOK_SUPPLIER_NAME + " - " +
                    BookEntry.COLUMN_BOOK_SUPPLIER_NUMBER + "\n"
            );

            // Determine column indices
            int idColIndex = cursor.getColumnIndex(BookEntry._ID);
            int titleColIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
            int authorColIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR);
            int priceColIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int typeColIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TYPE);
            int quantityColIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierColIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int numberColIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NUMBER);

            // Iterate through the returned cursor rows
            while (cursor.moveToNext()) {
                // Extract String/Int values of words at current row via index
                int currentID = cursor.getInt(idColIndex);
                String currentTitle = cursor.getString(titleColIndex);
                String currentAuthor = cursor.getString(authorColIndex);
                int currentPrice = cursor.getInt(priceColIndex);
                int currentType = cursor.getInt(typeColIndex);
                int currentQuantity = cursor.getInt(quantityColIndex);
                String currentSupplier = cursor.getString(supplierColIndex);
                String currentNumber = cursor.getString(numberColIndex);
                // Display column values of current cursor row in TextViews
                displayView.append(("\n" + currentID + " - " +
                        currentTitle + " - " +
                        currentAuthor + " - " +
                        currentPrice + " - " +
                        currentType + " - " +
                        currentQuantity + " - " +
                        currentSupplier + " - " +
                        currentNumber));
            }
        } finally {
            // Release all resources, or invalidate, when finished with cursor
            cursor.close();
        }
    }

    private void insertBook() {
        // Get DB in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues entries = new ContentValues();
        // Key = column header. Entry = information for each book
        entries.put(BookEntry.COLUMN_BOOK_TITLE, "The Magicians");
        entries.put(BookEntry.COLUMN_BOOK_AUTHOR, "Lev Grossman");
        entries.put(BookEntry.COLUMN_BOOK_PRICE, 10.42);
        entries.put(BookEntry.COLUMN_BOOK_TYPE, BookEntry.BOOK_TYPE_HARDCOVER);
        entries.put(BookEntry.COLUMN_BOOK_QUANTITY, 10); // hypothetical
        entries.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, "Amazon");
        entries.put(BookEntry.COLUMN_BOOK_SUPPLIER_NUMBER, "1 (888) 280-4331");

        // Use SQLiteDatabase method to call insert method to add entry to DB
        // Use variable newRowId to log errors for insertBook()
        long newRowId = db.insert(BookEntry.TABLE_NAME, null, entries);

        Log.v("CatalogActivity", "New row ID " + newRowId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add menu items to app bar
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // For when users click on menu item
        switch (item.getItemId()) {
            // Respond to "insert dummy data" click event
            case R.id.action_insert_dummy_data:
                insertBook();
                displayData();
                return true;
            // Respond to "delete all entries" click event
            case R.id.action_delete_all:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}