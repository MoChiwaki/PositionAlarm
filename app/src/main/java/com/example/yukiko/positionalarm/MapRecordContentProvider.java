package com.example.yukiko.positionalarm;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * maprecodeテーブルのレコードをCursorLoaderに提供
 * レコード追加
 */
public class MapRecordContentProvider extends ContentProvider {
    private DatabaseHelper mDbHelper;

    private static final int MAPRECORD = 10;
    private static final int MAPRECORD_ID = 20;
    private static final String AUTHORITY = "com.example.yukiko.positionalarm.MapRecordContentProvider";

    private static final String BASE_PASH = "maprecord";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PASH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/maprecord";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/maprecord";

    private static final UriMatcher uriMatcher = new UriMatcher((UriMatcher.NO_MATCH));
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PASH, MAPRECORD);
        uriMatcher.addURI(AUTHORITY, BASE_PASH + "/#", MAPRECORD_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DatabaseHelper(getContext());
        return false;
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(DatabaseHelper.TABLE_MAPRECORD);

        int uriType = uriMatcher.match(uri);
        switch (uriType) {
            case MAPRECORD:
                break;
            case MAPRECORD_ID:
                queryBuilder.appendWhere(DatabaseHelper.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI:" + uri);
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase sqlDB = mDbHelper.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case MAPRECORD:
                id = sqlDB.insert(DatabaseHelper.TABLE_MAPRECORD, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.withAppendedPath(uri, String.valueOf(id));

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
    @Override
    public String getType(Uri uri) {
        return null;
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }



}
