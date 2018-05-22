package com.arunditti.android.popularmoviesstage2.ui.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.arunditti.android.popularmoviesstage2.model.MovieItem;
import com.arunditti.android.popularmoviesstage2.ui.MainActivity;
import com.arunditti.android.popularmoviesstage2.utils.JsonUtils;
import com.arunditti.android.popularmoviesstage2.utils.MoviePreferences;
import com.arunditti.android.popularmoviesstage2.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by arunditti on 5/21/18.
 */

public class FetchMoviesLoader extends AsyncTaskLoader<ArrayList<MovieItem>> {

        private static final String LOG_TAG = FetchMoviesLoader.class.getSimpleName();

        ArrayList<MovieItem> mMovieData = null;

        public FetchMoviesLoader(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            if(mMovieData != null) {
                deliverResult(mMovieData);
            } else {
                forceLoad();
            }
        }

        @Override
        public ArrayList<MovieItem> loadInBackground() {

            String sortBy = MoviePreferences.getSortOrder(getContext());

            Log.d(LOG_TAG, "movieQuery is: " + sortBy);

            URL MovieRequestUrl = NetworkUtils.buildUrl(sortBy);

            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(MovieRequestUrl);
                ArrayList<MovieItem> simpleJsonMovieData = JsonUtils.getPopularMoviesDataFromJson(getContext(), jsonMovieResponse);
                return simpleJsonMovieData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public void deliverResult(ArrayList<MovieItem> data) {
            mMovieData = data;
            super.deliverResult(data);
        }
}
