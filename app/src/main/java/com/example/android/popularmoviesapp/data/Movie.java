package com.example.android.popularmoviesapp.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by da7th on 23/09/2016.
 */

//this here will be the a new object that stores each movie's relevant information with regards to
//the app so as to easily use it later
public class Movie implements Parcelable {

    //for the parcelable implementation
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    //the following are the paramters taken from the API for each entry and used in the app
    private String mPosterPath;
    private Boolean mAdult;
    private String mOverview;
    private String mReleaseDate;
    private int mId;
    private String mOriginalTitle;
    private String mOriginalLanguage;
    private String mTitle;
    private String mBackdropPath;
    private long mPopularity;
    private int mVoteCount;
    private Boolean mVideo;
    private double mVoteAverage;

    //define the movie object
    public Movie(String posterPath, Boolean adult, String overview, String releaseDate, int id,
                 String originalTitle, String originalLanguage, String title, String backdropPath,
                 long popularity, int voteCount, Boolean video, double voteAverage) {

        mPosterPath = posterPath;
        mAdult = adult;
        mOverview = overview;
        mReleaseDate = releaseDate;
        mId = id;
        mOriginalTitle = originalTitle;
        mOriginalLanguage = originalLanguage;
        mTitle = title;
        mBackdropPath = backdropPath;
        mPopularity = popularity;
        mVoteCount = voteCount;
        mVideo = video;
        mVoteAverage = voteAverage;
    }

    //the movie parcelable object creation
    public Movie(Parcel in) {
        this.mPosterPath = in.readString();
        this.mAdult = (in.readInt() == 1);
        this.mOverview = in.readString();
        this.mReleaseDate = in.readString();
        this.mId = in.readInt();
        this.mOriginalTitle = in.readString();
        this.mOriginalLanguage = in.readString();
        this.mTitle = in.readString();
        this.mBackdropPath = in.readString();
        this.mPopularity = in.readLong();
        this.mVoteCount = in.readInt();
        this.mVideo = (in.readInt() == 1);
        this.mVoteAverage = in.readDouble();
    }

    //the movie parcelable object types and defaults if needed
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPosterPath);
        dest.writeInt(mAdult ? 1 : 0);
        dest.writeString(mOverview);
        dest.writeString(mReleaseDate);
        dest.writeInt(mId);
        dest.writeString(mOriginalTitle);
        dest.writeString(mOriginalLanguage);
        dest.writeString(mTitle);
        dest.writeString(mBackdropPath);
        dest.writeLong(mPopularity);
        dest.writeInt(mVoteCount);
        dest.writeInt(mVideo ? 1 : 0);
        dest.writeDouble(mVoteAverage);
    }

    //not sure what this is...?
    public int describeContents() {
        return 0;
    }


    //the following are the public get methods to call parameters from the movie object as needed
    public String getPosterPath() {
        return mPosterPath;
    }

    public Boolean getAdult() {
        return mAdult;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public int getID() {
        return mId;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getOriginalLanguage() {
        return mOriginalLanguage;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public long getPopularity() {
        return mPopularity;
    }

    public int getVoteCount() {
        return mVoteCount;
    }

    public boolean getVideo() {
        return mVideo;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

}
