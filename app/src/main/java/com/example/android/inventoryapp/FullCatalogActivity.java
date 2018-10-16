package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract.BookEntry;


/**
 * Displays list of books that were entered and stored in the app.
 */
@SuppressWarnings("ALL")
public class FullCatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;

    // Adapter for our ListView
    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // FAB to open EditProductsActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FullCatalogActivity.this, EditProductsActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the book data
        ListView bookListView = findViewById(R.id.list);

        // Find the set empty view on the ListView, so that it only shows when the list has 0 books.
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of book data in the Cursor
        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);

        // Setup item click listener
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // Create new intent to go to {@link EditProductsActivity}
                Intent intent = new Intent(FullCatalogActivity.this, EditProductsActivity.class);

                /*
                  Form the content URI that represents the specific book that was clicked on,
                  by appending the "id" (passed as input to this method) onto the
                  {@link BookEntry#CONTENT_URI}. i.e. uri: "content://com.example.android.inventoryapp/books/2"
                  if the book with ID 2 was clicked on.
                  */

                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentBookUri);

                // Launch the {@link EditProductsActivity} to display the data for the current book.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }


    /**
     * Helper method to insert hardcoded book data into the database. For debugging only.
     */

    private void insertBook() {

        ContentValues values = new ContentValues();
        // Key = column header. Value = whatever information for book we want to enter
        values.put(BookEntry.COLUMN_BOOK_TITLE, "The Magicians");
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, "Lev Grossman");
        values.put(BookEntry.COLUMN_BOOK_PRICE, "13.97");
        values.put(BookEntry.COLUMN_BOOK_TYPE, BookEntry.BOOK_TYPE_HARDCOVER);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 10);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, "Amazon");
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NUMBER, "1 (888) 280-4331");

        /*
        Insert a new row for The Magicians into provider using ContentResolver.
        Use {@link BookEntry#CONTENT_URI} to indicate we want to insert
        into the books database table.
        Receive new content URI that will allow us access to The Magicians' data in the future.
         */
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
        /*
        Pass in Uri [i.e. name in a row in a table in the db] to content resolver here to interact with
        the book provider (or content provider -- have to use "book provider" because this is an abstract class
        we must subclass to use our own methods**) that will be pulling from the database in BookDbHelper.java [SQLite Database]
        */
    }

    /**
     * Method for selling items-- when a book is sold it's quantity shown by the "In Stock: #" TextViews in FullCatalogActivity will decrease.
     * In accordance with that logic, when an order is placed to restock an item or the user enters a new quantity the number will increase.
     */
    public void sellBook(int itemId, int quantity) {

        // If there are currently books, reduce by 1 each time an item is sold.
        if (quantity > 0) {
            quantity--;

            // Recreate URI with new content values
            Uri soldUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, itemId);
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
            int rowsUpdated = getContentResolver().update(
                    soldUri,
                    values,
                    null,
                    null);
            if (rowsUpdated == 0) {
                // If none updated, show error message in a toast
                Toast.makeText(this, R.string.sale_error, Toast.LENGTH_LONG).show();
            } else {
                // Otherwise, the sale was successful-- show toast.
                Toast.makeText(this, R.string.sale_success, Toast.LENGTH_SHORT).show();
            }
        }

        if (quantity == 0) {
            Toast.makeText(this, R.string.toast_sales, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Helper method to delete all books in the database.
     */
    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        Log.v("FullCatalogActivity", rowsDeleted + " rows deleted from the book database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertBook();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_TITLE,
                BookEntry.COLUMN_BOOK_AUTHOR,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY};

        // This loader will execute teh ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                BookEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        /* Update {@link BookCursorAdapter} with this new cursor containing updated book data */
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}