package com.arunditti.android.popularmoviesstage2.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by arunditti on 5/2/18.
 */

public class Trailer implements Parcelable {

    String movieId;
    String mKey;
    String mName;
    String mSite;

    public Trailer(String id, String key, String name, String site) {
        this.movieId = id;
        this.mKey = key;
        this.mName = name;
        this.mSite = site;
    }

    public Trailer(Parcel in) {
        movieId = in.readString();
        mKey = in.readString();
        mName = in.readString();
        mSite = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return movieId + mKey + mName + mSite;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(movieId);
        dest.writeString(mKey);
        dest.writeString(mName);
        dest.writeString(mSite);
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel parcel) {
            return new Trailer(parcel);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    public String getItemId() {
        return movieId;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String mKey) {
        this.mKey = mKey;
    }

    public String getTrailerName() {
        return mName;
    }

    public void setTrailerName(String mName) {
        this.mName = mName;
    }

    public String getSite() {
        return mSite;
    }

    public void setSite(String site) {
        this.mSite = site;
    }

    public String getTrailerImage() {
        return "http://img.youtube.com/vi/" + mKey + "/0.jpg";
    }
}
