package com.arunditti.android.popularmoviesstage2.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.SimpleTimeZone;

/**
 * Created by arunditti on 5/10/18.
 */

public class FavoritesContract {

    public static final String SCHEME = "content://";
    //Authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.arunditti.android.popularmoviesstage2";

    //The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY);

    //Define the possible paths for accessing data in the contract
    public static final String PATH_FAVORITES = "favorites";


    //FavoriteEntry is an inner class that defines the contents of the favorites table
    public static final class FavoriteEntry implements BaseColumns {

        //Favorites content URI = base content URI + path
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        //Favorites table name
        public static final String TABLE_NAME = "favorites";

        //Favorites column names
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movie_release_date";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_MOVIE_RATING = "movie_rating";
        public static final String COLUMN_MOVIE_IMAGE_PATH = "movie_image_path";
    }
}
