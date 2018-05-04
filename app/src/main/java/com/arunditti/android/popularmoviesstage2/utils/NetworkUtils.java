package com.arunditti.android.popularmoviesstage2.utils;

import android.net.Uri;
import android.util.Log;
import android.view.MenuInflater;

import com.arunditti.android.popularmoviesstage2.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
/**
 * Created by arunditti on 5/2/18.
 */

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
    private static final String SORT_BY = "sort_by";
    private static final String MOVIE_REVIEW_PATH = "/reviews";
    private static final String  API_KEY = "api_key";

    //Format we want our API to return
    private static final String format = "json";

    //Build the URL
    public static URL buildUrl(String movieQuery) {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendQueryParameter(SORT_BY, movieQuery)
                .appendQueryParameter(API_KEY, BuildConfig.PICASSO_API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI: " + url);
        return url;
    }

    //Build the URL for movie reviews

  
    public static URL buildReviewUrl(String movieId) {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendQueryParameter(movieId, MOVIE_REVIEW_PATH)
                .appendQueryParameter(API_KEY, BuildConfig.PICASSO_API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI for reviews: " + url);
        return url;
    }


    //This method return the entire result from the HTTP response
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
