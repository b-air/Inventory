package com.example.android.inventory;


import android.app.LoaderManager;
import android.content.Intent;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.example.android.inventory.data.InventoryContract.ItemEntry;

import static android.R.attr.data;

/**
 * Created by bruno on 25/07/2017.
 */

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Item data Loader */
    private static final int EXISTING_ITEM_LOADER = 0;

    /** Content URI */
    private Uri mCurrentItemUri;

    /** EditText for Name */
    private EditText mNameEditText;

    /** EditText for Amount */
    private EditText mAmountEditText;

    /** EditText for Price */
    private EditText mPriceEditText;

    /** change item flag */
    private boolean mItemHasChanged = false;

    /** Listener for user touches */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        if(mCurrentItemUri == null){
            setTitle(getString(R.string.editor_new_item));
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.editor_edit_item);
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.edit_item_name);
        mAmountEditText = (EditText) findViewById(R.id.edit_item_number);
        mPriceEditText = (EditText) findViewById(R.id.edit_item_price);

        mNameEditText.setOnTouchListener(mTouchListener);
        mAmountEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_AMOUNT,
                ItemEntry.COLUMN_ITEM_PRICE };
        return new CursorLoader(this,
                mCurrentItemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor == null || cursor.getCount() <1){
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
            int amountColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_AMOUNT);
            int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);

            String name = cursor.getString(nameColumnIndex);
            int amount = cursor.getInt(amountColumnIndex);
            int price = cursor.getInt(priceColumnIndex);

            mNameEditText.setText(name);
            mAmountEditText.setText(amount);
            mPriceEditText.setText(price);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mAmountEditText.setText("");
        mPriceEditText.setText("");
    }
}
