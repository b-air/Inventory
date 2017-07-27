package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventory.data.InventoryContract.ItemEntry;

/**
 * Created by bruno on 25/07/2017.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();

    /** Database */
    private static final String DATABASE_NAME = "inventory.db";

    /** Database Version */
    private static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + " ("
                + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemEntry.COLUMN_ITEM_IMAGE + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_ITEM_AMOUNT + " INTEGER NOT NULL DEFAULT 0, "
                + ItemEntry.COLUMN_ITEM_PRICE + " INTEGER NOT NULL);";

        db.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
