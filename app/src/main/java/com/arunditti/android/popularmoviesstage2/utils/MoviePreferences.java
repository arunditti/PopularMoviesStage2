package com.arunditti.android.popularmoviesstage2.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.arunditti.android.popularmoviesstage2.R;

/**
 * Created by arunditti on 5/2/18.
 */

public class MoviePreferences {

    public static String getSortOrder(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String keyForMovie = context.getString(R.string.pref_sort_by_key);
        String defaultMovie = context.getString(R.string.pref_sort_by_default_value);
        String sortBy = sharedPreferences.getString(keyForMovie, defaultMovie);

        if(sortBy.equals(R.string.pref_sort_by_favorite_value)) {
            //Load movies from database
        } else if(sortBy.equals(R.string.pref_sort_by_popularity_value) || sortBy.equals(R.string.pref_sort_by_rating_value)) {
            //Load movies from the network
        }
            return sortBy;
        }
}

