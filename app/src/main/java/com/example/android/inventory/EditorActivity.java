package com.example.android.inventory;


import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.CursorLoader;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView mAmountTextView;

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

    /** Quantity */
    private int quantity;

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
        mAmountTextView = (TextView) findViewById(R.id.edit_item_number);
        mPriceEditText = (EditText) findViewById(R.id.edit_item_price);

        mNameEditText.setOnTouchListener(mTouchListener);
        mAmountTextView.setOnTouchListener(mTouchListener);
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
            mAmountTextView.setText(String.valueOf(amount));
            mPriceEditText.setText(String.valueOf(price));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mAmountTextView.setText("");
        mPriceEditText.setText("");
    }

    /** SAVE ITEM */
    private void saveItem(){
        //read fields
        String name = mNameEditText.getText().toString().trim();
        String amount = mAmountTextView.getText().toString().trim();
        String price = mPriceEditText.getText().toString().trim();

        // exit if empty
        if(mCurrentItemUri == null &&
                TextUtils.isEmpty(name) ||
                TextUtils.isEmpty(price)){
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_NAME, name);
        values.put(ItemEntry.COLUMN_ITEM_PRICE, price);

        //default amount
        int amountDefault = 0;

        if(!TextUtils.isEmpty(amount)){
            amountDefault = Integer.parseInt(amount);
        }
        values.put(ItemEntry.COLUMN_ITEM_AMOUNT, amountDefault);

        // New or existing item
        if(mCurrentItemUri == null){
            //New Item
            Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);

        } else {
            //Existing item
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

        }
    }

    /** MENU */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveItem();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        // Delete
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        //Cancel
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentItemUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    public void lessItem(View view) {
        if (quantity <= 0) {
            Toast.makeText(this, "Quantity can't be negative!", Toast.LENGTH_SHORT).show();
        } else {
            quantity--;
            mAmountTextView.setText(String.valueOf(quantity));
        }
    }

    public void moreItem(View view) {
        quantity++;
        mAmountTextView.setText(String.valueOf(quantity));
    }
}
