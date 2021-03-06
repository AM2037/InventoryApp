package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of book data as its data source. This adapter knows
 * how to create list items for each row of book data in the {@link Cursor}.
 */
@SuppressWarnings("ALL")
public class BookCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }
    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }
    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView titleTextView = view.findViewById(R.id.title);
        TextView summaryTextView = view.findViewById(R.id.summary);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        Button saleButton = view.findViewById(R.id.sell);

        //Find the columns of book attributes that we're interested in
        int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
        int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);

        //Read the book attributes from the Cursor for the current book
        String bookTitle = cursor.getString(titleColumnIndex);
        String bookAuthor = cursor.getString(authorColumnIndex);
        double bookPrice = cursor.getDouble(priceColumnIndex);
        String bookQuantity = cursor.getString(quantityColumnIndex);


        // Update the TextViews with the attributes for the current book
        titleTextView.setText(bookTitle);
        summaryTextView.setText(bookAuthor);
        priceTextView.setText(String.valueOf(bookPrice));
        quantityTextView.setText(bookQuantity);

        final int id = cursor.getInt(cursor.getColumnIndex(BookEntry._ID));
        final int quantity = Integer.parseInt(bookQuantity);
        // Sell items
        saleButton.findViewById(R.id.sell).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullCatalogActivity mainActivity = (FullCatalogActivity) context;
                mainActivity.sellBook(id, quantity);
            }
        });
    }
}