package com.example.android.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by bruno on 25/07/2017.
 */
public class InventoryContract {

    private InventoryContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ITEMS = "items";

    /** Define values for item database */
    public static final class ItemEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public final static String TABLE_NAME = "items";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_ITEM_NAME = "name";

        public final static String COLUMN_ITEM_AMOUNT = "amount";

        public final static String COLUMN_ITEM_PRICE = "price";

        public final static String COLUMN_ITEM_IMAGE = "image";

        public static final String DEFAULT_IMAGE = "android.resource://com.example.android.inventory/drawable/placeholder";
    }
}
