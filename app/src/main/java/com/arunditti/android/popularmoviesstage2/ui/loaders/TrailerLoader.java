package com.arunditti.android.popularmoviesstage2.ui.loaders;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.arunditti.android.popularmoviesstage2.model.Trailer;
import com.arunditti.android.popularmoviesstage2.utils.JsonUtils;
import com.arunditti.android.popularmoviesstage2.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by arunditti on 5/30/18.
 */

public class TrailerLoader extends AsyncTaskLoader<ArrayList<Trailer>> {

    private static final String LOG_TAG = TrailerLoader.class.getSimpleName();

    String mMovieId;
    public TrailerLoader(Context context, String id) {
        super(context);
        mMovieId = id;
    }

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

        Log.d(LOG_TAG, "Movie ID is : " + mMovieId);
        URL TrailerRequestUrl = NetworkUtils.buildTrailerUrl(mMovieId);

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
}
