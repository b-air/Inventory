package com.example.android.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract.ItemEntry;

import static android.R.attr.id;

/**
 * Created by bruno on 25/07/2017.
 */

public class ItemCursorAdapter extends CursorAdapter {

    private final MainActivity activity;

    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0/*flags*/);
        this.activity = (MainActivity) context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.product_name);
        TextView amountTextView = (TextView) view.findViewById(R.id.product_items);
        TextView priceTextView = (TextView) view.findViewById(R.id.product_price);

        // Find the columns of pet attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
        final int amountColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_AMOUNT);
        int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);

        // Read the pet attributes from the Cursor for the current pet
        final String itemName = cursor.getString(nameColumnIndex);
        final int itemNumber = cursor.getInt(amountColumnIndex);
        String itemPrice = cursor.getString(priceColumnIndex);

        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(itemName);
        amountTextView.setText("Items: " + itemNumber);
        priceTextView.setText(itemPrice + " $");

        //onclicklistener
        final Button buyButton = (Button) view.findViewById(R.id.buy_button);

        final long id = cursor.getLong(cursor.getColumnIndex(ItemEntry._ID));

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //On click function
            public void onClick(View view) {
                if (itemNumber > 0) {
                    activity.buyItem(id, itemNumber);
                    Toast.makeText(context.getApplicationContext(), "You Bought 1 " + itemName, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context.getApplicationContext(), itemName + " not available.", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

}
