package com.example.android.popularmoviesapp;

/**
 * Created by da7th on 23/09/2016.
 */

//this here will be the a new object that stores each movie's relevant information with regards to
// the app so as to easily use it later
public class Movie {

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
    private float mPopularity;
    private int mVoteCount;
    private Boolean mVideo;
    private double mVoteAverage;

    public Movie(String posterPath, Boolean adult, String overview, String releaseDate, int id,
                 String originalTitle, String originalLanguage, String title, String backdropPath,
                 float popularity, int voteCount, Boolean video, double voteAverage) {

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

    public float getPopularity() {
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
