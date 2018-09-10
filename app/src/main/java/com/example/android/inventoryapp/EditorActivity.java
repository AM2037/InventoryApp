package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract.BookEntry;
import com.example.android.inventoryapp.data.BookDbHelper;

/**
 * What users use to add (or edit) book entries
 */

public class EditorActivity extends AppCompatActivity {

    // EditText field to enter the book title
    private EditText mTitleEditText;

    // EditText field to enter book author
    private EditText mAuthorEditText;

    // EditText field to enter book price
    private EditText mPriceEditText;

    // EditText field to enter book type (spinner)
    private Spinner mTypeSpinner;

    // EditText field to enter how many in stock, or quantity
    private EditText mQuantityEditText;

    // EditText field to enter supplier name
    private EditText mSupplierEditText;

    // EditText field to enter supplier phone number
    private EditText mNumberEditText;

    /**
     * Type of book. Possible values are in the BookContract.java file:
     * {@link BookEntry#BOOK_TYPE_UNKNOWN}, {@link BookEntry#BOOK_TYPE_HARDCOVER}, or
     * {@link BookEntry#BOOK_TYPE_PAPERBACK}.
     */

    private int mType = BookEntry.BOOK_TYPE_UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Locate views necessary to read user input
        mTitleEditText = findViewById(R.id.edit_book_title);
        mAuthorEditText = findViewById(R.id.edit_book_author);
        mPriceEditText = findViewById(R.id.edit_book_price);
        mTypeSpinner = findViewById(R.id.spinner_type);
        mQuantityEditText = findViewById(R.id.edit_book_quantity);
        mSupplierEditText = findViewById(R.id.edit_supplier_name);
        mNumberEditText = findViewById(R.id.edit_supplier_number);

        setSpinner();
    }

    /**
     * Set up dropdown spinner for book type
     */
    private void setSpinner() {
        // Create spinner adapter from the String array list options -- default layout
        ArrayAdapter typeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_type_options, android.R.layout.simple_spinner_item);

        // Specify layout style for dropdown menu-- List using 1 item/line [may switch to recycler later]
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        // Apply adapter
        mTypeSpinner.setAdapter(typeSpinnerAdapter);

        // Set integer mSelected to constants
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.type_hardcover))) {
                        mType = BookEntry.BOOK_TYPE_HARDCOVER; // Hardcover
                    } else if (selection.equals(getString(R.string.type_paperback))) {
                        mType = BookEntry.BOOK_TYPE_PAPERBACK; // Paperback
                    } else {
                        mType = BookEntry.BOOK_TYPE_UNKNOWN; // Unknown
                    }
                }
            }

            // Since AdapterView = abstract, define onNothingSelected
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mType = BookEntry.BOOK_TYPE_UNKNOWN; //Unknown
            }
        });
    }

    /**
     * Retrieve user entry and save book to database
     */
    private void insertBook() {

        // Delete trailing or unnecessary space(s)
        String titleString = mTitleEditText.getText().toString().trim();
        String authorString = mAuthorEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        // Integer.parseInt("1") --> 1 ex for ones converted to integers
        double price = Double.parseDouble(priceString);
        String quantityString = mQuantityEditText.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);
        String supplierString = mSupplierEditText.getText().toString().trim();
        String numberString = mNumberEditText.getText().toString().trim();

        // Create database helper
        BookDbHelper mDbHelper = new BookDbHelper(this);

        // Get database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create ContentValues object where column names = keys
        // Book attributes = entries (from editor)
        ContentValues entries = new ContentValues();
        entries.put(BookEntry.COLUMN_BOOK_TITLE, titleString);
        entries.put(BookEntry.COLUMN_BOOK_AUTHOR, authorString);
        entries.put(BookEntry.COLUMN_BOOK_PRICE, price);
        entries.put(BookEntry.COLUMN_BOOK_TYPE, mType);
        entries.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
        entries.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierString);
        entries.put(BookEntry.COLUMN_BOOK_SUPPLIER_NUMBER, numberString);

        // Insert new row for each book entry & return row ID
        long newRowId = db.insert(BookEntry.TABLE_NAME, null, entries);

        // Show toast to show if adding new entry was successful or net
        if (newRowId == -1) {
            // If the ID of the row is -1, then there was an error.
            Toast.makeText(this, getString(R.string.toast_fail), Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, it worked so show the successful toast
            Toast.makeText(this, getString(R.string.toast_success) + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu options from menu_editor.xml file under res/menu/
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // For when users click on items in the app bar overflow menu
        switch (item.getItemId()) {
            // If user clicks on "save" -- check mark icon
            case R.id.action_save:
                // Save book to database
                insertBook();
                // Return to CatalogActivity
                finish();
                return true;
            // Respond to clicking "Delete"
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to app bar's "up" navigation
            case android.R.id.home:
                // Navigate to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
