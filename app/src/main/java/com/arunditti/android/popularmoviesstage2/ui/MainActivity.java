package com.arunditti.android.popularmoviesstage2.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arunditti.android.popularmoviesstage2.R;
import com.arunditti.android.popularmoviesstage2.data.FavoritesContract.FavoriteEntry;
import com.arunditti.android.popularmoviesstage2.data.FavoritesDbHelper;
import com.arunditti.android.popularmoviesstage2.model.MovieItem;
import com.arunditti.android.popularmoviesstage2.ui.adapters.MovieAdapter;
import com.arunditti.android.popularmoviesstage2.ui.adapters.MovieAdapter.MovieAdapterOnClickHandler;
import com.arunditti.android.popularmoviesstage2.ui.loaders.FetchMoviesLoader;
import com.arunditti.android.popularmoviesstage2.utils.JsonUtils;
import com.arunditti.android.popularmoviesstage2.utils.MoviePreferences;
import com.arunditti.android.popularmoviesstage2.utils.NetworkUtils;


import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapterOnClickHandler,
       LoaderCallbacks<ArrayList<MovieItem>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    //Constant to uniquely identify loader
    private static final int MOVIE_LOADER_ID = 11;

    private MovieAdapter mAdapter;
    RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;
    private Toast mToast;
    private ProgressBar mLoadingIndicator;

    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

    ArrayList<MovieItem> mMovieItems = new ArrayList<MovieItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        //Set the RecyclerView to its corresponding view
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_list);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mAdapter = new MovieAdapter(this, MainActivity.this, mMovieItems);

        //Link the adapter to the RecyclerView
        mRecyclerView.setAdapter(mAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        int loaderId = MOVIE_LOADER_ID;

        //LoaderCallbacks<ArrayList<MovieItem>> callback = MainActivity.this;
        Bundle bundleForLoader = null;

        LoaderCallbacks<ArrayList<MovieItem>> callbacks = MainActivity.this;

        //Ensure a loader is initialized and active. If the loader doesn't already exist, one is created and starts the loader. Othe
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callbacks);

        //Register MainActivity as an OnPreferenceChangedListener
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public Loader<ArrayList<MovieItem>> onCreateLoader(int id, final Bundle args) {
        Log.i(LOG_TAG, "onCreateLoader is called");

        return new AsyncTaskLoader<ArrayList<MovieItem>>(this) {

            ArrayList<MovieItem> mMovieData = null;

            @Override
            protected void onStartLoading() {
                if(mMovieData != null) {
                    deliverResult(mMovieData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public ArrayList<MovieItem> loadInBackground() {

//                String sortBy = MoviePreferences.getSortOrder(MainActivity.this);

                String mSortBy = setupSharedPreferences();;

              // if(!mSortBy.equals(R.string.pref_sort_by_favorite_value)) {
                    Log.d(LOG_TAG, "movieQuery is: " + mSortBy);

                    URL MovieRequestUrl = NetworkUtils.buildUrl(mSortBy);

                    try {
                        String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(MovieRequestUrl);
                        ArrayList<MovieItem> simpleJsonMovieData = JsonUtils.getPopularMoviesDataFromJson(MainActivity.this, jsonMovieResponse);
                        return simpleJsonMovieData;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }

//                } else {
//                    fetchFavoriteMovies();
//
//                }
                //return mMovieData;

            }

            public void deliverResult(ArrayList<MovieItem> data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MovieItem>> loader, ArrayList<MovieItem> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null) {
            showMovieDataView();
            mAdapter.updateMovieList(data);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MovieItem>> loader) {

    }

    public void fetchFavoriteMovies() {
        Cursor mCursor = getContentResolver().query(FavoriteEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        ArrayList<MovieItem> movieItems = new ArrayList<MovieItem>();


        while (mCursor.moveToNext()) {

            String movieId = mCursor.getString(mCursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_ID));
            String movieTitle = mCursor.getString(mCursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_TITLE));
            String movieReleaseDate = mCursor.getString(mCursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE));
            String movieOverview = mCursor.getString(mCursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_OVERVIEW));
            String movieRating = mCursor.getString(mCursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_RATING));
            String movieImage = mCursor.getString(mCursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_IMAGE_PATH));
            MovieItem movieItem = new MovieItem(movieId, movieTitle, movieReleaseDate, movieOverview, movieRating, movieImage, movieImage);
            movieItems.add(movieItem);
        }

        mAdapter.updateMovieList(movieItems);
        mCursor.close();
    }


    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

        private String setupSharedPreferences() {
        String sortBy;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String keyForMovie = getString(R.string.pref_sort_by_key);
        String defaultMovie = getString(R.string.pref_sort_by_default_value);
        sortBy = sharedPreferences.getString(keyForMovie, defaultMovie);

        //sharedPreferences.registerOnSharedPreferenceChangeListener(this);
//
//        if (sortBy.equals(getString(R.string.pref_sort_by_favorite_value))) {
//            // Load data from the database
//            fetchFavoriteMovies();
//
//        } else {
//            //new FetchMoviesTask().execute(sortBy);
//        }

        return  sortBy;

    }



    @Override
    public void onClick(MovieItem movieClicked) {

        Intent intentToStartDetailActivity = new Intent(MainActivity.this, DetailActivity.class);
        intentToStartDetailActivity.putExtra("MovieItem", movieClicked);
        startActivity(intentToStartDetailActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(PREFERENCES_HAVE_BEEN_UPDATED) {
            Log.d(LOG_TAG, "onStart: Preferences were updated");
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(LOG_TAG, "Preferences are updated");
        PREFERENCES_HAVE_BEEN_UPDATED = true;

    }

    //    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<MovieItem>> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mLoadingIndicator.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected ArrayList<MovieItem> doInBackground(String... params) {
//
//            mSortBy = params[0];;
//
//            URL MovieRequestUrl = NetworkUtils.buildUrl(mSortBy);
//
//            try {
//                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(MovieRequestUrl);
//                ArrayList<MovieItem> simpleJsonMovieData = JsonUtils.getPopularMoviesDataFromJson(MainActivity.this, jsonMovieResponse);
//                return simpleJsonMovieData;
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<MovieItem> data) {
//            mLoadingIndicator.setVisibility(View.INVISIBLE);
//            if (data != null) {
//                mMovieItems = data;
//                showMovieDataView();
//                mAdapter.updateMovieList(mMovieItems);
//            } else {
//                showErrorMessage();
//            }
//        }
//    }
//

}