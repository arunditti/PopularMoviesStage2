package com.arunditti.android.popularmoviesstage2.ui;

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
import com.arunditti.android.popularmoviesstage2.databinding.ActivityDetailBinding;
import com.arunditti.android.popularmoviesstage2.model.MovieItem;
import com.arunditti.android.popularmoviesstage2.model.Review;
import com.arunditti.android.popularmoviesstage2.model.Trailer;
import com.arunditti.android.popularmoviesstage2.ui.adapters.ReviewAdapter;
import com.arunditti.android.popularmoviesstage2.utils.JsonUtils;
import com.arunditti.android.popularmoviesstage2.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements
        LoaderCallbacks<ArrayList<Review>> {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private static final int REVIEW_LOADER_ID = 21;

    private MovieItem mCurrentMovieItem;

    RecyclerView mReviewRecyclerView;
    private ReviewAdapter mReviewAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    ArrayList<Review> mReviewItems = new ArrayList<Review>();

    //Declare an ActivityDetailBinding field called mDetailBinding
    private ActivityDetailBinding mDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

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

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReviewRecyclerView.setLayoutManager(mLinearLayoutManager);

        //mAdapter = new ReviewAdapter(this, null, mReviewItems);
        mReviewAdapter = new ReviewAdapter(this, mReviewItems);

        //Link the adapter to the RecyclerView
        mReviewRecyclerView.setAdapter(mReviewAdapter);


        mLoadingIndicator = (ProgressBar) findViewById(R.id.detail_loading_indicator);

        int loaderId = REVIEW_LOADER_ID;

        Bundle bundleForLoader = null;

        LoaderCallbacks<ArrayList<Review>> callbacks = DetailActivity.this;

        getSupportLoaderManager().restartLoader(loaderId, bundleForLoader, callbacks);

    }

    @Override
    public Loader<ArrayList<Review>> onCreateLoader(int id, final Bundle args) {
        Log.i(LOG_TAG, "onCreateLoader is called");

        return new AsyncTaskLoader<ArrayList<Review>>(this) {

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
                URL ReviewRequestUrl = NetworkUtils.buildReviewAndTrailerUrl(movieId);

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