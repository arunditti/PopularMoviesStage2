package com.arunditti.android.popularmoviesstage2.data;

import android.provider.BaseColumns;

import java.util.SimpleTimeZone;

/**
 * Created by arunditti on 5/10/18.
 */

public class FavoritesContract {

    public static final class FavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movie_release_date";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_MOVIE_RATING = "movie_rating";
        public static final String COLUMN_MOVIE_IMAGE_PATH = "movie_image_path";
    }
}
