package com.arunditti.android.popularmoviesstage2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.arunditti.android.popularmoviesstage2.data.FavoritesContract.FavoriteEntry.TABLE_NAME;

/**
 * Created by arunditti on 5/10/18.
 */

public class FavoritesContentProvider extends ContentProvider {

    public static final int FAVORITES = 100;
    public static final int FAVORITES_WITH_ID = 101;

    //Declare a static variable for the Uri matcher that you construct
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    //Define a static buildUriMatcher method that asscociates URI's with their int match
    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //Add matches
        uriMatcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_FAVORITES, FAVORITES);

        //Single item
        uriMatcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_FAVORITES + "/#", FAVORITES_WITH_ID);

        return uriMatcher;
    }

    //Member variable for a FavoriteDbHelper that is initialized in onCreate method
    private FavoritesDbHelper mFavoriteDbHelper;

    @Override
    public boolean onCreate() {
        //Initialize FavoriteDbHelper on startup
        Context context = getContext();
        mFavoriteDbHelper = new FavoritesDbHelper(context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        //Get access to the favorites database to write new data to
        final SQLiteDatabase db = mFavoriteDbHelper.getWritableDatabase();

        //Write URI matching code to identify the match for the favorites directory
        int match = sUriMatcher.match(uri);

        //Uri to be returned
        Uri returnUri;

        switch (match) {
            case FAVORITES:
                long id = db.insert(TABLE_NAME, null, values);
                if(id > 0 ) {
                    returnUri = ContentUris.withAppendedId(FavoritesContract.FavoriteEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri);
                }

                break;
                default:
                    throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        //Notify the resolver if the uri has been changed
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
