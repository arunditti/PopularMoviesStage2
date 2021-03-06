package com.arunditti.android.popularmoviesstage2.ui;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arunditti.android.popularmoviesstage2.R;
import com.arunditti.android.popularmoviesstage2.data.FavoritesContract.FavoriteEntry;
import com.arunditti.android.popularmoviesstage2.databinding.ActivityDetailBinding;
import com.arunditti.android.popularmoviesstage2.ui.loaders.ReviewLoader;
import com.arunditti.android.popularmoviesstage2.model.MovieItem;
import com.arunditti.android.popularmoviesstage2.model.Review;
import com.arunditti.android.popularmoviesstage2.model.Trailer;
import com.arunditti.android.popularmoviesstage2.ui.adapters.ReviewAdapter;
import com.arunditti.android.popularmoviesstage2.ui.adapters.TrailerAdapter;
import com.arunditti.android.popularmoviesstage2.ui.loaders.TrailerLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    public static final String YOU_TUBE_VIDEO_URL = "http://www.youtube.com/watch?v=";

    private static final int REVIEW_LOADER_ID = 21;
    private static final int TRAILER_LOADER_ID = 31;

    private static final String DETAILS_KEY = "MovieItem_parcel";

    private MovieItem mCurrentMovieItem;

    RecyclerView mReviewRecyclerView;
    private ReviewAdapter mReviewAdapter;

    RecyclerView mTrailerRecyclerView;
    private TrailerAdapter mTrailerAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    private FloatingActionButton fab;

    private boolean isFavorite = false;

    ArrayList<MovieItem> mMovieItems = new ArrayList<MovieItem>();

    ArrayList<Review> mReviewItems = new ArrayList<Review>();
    ArrayList<Trailer> mTrailerItems = new ArrayList<Trailer>();
    String mMovieId;

    //Declare an ActivityDetailBinding field called mDetailBinding
    private ActivityDetailBinding mDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

               /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.detail_error_message_display);

        Intent intentThatStartedThisActivity = getIntent();

        mCurrentMovieItem = intentThatStartedThisActivity.getParcelableExtra(DETAILS_KEY);

        mMovieId = mCurrentMovieItem.getItemId();

        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        mDetailBinding.movieTitle.setText(mCurrentMovieItem.getMovieTitle());
        mDetailBinding.movieReleaseDate.setText(mCurrentMovieItem.getmMovieReleaseDate());
        mDetailBinding.movieRating.setText(mCurrentMovieItem.getmRating());
        mDetailBinding.movieOverview.setText(mCurrentMovieItem.getmOverview());

        fab = mDetailBinding.fab;

        Picasso.with(this)
                .load(mCurrentMovieItem.getmImagePath())
                .into(mDetailBinding.movieImage);

        Picasso.with(this)
                .load(mCurrentMovieItem.getBackdropPath())
                .into(mDetailBinding.movieImageBackdropPath);

        mReviewRecyclerView = mDetailBinding.rvReviews;
        LinearLayoutManager mLinearLayoutManagerForReviews = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReviewRecyclerView.setLayoutManager(mLinearLayoutManagerForReviews);
        mReviewAdapter = new ReviewAdapter(this, mReviewItems);
        mReviewRecyclerView.setAdapter(mReviewAdapter);

        mTrailerRecyclerView = mDetailBinding.rvTrailers;
        LinearLayoutManager mLinearLayoutManagerForTrailers = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mTrailerRecyclerView.setLayoutManager(mLinearLayoutManagerForTrailers);
        mTrailerAdapter = new TrailerAdapter(this, this, mTrailerItems);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.detail_loading_indicator);

        int loaderIdReview = REVIEW_LOADER_ID;
        int loaderIdTrailer = TRAILER_LOADER_ID;

        getSupportLoaderManager().restartLoader(loaderIdReview, null, mReviewLoader);
        getSupportLoaderManager().restartLoader(loaderIdTrailer, null, mTrailerLoader);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String keyForMovie = getString(R.string.pref_sort_by_key);
        String defaultMovie = getString(R.string.pref_sort_by_default_value);
        String sortBy = sharedPreferences.getString(keyForMovie, defaultMovie);

        if(sortBy.equals(R.string.pref_sort_by_favorite_value) || isMovieFavorite(mCurrentMovieItem.getItemId()) ) {
            isFavorite = true;
            fab.setImageResource(R.drawable.ic_launcher_background);
        } else {
            isFavorite = false;
            fab.setImageResource(R.drawable.ic_launcher_foreground);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorite) {
                    deleteFromFavorites();
                    Toast.makeText(DetailActivity.this, getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT).show();
                    isFavorite = false;
                    fab.setImageResource(R.drawable.ic_launcher_foreground);
                } else {
                    new saveMovieAsFavorite().execute(mCurrentMovieItem);
                    isFavorite = true;
                    fab.setImageResource(R.drawable.ic_launcher_background);
                }
            }
        });

    }

    private boolean isMovieFavorite(String movieId) {
        String mSelectionClause = FavoriteEntry.COLUMN_MOVIE_ID + " = ?";
        String[] mSelectionArgs = {movieId};
        Cursor mCursor = getContentResolver().query(FavoriteEntry.CONTENT_URI, null, mSelectionClause, mSelectionArgs, null);                       // The sort order for the returned rows
        boolean movieIsFavorited = (mCursor != null && mCursor.getCount() > 0);
        mCursor.close();
        return movieIsFavorited;
    }

    private void deleteFromFavorites() {
        String mSelectionClause = FavoriteEntry.COLUMN_MOVIE_ID + " = ?";
        String[] mSelectionArgs = {mCurrentMovieItem.getItemId()};
        int mRowDeleted = getContentResolver().delete(FavoriteEntry.CONTENT_URI, mSelectionClause, mSelectionArgs);
    }


    public class saveMovieAsFavorite extends AsyncTask<MovieItem, Void, Uri> {
        MovieItem movieItem;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Uri doInBackground(MovieItem... params) {
            movieItem = params[0];
            ContentValues contentValues = new ContentValues();
            contentValues.put(FavoriteEntry.COLUMN_MOVIE_ID, mCurrentMovieItem.getItemId());
            contentValues.put(FavoriteEntry.COLUMN_MOVIE_TITLE, mCurrentMovieItem.getMovieTitle());
            contentValues.put(FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE, mCurrentMovieItem.getmMovieReleaseDate());
            contentValues.put(FavoriteEntry.COLUMN_MOVIE_OVERVIEW, mCurrentMovieItem.getmOverview());
            contentValues.put(FavoriteEntry.COLUMN_MOVIE_RATING, mCurrentMovieItem.getmRating());
            contentValues.put(FavoriteEntry.COLUMN_MOVIE_IMAGE_PATH, mCurrentMovieItem.getmImagePath());

            return getContentResolver().insert(FavoriteEntry.CONTENT_URI, contentValues);

        }

        @Override
        protected void onPostExecute(Uri uri) {
            if (uri != null) {
                Toast.makeText(DetailActivity.this, getString(R.string.added_to_favorites), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private LoaderManager.LoaderCallbacks<ArrayList<Review>> mReviewLoader = new LoaderCallbacks<ArrayList<Review>>() {

        @Override
        public Loader<ArrayList<Review>> onCreateLoader(int id, Bundle args) {
            Log.i(LOG_TAG, "onCreateLoader is called");

            return new ReviewLoader(DetailActivity.this, mMovieId);
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Review>> loader, ArrayList<Review> data) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (data != null) {
                showMovieDataView();
                mReviewAdapter.updateReviewList(data);
            } else {
                showErrorMessage();
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Review>> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks<ArrayList<Trailer>> mTrailerLoader = new LoaderCallbacks<ArrayList<Trailer>>() {

        @Override
        public Loader<ArrayList<Trailer>> onCreateLoader(int id, Bundle args) {
            Log.i(LOG_TAG, "onCreateLoader is called");

            return new TrailerLoader(DetailActivity.this, mMovieId);
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Trailer>> loader, ArrayList<Trailer> data) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (data != null) {
                showMovieDataView();
                mTrailerAdapter.updateTrailerList(data);
            } else {
                showErrorMessage();
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Trailer>> loader) {

        }
    };

    @Override
    public void onClick(Trailer trailerClicked) {

        String videoKey = trailerClicked.getKey();
        Uri trailerUri = Uri.parse(YOU_TUBE_VIDEO_URL + videoKey);

        Intent intent = new Intent(Intent.ACTION_VIEW, trailerUri);
        startActivity(intent);
    }

    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie data is visible */
        mReviewRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mReviewRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

}
