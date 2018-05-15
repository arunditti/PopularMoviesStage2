package com.arunditti.android.popularmoviesstage2.ui;

import android.content.ContentValues;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
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
import com.arunditti.android.popularmoviesstage2.data.FavoritesContract;
import com.arunditti.android.popularmoviesstage2.data.FavoritesDbHelper;
import com.arunditti.android.popularmoviesstage2.databinding.ActivityDetailBinding;
import com.arunditti.android.popularmoviesstage2.model.MovieItem;
import com.arunditti.android.popularmoviesstage2.model.Review;
import com.arunditti.android.popularmoviesstage2.model.Trailer;
import com.arunditti.android.popularmoviesstage2.ui.adapters.ReviewAdapter;
import com.arunditti.android.popularmoviesstage2.ui.adapters.TrailerAdapter;
import com.arunditti.android.popularmoviesstage2.utils.JsonUtils;
import com.arunditti.android.popularmoviesstage2.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;


public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    public static final String YOU_TUBE_VIDEO_URL = "http://www.youtube.com/watch?v=";

    private static final int REVIEW_LOADER_ID = 21;
    private static final int TRAILER_LOADER_ID = 31;

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

    //Declare an ActivityDetailBinding field called mDetailBinding
    private ActivityDetailBinding mDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFavorite) {
                    //Delete movie from the database

                } else {
                    //Save movie as favorite
                    //saveMovieAsFavorite();
                }
            }
        });


               /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.detail_error_message_display);

        Intent intentThatStartedThisActivity = getIntent();
        mCurrentMovieItem = intentThatStartedThisActivity.getParcelableExtra("MovieItem");

        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        mDetailBinding.movieTitle.setText(mCurrentMovieItem.getMovieTitle());
        mDetailBinding.movieReleaseDate.setText(mCurrentMovieItem.getmMovieReleaseDate());
        mDetailBinding.movieRating.setText(mCurrentMovieItem.getmRating());
        mDetailBinding.movieOverview.setText(mCurrentMovieItem.getmOverview());

        Picasso.with(this)
                .load(mCurrentMovieItem.getmImagePath())
                .into(mDetailBinding.movieImage);

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

        //FavoritesDbHelper favoritesDbHelper = new FavoritesDbHelper(this);

    }

 //   private Uri saveMovieAsFavorite() {
//        //Create new empty ContentValues object
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_MOVIE_ID, mCurrentMovieItem.getItemId());
//        contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_MOVIE_TITLE, mCurrentMovieItem.getMovieTitle());
//        contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE, mCurrentMovieItem.getmMovieReleaseDate());
//        contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_MOVIE_OVERVIEW, mCurrentMovieItem.getmOverview());
//        contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_MOVIE_RATING, mCurrentMovieItem.getmRating());
//        contentValues.put(FavoritesContract.FavoriteEntry.COLUMN_MOVIE_IMAGE_PATH, mCurrentMovieItem.getmImagePath());
//
//        Uri uri = getContentResolver().insert(FavoritesContract.FavoriteEntry.CONTENT_URI, contentValues);

//        return uri;
//    }


    private LoaderManager.LoaderCallbacks<ArrayList<Review>> mReviewLoader = new LoaderCallbacks<ArrayList<Review>>() {

        @Override
        public Loader<ArrayList<Review>> onCreateLoader(int id, Bundle args) {
            Log.i(LOG_TAG, "onCreateLoader is called");

            return new AsyncTaskLoader<ArrayList<Review>>(getApplicationContext()) {

                ArrayList<Review> mReviewData = null;

                @Override
                protected void onStartLoading() {
                    if (mReviewData != null) {
                        deliverResult(mReviewData);
                    } else {

                        forceLoad();
                    }
                }

                @Override
                public ArrayList<Review> loadInBackground() {

                    String movieId = mCurrentMovieItem.getItemId();
                    Log.d(LOG_TAG, "Movie ID is : " + movieId);
                    URL ReviewRequestUrl = NetworkUtils.buildReviewUrl(movieId);

                    try {
                        String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(ReviewRequestUrl);
                        ArrayList<Review> simpleJsonMovieData = JsonUtils.getMovieReviewsDataFromJson(jsonMovieResponse);
                        return simpleJsonMovieData;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                public void deliverResult(ArrayList<Review> data) {
                    mReviewData = data;
                    super.deliverResult(data);
                }
            };
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

            return new AsyncTaskLoader<ArrayList<Trailer>>(getApplicationContext()) {

                ArrayList<Trailer> mTrailerData = null;

                @Override
                protected void onStartLoading() {
                    if (mTrailerData != null) {
                        deliverResult(mTrailerData);
                    } else {

                        forceLoad();
                    }
                }

                @Override
                public ArrayList<Trailer> loadInBackground() {

                    String movieId = mCurrentMovieItem.getItemId();
                    Log.d(LOG_TAG, "Movie ID is : " + movieId);
                    URL TrailerRequestUrl = NetworkUtils.buildTrailerUrl(movieId);

                    try {
                        String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(TrailerRequestUrl);
                        ArrayList<Trailer> simpleJsonMovieData = JsonUtils.getMovieTrailersDataFromJson(jsonMovieResponse);
                        return simpleJsonMovieData;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                public void deliverResult(ArrayList<Trailer> data) {
                    mTrailerData = data;
                    super.deliverResult(data);
                }
            };
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


    @Override
    public void onClick(Trailer trailerClicked) {

        String videoKey = trailerClicked.getKey();
        Uri trailerUri = Uri.parse(YOU_TUBE_VIDEO_URL + videoKey);

        Intent intent = new Intent(Intent.ACTION_VIEW, trailerUri);
        startActivity(intent);
    }

}
