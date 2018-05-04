package com.arunditti.android.popularmoviesstage2.utils;

import android.content.Context;

import com.arunditti.android.popularmoviesstage2.model.MovieItem;
import com.arunditti.android.popularmoviesstage2.model.Review;
import com.arunditti.android.popularmoviesstage2.ui.DetailActivity;
import com.arunditti.android.popularmoviesstage2.ui.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by arunditti on 5/2/18.
 */

public class JsonUtils {
    private final String LOG_TAG = JsonUtils.class.getSimpleName();

    public static ArrayList<MovieItem> getPopularMoviesDataFromJson(MainActivity mainActivity, String PopularMoviesJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String PMD_ID = "id";
        final String PMD_LIST = "results";
        final String PMD_TITLE = "title";
        final String PMD_POSTER = "poster_path";
        final String PMD_BACKDROP_PATH = "backdrop_path";
        final String PMD_PICTURE_PATH = "https://image.tmdb.org/t/p/";
        final String PMD_OVERVIEW = "overview";
        final String PMD_RATING = "vote_average";
        final String PMD_RELEASE_DATE = "release_date";
        final String PMD_PICTURE_SIZE    = "w185";

        JSONObject PopularMoviesJson = new JSONObject(PopularMoviesJsonStr);
        JSONArray moviesArray = PopularMoviesJson.getJSONArray(PMD_LIST);
        ArrayList<MovieItem> movieItems = new ArrayList<MovieItem>();

        movieItems.clear();
        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject popularMovies = moviesArray.getJSONObject(i);
            String movieId = popularMovies.getString(PMD_ID);
            String title = popularMovies.getString(PMD_TITLE);
            String releaseDate = popularMovies.getString(PMD_RELEASE_DATE);
            String movieOverview= popularMovies.getString(PMD_OVERVIEW);
            String rating= popularMovies.getString(PMD_RATING);
            String imagePath = PMD_PICTURE_PATH + PMD_PICTURE_SIZE + popularMovies.getString(PMD_POSTER);
            String backdropPath = PMD_BACKDROP_PATH + PMD_PICTURE_SIZE + popularMovies.getString(PMD_POSTER);
            movieItems.add(new MovieItem(movieId, title, releaseDate, movieOverview, rating, imagePath, backdropPath));
        }

        return movieItems;

    }

    public static ArrayList<Review> getMovieReviewsDataFromJson(String reviewsJsonStr)
        throws JSONException {

        //These are the names of the Json objects that need to be extracted
        final String PMD_ID = "id";
        final String REVIEW_LIST = "results";
        final String REVIEW_ID = "id";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";
        final String REVIEW_URL = "url";

        JSONObject ReviewJson = new JSONObject(reviewsJsonStr);
    JSONObject reviewDataJson = ReviewJson.getJSONObject("reviews");
        JSONArray reviewArray = reviewDataJson.getJSONArray(REVIEW_LIST);
        ArrayList<Review> reviewItems = new ArrayList<Review>();

        reviewItems.clear();
        for(int i = 0; i < reviewArray.length(); i++) {
            JSONObject reviewMovies = reviewArray.getJSONObject(i);
            String reviewId = reviewMovies.getString(REVIEW_ID);
            String reviewAuthor = reviewMovies.getString(REVIEW_AUTHOR);
            String reviewContent = reviewMovies.getString(REVIEW_CONTENT);
            String reviewUrl = reviewMovies.getString(REVIEW_URL);
            reviewItems.add(new Review(reviewId, reviewAuthor, reviewContent, reviewUrl));
        }

        return reviewItems;
    }

}