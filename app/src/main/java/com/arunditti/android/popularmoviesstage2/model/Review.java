package com.arunditti.android.popularmoviesstage2.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arunditti on 5/2/18.
 */

public class Review implements Parcelable {

    public String movieId;
    String mAuthor;
    String mContent;
    String mUrl;

    public Review(String id, String author, String content, String url) {
        this.movieId = id;
        this.mAuthor = author;
        this.mContent = content;
        this.mUrl = url;
    }

    public Review(Parcel in) {
        movieId = in.readString();
        mAuthor = in.readString();
        mContent = in.readString();
        mUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return movieId + mAuthor + mContent;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(movieId);
        dest.writeString(mAuthor);
        dest.writeString(mContent);
        dest.writeString(mUrl);
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel parcel) {
            return new Review(parcel);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getItemId() {
        return movieId;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    public String getUrl() {
        return mUrl;
    }
}