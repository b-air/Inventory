package com.example.android.inventory;


import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventory.data.InventoryContract.ItemEntry;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Item data loader id */
    private static final int ITEM_LOADER = 0;

    /** ListView adapter */
    ItemCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated
        ListView itemListView = (ListView) findViewById(R.id.list);

        // Find set EmptyView
        View emptyView = findViewById(R.id.empty_view);
        itemListView.setEmptyView(emptyView);

        // Setup Adapter
        mCursorAdapter = new ItemCursorAdapter(this, null);
        itemListView.setAdapter(mCursorAdapter);

        // Setup ClickListener
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                // Form the content URI
                Uri currentItemUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentItemUri);

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(ITEM_LOADER, null, this);
    }

    public void buyItem(long id, int amount){
        Uri currentItem = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);

        if(amount > 0){
            amount--;
        }
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_AMOUNT, amount);
        getContentResolver().update(currentItem, values, null, null);
    }

    private void insertItem(){
        // insert dummy data
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_NAME, "Dummy item");
        values.put(ItemEntry.COLUMN_ITEM_AMOUNT, 10);
        values.put(ItemEntry.COLUMN_ITEM_PRICE, 14);

        Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);
    }

    private void deleteAllItems(){
        int rowsDeleted = getContentResolver().delete(ItemEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from item database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertItem();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_AMOUNT,
                ItemEntry.COLUMN_ITEM_PRICE };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                ItemEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
