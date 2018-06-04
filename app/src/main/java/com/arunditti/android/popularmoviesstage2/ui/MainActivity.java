package com.arunditti.android.popularmoviesstage2.ui;


import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arunditti.android.popularmoviesstage2.R;
import com.arunditti.android.popularmoviesstage2.data.FavoritesContract.FavoriteEntry;
import com.arunditti.android.popularmoviesstage2.model.MovieItem;
import com.arunditti.android.popularmoviesstage2.ui.adapters.MovieAdapter;
import com.arunditti.android.popularmoviesstage2.ui.adapters.MovieAdapter.MovieAdapterOnClickHandler;
import com.arunditti.android.popularmoviesstage2.utils.JsonUtils;
import com.arunditti.android.popularmoviesstage2.utils.NetworkUtils;


import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapterOnClickHandler,
       // LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    //Create a String array containing the names of the desired data columns from our ContentProvider
    public static final String[] FAVORITE_MOVIE_COLUMNS = {
            FavoriteEntry._ID,
            FavoriteEntry.COLUMN_MOVIE_ID,
            FavoriteEntry.COLUMN_MOVIE_TITLE,
            FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE,
            FavoriteEntry.COLUMN_MOVIE_OVERVIEW,
            FavoriteEntry.COLUMN_MOVIE_RATING,
            FavoriteEntry.COLUMN_MOVIE_IMAGE_PATH
    };

    //Create constant int values representing each column name's position above
    public static final int INDEX_ID = 0;
    public static final int INDEX_MOVIE_TITLE = 1;
    public static final int INDEX_MOVIE_RELEASE_DATE = 2;
    public static final int INDEX_MOVIE_OVERVIEW = 3;
    public static final int INDEX_MOVIE_RATING = 4;
    public static final int INDEX_MOVIE_IMAGE_PATH = 5;


    //Constant to uniquely identify loader
    private static final int MOVIE_LOADER_ID = 11;
    private static final int FAVORITE_LOADER_ID = 21;
    private int mPosition = RecyclerView.NO_POSITION;
    private static final String MOVIES_KEY = "movies_key";
    private static final String DETAILS_KEY = "MovieItem_parcel";

    private MovieAdapter mAdapter;
    RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private String mSortBy;

    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

    ArrayList<MovieItem> mMovieItems = new ArrayList<MovieItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        //Set the RecyclerView to its corresponding view
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_list);
        //GridLayoutManager mGridLayoutManager = new GridLayoutManager(MainActivity.this, 2);

        //Size in pixels
        int imageWidth = 500;
        GridLayoutManager mGridLayoutManager =
                new GridLayoutManager(this, calculateBestSpanCount(imageWidth));
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mAdapter = new MovieAdapter(this, MainActivity.this, mMovieItems);
        //Link the adapter to the RecyclerView
        mRecyclerView.setAdapter(mAdapter);

        //getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, mPopularAndTopMovieLoader);

        //Register MainActivity as an OnPreferenceChangedListener
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        setupMovieSharedPreferences();

        if(savedInstanceState != null) {
            mMovieItems = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
            mAdapter = new MovieAdapter(this, MainActivity.this, mMovieItems);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            setupMovieSharedPreferences();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIES_KEY, mMovieItems);
    }

        private String setupMovieSharedPreferences() {
        String sortBy;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String keyForMovie = getString(R.string.pref_sort_by_key);
        String defaultMovie = getString(R.string.pref_sort_by_default_value);
        sortBy = sharedPreferences.getString(keyForMovie, defaultMovie);

        if (sortBy.equals(getString(R.string.pref_sort_by_favorite_value))) {
            // Load data from the database
            getSupportLoaderManager().destroyLoader(MOVIE_LOADER_ID);
            getSupportLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, mCursorLoader);
        }
        else {
            //getSupportLoaderManager().destroyLoader(FAVORITE_LOADER_ID);
            //new FetchMoviesTask().execute(sortBy);
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, mPopularAndTopMovieLoader);
        }

        return  sortBy;

    }

    @Override
    public void onClick(MovieItem movieClicked) {

        Intent intentToStartDetailActivity = new Intent(MainActivity.this, DetailActivity.class);
        intentToStartDetailActivity.putExtra(DETAILS_KEY, movieClicked);
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
        setupMovieSharedPreferences();

    }

    private LoaderManager.LoaderCallbacks<Cursor> mCursorLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            switch(id) {
                case FAVORITE_LOADER_ID:
                    Uri favoriteUri = FavoriteEntry.CONTENT_URI;

                    return new CursorLoader(MainActivity.this,
                            favoriteUri,
                            FAVORITE_MOVIE_COLUMNS,
                            null,
                            null,
                            null);

                default:
                    throw new RuntimeException("Loader is not implemented: " + id);
            }

        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            Log.d(LOG_TAG, "onLoadFinished is called");

            ArrayList<MovieItem> movieItems = new ArrayList<MovieItem>();

            data.moveToPosition(-1);

            while (data.moveToNext()) {

                String movieId = data.getString(data.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_ID));
                String movieTitle = data.getString(data.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_TITLE));
                String movieReleaseDate = data.getString(data.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE));
                String movieOverview = data.getString(data.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_OVERVIEW));
                String movieRating = data.getString(data.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_RATING));
                String movieImage = data.getString(data.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_IMAGE_PATH));
                MovieItem movieItem = new MovieItem(movieId, movieTitle, movieReleaseDate, movieOverview, movieRating, movieImage, movieImage);
                movieItems.add(movieItem);
            }

            mAdapter.updateMovieList(movieItems);

            //Call mAdapter's swapCursor method to pass in the new Cursor
            mAdapter.swapCursor(data);

            // If mPosition equals RecyclerView.NO_POSITION, set it to 0
            if (mPosition == RecyclerView.NO_POSITION) {
                mPosition = 0;
            }

            // Smooth scroll the RecyclerView to mPosition
            mRecyclerView.smoothScrollToPosition(mPosition);

            //If the Cursor's size is not equal to 0, call showMovieDataView
            if (data.getCount() != 0) {
                showMovieDataView();
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            Log.d(LOG_TAG, "onLoadReset is called");
            mAdapter.swapCursor(null);
        }
    };


    private LoaderManager.LoaderCallbacks<ArrayList<MovieItem>> mPopularAndTopMovieLoader = new LoaderCallbacks<ArrayList<MovieItem>>() {

        @Override
        public AsyncTaskLoader<ArrayList<MovieItem>> onCreateLoader(int id, final Bundle args) {
            Log.i(LOG_TAG, "onCreateLoader is called");

            return new AsyncTaskLoader<ArrayList<MovieItem>>(MainActivity.this) {

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

                    mSortBy = setupMovieSharedPreferences();

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
    };

    //Suggested by the reviewer
    private int calculateBestSpanCount(int imageWidth) {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float screenWidth = outMetrics.widthPixels;
        return Math.round(screenWidth / imageWidth);
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




    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<MovieItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<MovieItem> doInBackground(String... params) {

            mSortBy = params[0];

            URL MovieRequestUrl = NetworkUtils.buildUrl(mSortBy);

            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(MovieRequestUrl);
                ArrayList<MovieItem> simpleJsonMovieData = JsonUtils.getPopularMoviesDataFromJson(MainActivity.this, jsonMovieResponse);
                return simpleJsonMovieData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MovieItem> data) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (data != null) {
                mMovieItems = data;
                showMovieDataView();
                mAdapter.updateMovieList(mMovieItems);
            } else {
                showErrorMessage();
            }
        }
    }



    //    @NonNull
//    @Override
//    public Loader<ArrayList<MovieItem>> onCreateLoader(int id, @Nullable Bundle args) {
//        Log.i(LOG_TAG, "onCreateLoader is called");
//
//        return new AsyncTaskLoader<ArrayList<MovieItem>>(MainActivity.this) {
//
//            ArrayList<MovieItem> mMovieData = null;
//
//            @Override
//            protected void onStartLoading() {
//                if (mMovieData != null) {
//                    deliverResult(mMovieData);
//                } else {
//                    mLoadingIndicator.setVisibility(View.VISIBLE);
//                    forceLoad();
//                }
//            }
//
//            @Override
//            public ArrayList<MovieItem> loadInBackground() {
//
////                String sortBy = MoviePreferences.getSortOrder(MainActivity.this);
//
//                mSortBy = setupMovieSharedPreferences();
//
//                Log.d(LOG_TAG, "movieQuery is: " + mSortBy);
//
//                URL MovieRequestUrl = NetworkUtils.buildUrl(mSortBy);
//
//                try {
//                    String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(MovieRequestUrl);
//                    ArrayList<MovieItem> simpleJsonMovieData = JsonUtils.getPopularMoviesDataFromJson(MainActivity.this, jsonMovieResponse);
//                    return simpleJsonMovieData;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return null;
//                }
//
//            }
//
//            public void deliverResult(ArrayList<MovieItem> data) {
//                mMovieData = data;
//                super.deliverResult(data);
//            }
//        };
//    }
//
//    @Override
//    public void onLoadFinished(@NonNull Loader<ArrayList<MovieItem>> loader, ArrayList<MovieItem> data) {
//        mLoadingIndicator.setVisibility(View.INVISIBLE);
//        if (data != null) {
//            showMovieDataView();
//            mAdapter.updateMovieList(data);
//           // mRecyclerView.getLayoutManager().onRestoreInstanceState((Parcelable) data);
//        } else {
//            showErrorMessage();
//        }
//    }
//
//    @Override
//    public void onLoaderReset(@NonNull Loader<ArrayList<MovieItem>> loader) {
//
//    }

}