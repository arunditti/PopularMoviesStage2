package com.arunditti.android.popularmoviesstage2.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.SimpleTimeZone;

/**
 * Created by arunditti on 5/2/18.
 */

public class MovieItem implements Parcelable {

    public String movieId;
    public String mMovieTitle;
    public String mMovieReleaseDate;
    public String mOverview;
    public String mRating;
    public String mImagePath;
    public String mBackdropPath;

    public MovieItem(String id, String title, String releaseDate, String movieOverview, String rating, String imagePath, String backdropPath) {
        this.movieId = id;
        this.mMovieTitle = title;
        this.mMovieReleaseDate = releaseDate;
        this.mOverview = movieOverview;
        this.mImagePath = imagePath;
        this.mRating = rating;
        this.mBackdropPath = backdropPath;
    }

    public MovieItem(Parcel in) {
        movieId = in.readString();
        mMovieTitle = in.readString();
        mMovieReleaseDate = in.readString();
        mOverview = in.readString();
        mImagePath = in.readString();
        mRating = in.readString();
        mBackdropPath = in.readString();
    }

    public MovieItem() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return movieId + mMovieTitle + mMovieReleaseDate + mOverview + mImagePath + mRating + mBackdropPath;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(movieId);
        dest.writeString(mMovieTitle);
        dest.writeString(mMovieReleaseDate);
        dest.writeString(mOverview);
        dest.writeString(mImagePath);
        dest.writeString(mRating);
        dest.writeString(mBackdropPath);
    }

    public static final Parcelable.Creator<MovieItem> CREATOR = new Parcelable.Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel parcel) {
            return new MovieItem(parcel);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    public String getItemId() {
        return movieId;
    }

//    public void setMovieId(String id) {
//        this.movieId = id;
//    }

    public String getMovieTitle() {
        return mMovieTitle;
    }

//    public void setmMovieTitle(String title) {
//        this.mMovieTitle = title;
//    }

    public String getmMovieReleaseDate() {
        return mMovieReleaseDate;
    }

//    public void setmMovieReleaseDate(String releaseDate) {
//        this.mMovieReleaseDate = releaseDate;
//    }

    public String getmOverview() {
        return mOverview;
    }

//    public void setmOverview(String overview) {
//        this.mOverview = overview;
//    }

    public String getmImagePath() {
        return mImagePath;
    }

//    public void setmImagePath (String imagePath) {
//        this.mImagePath = imagePath;
//    }

    public String getmRating() {
        return mRating;
    }

    //public void setmRating (String rating) {
//        this.mRating = rating;
//    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

//    public void setmBackdropPath (String backdropPath) {
//        this.mBackdropPath = backdropPath;
//    }


}
