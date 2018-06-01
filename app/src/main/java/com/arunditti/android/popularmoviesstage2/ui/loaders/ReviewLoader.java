package com.arunditti.android.popularmoviesstage2.ui.loaders;

import android.content.Context;
import android.util.Log;

import android.support.v4.content.AsyncTaskLoader;

import com.arunditti.android.popularmoviesstage2.model.Review;
import com.arunditti.android.popularmoviesstage2.utils.JsonUtils;
import com.arunditti.android.popularmoviesstage2.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by arunditti on 5/29/18.
 */

public class ReviewLoader extends AsyncTaskLoader<ArrayList<Review>>{

    private static final String LOG_TAG = ReviewLoader.class.getSimpleName();

    private String mMovieId;

    public ReviewLoader(Context context, String id) {
        super(context);
        mMovieId = id;
    }

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

            Log.d(LOG_TAG, "Movie ID is : " + mMovieId);
            URL ReviewRequestUrl = NetworkUtils.buildReviewUrl(mMovieId);

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
}


