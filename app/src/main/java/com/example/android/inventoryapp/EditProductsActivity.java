package com.example.android.inventoryapp;

import android.annotation.SuppressLint;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

/**
 * What users use to add (or edit) book entries
 * Used to be called "EditorActivity"
 */

public class EditProductsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for book data loader */
    private static final int EXISTING_BOOK_LOADER = 0;

    /** Content URI for existing book (null if it's a new book) */
    private Uri mCurrentBookUri;


    /** EditText field to enter the book title */
    private EditText mTitleEditText;

    /** EditText field to enter book author */
    private EditText mAuthorEditText;

    /** EditText field to enter book price */
    private EditText mPriceEditText;

    /** EditText field to enter book type (spinner) */
    private Spinner mTypeSpinner;

    /** TextView to show many in stock, or quantity */
    private TextView mQuantityTextView;

    /** EditText field to enter supplier name */
    private EditText mSupplierEditText;

    /** EditText field to enter supplier phone number*/
    private EditText mNumberEditText;

    /**
     * Type of book. Possible values are in the BookContract.java file:
     * {@link BookEntry#BOOK_TYPE_UNKNOWN}, {@link BookEntry#BOOK_TYPE_HARDCOVER}, or
     * {@link BookEntry#BOOK_TYPE_PAPERBACK}.
     */

    private int mType = BookEntry.BOOK_TYPE_UNKNOWN;

    /** Boolean flag that keeps track of whether the pet has been edited (true) or not (false) */
    private boolean mBookHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they're
     * modifying the view, changing mBookHasChanged to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Examine the intent that was used to launch this activity, to figure out if
        // we're creating a new book or editing one that already exists.
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // If the intent doesn't have URI then it's a new book
        if (mCurrentBookUri == null) {
            // New book, change app bar to say "Add a Book"
            setTitle(getString(R.string.editor_activity_title_new_book));

            // Invalidate options menu to hide "Delete" menu item since we can't delete
            // a book that doesn't exist yet
            invalidateOptionsMenu();
        } else {
            // Otherwise, this book exists; change app bar to say "Edit Book"
            setTitle(getString(R.string.editor_activity_title_edit_book));

            // Initialize loader to read data from DB and display values in editor
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Locate views necessary to read user input
        mTitleEditText = findViewById(R.id.edit_book_title);
        mAuthorEditText = findViewById(R.id.edit_book_author);
        mPriceEditText = findViewById(R.id.edit_book_price);
        mTypeSpinner = findViewById(R.id.spinner_type);
        mQuantityTextView = findViewById(R.id.book_quantity);
        mSupplierEditText = findViewById(R.id.edit_supplier_name);
        mNumberEditText = findViewById(R.id.edit_supplier_number);

        // OnTouchListeners for input fields which determine if user's touched or modified
        // them. It will notify us of unsaved changes.
        mTitleEditText.setOnTouchListener(mTouchListener);
        mAuthorEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mTypeSpinner.setOnTouchListener(mTouchListener);
        mQuantityTextView.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mNumberEditText.setOnTouchListener(mTouchListener);

        // Get local currency for each user? i.e.
        // https://github.com/yummywakame/BookInventory2/blob/master/app/src/main/java/com/yummywakame/bookinventory2/EditorActivity.java

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
    private void saveBook() {
        // Read from input fields
        // Delete trailing or unnecessary space(s)
        String titleString = mTitleEditText.getText().toString().trim();
        String authorString = mAuthorEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityTextView.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);
        String supplierString = mSupplierEditText.getText().toString().trim();
        String numberString = mNumberEditText.getText().toString().trim();

        // Check if this is supposed to be a new book & if all fields are blank
        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(titleString) && TextUtils.isEmpty(authorString) &&
                TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(supplierString) && TextUtils.isEmpty(numberString) &&
                mType == BookEntry.BOOK_TYPE_UNKNOWN) {
            // Since none were modified, return without creating new book.
            // No need to create ContentValues and no need to do any provider operations.
            return;
        }

        // Create ContentValues object where column names = keys
        // Book attributes = values (from editor)
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_TITLE, titleString);
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, authorString);
        // Integer.parseInt("1") --> 1 ex for ones converted to integers
        double price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Double.parseDouble(priceString);
        }
        values.put(BookEntry.COLUMN_BOOK_PRICE, price);
        values.put(BookEntry.COLUMN_BOOK_TYPE, mType);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierString);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NUMBER, numberString);

        // Determine if this is a new or existing book by checking if mCurrentBookUri
        // is null or not
        if (mCurrentBookUri == null) {
            // This = NEW book. Insert new book into provider & return URI for it.
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            // Show toast message if insertion was successful (or if it wasn't).
            if (newUri == null) {
                // If the URI is null, there's an insertion error.
                Toast.makeText(this, getString(R.string.toast_fail),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise it was successful & we can display toast.
                Toast.makeText(this, getString(R.string.toast_success),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this book EXITS so update book with content URI: mCurrentBookUri
            // and pass in new ContentValues. Null for selection, selectionArgs since
            // mCurrentBookUri will already id the correct row in DB we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentBookUri,
                    values, null, null);

            // Show toast if update was a success or not.
            if (rowsAffected == 0) {
                // If none were affected, there's an updating error.
                Toast.makeText(this, getString(R.string.toast_fail),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise it was successful & we can show a toast.
                Toast.makeText(this, getString(R.string.toast_success),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu options from menu_editor.xml file under res/menu/
        // This adds menu items to app bar
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so the
     * menu can be updated (some can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If new book, hide "Delete" option
        if (mCurrentBookUri == null) {
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
                // Save book to database
                saveBook();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link FullCatalogActivity}.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditProductsActivity.this);
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
                                NavUtils.navigateUpFromSameTask(EditProductsActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This is called when back button's pressed.
     */
    @Override
    public void onBackPressed() {
        // If book hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise, there are unsaved changes, so set up dialog to warn user.
        // Use click listener to handle confirmation of user discarding changes.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "Discard" so close this activity.
                finish();
            }
        };

        // Alert user to unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all book attributes, define a projection containing
        // all columns from book table.
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_TITLE,
                BookEntry.COLUMN_BOOK_AUTHOR,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_TYPE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_NUMBER };

        // This loader will execute the ContentProvider's query method in background
        return new CursorLoader(this,       // Parent activity context
                mCurrentBookUri,            // Query the content URI for current pet
                projection,                 // Columns to include in resulting Cursor
                null,                       // No selection clause
                null,                       // No selection arguments
                null);                      // Default sort order
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoadFinished(Loader<Cursor> loader,Cursor cursor) {
        // Bail early if cursor = null or there's less than 1 row in it.
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to first row and reading data from Cursor (only row)
        if (cursor.moveToFirst()) {
            // Find columns of book attributes we are interested in
            int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
            int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int typeColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TYPE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int numberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NUMBER);

            // Extract value from Cursor for given index
            String title = cursor.getString(titleColumnIndex);
            String author = cursor.getString(authorColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            int type = cursor.getInt(typeColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String number = cursor.getString(numberColumnIndex);

            // Update Views with DB values.
            mTitleEditText.setText(title);
            mAuthorEditText.setText(author);
            mPriceEditText.setText(Double.toString(price));
            mQuantityTextView.setText(Integer.toString(quantity));
            mSupplierEditText.setText(supplier);
            mNumberEditText.setText(number);

            // Since type = dropdown spinner, map constant value from DB into one of
            // the dropdown options (0 = unknown, 1 = hardcover, 2 = paperback).
            // Then call setSelection() so option is displayed as current selection.
            switch (type) {
                case BookEntry.BOOK_TYPE_HARDCOVER:
                    mTypeSpinner.setSelection(1);
                    break;
                case BookEntry.BOOK_TYPE_PAPERBACK:
                    mTypeSpinner.setSelection(2);
                    break;
                default:
                    mTypeSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If loader's invalidated, clear data from input fields
        mTitleEditText.setText("");
        mAuthorEditText.setText("");
        mPriceEditText.setText("");
        mQuantityTextView.setText("");
        mTypeSpinner.setSelection(0); // Select "unknown" type
        mSupplierEditText.setText("");
        mNumberEditText.setText("");
    }

    /**
     * Show dialog that warns user there's unsaved changes they'll lose if
     * they continue leaving editor
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create AlertDialog.Builder and set the message, & click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked "Keep editing" so dismiss alert and continue editing book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Prompt user to confirm if they want to delete book
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, & click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked "Delete" so delete book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked "Cancel" so dismiss dialog & continue editing.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create an show AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of book from DB.
     */
    private void deleteBook() {
        // Only perform if this book exists.
        if (mCurrentBookUri != null) {
            // Call ContentResolver to delete book at given URI.
            // Pass in null for selection & selectionArgs since mCurrentBookUri URI
            // already id's the book we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show toast if the delete was successful or not.
            if (rowsDeleted == 0) {
                // If none were deleted, there was a deletion error.
                Toast.makeText(this, getString(R.string.toast_fail),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, display successful toast.
                Toast.makeText(this, getString(R.string.toast_success),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close activity
        finish();
    }
}
