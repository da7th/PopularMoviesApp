package com.example.android.popularmoviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmoviesapp.data.MovieContract.MoviesSaved;


/**
 * Created by da7th on 04/10/2016.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = MovieDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 2;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //SQLite creation of the table with its basic rules
        String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesSaved.TABLE_NAME + " (" +
                MoviesSaved._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesSaved.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MoviesSaved.COLUMN_ADULT + " BOOLEAN NOT NULL, " +
                MoviesSaved.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MoviesSaved.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesSaved.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MoviesSaved.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MoviesSaved.COLUMN_ORIGINAL_LANGUAGE + " TEXT NOT NULL, " +
                MoviesSaved.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesSaved.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                MoviesSaved.COLUMN_POPULARITY + " LONG NOT NULL, " +
                MoviesSaved.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, " +
                MoviesSaved.COLUMN_VIDEO + " BOOLEAN NOT NULL, " +
                MoviesSaved.COLUMN_VOTE_AVERAGE + " DOUBLE NOT NULL, " +
                MoviesSaved.COLUMN_TRAILER + " TEXT NOT NULL, " +
                MoviesSaved.COLUMN_REVIEWS + " TEXT NOT NULL, " +
                MoviesSaved.COLUMN_FAV + " INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);

        //SQLite creation of the table with its basic rules
        String SQL_CREATE_POPULAR_MOVIES_TABLE = "CREATE TABLE " + MovieContract.MoviesSavedPopularity.TABLE_NAME + " (" +
                MoviesSaved._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesSaved.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MoviesSaved.COLUMN_ADULT + " BOOLEAN NOT NULL, " +
                MoviesSaved.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MoviesSaved.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesSaved.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MoviesSaved.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MoviesSaved.COLUMN_ORIGINAL_LANGUAGE + " TEXT NOT NULL, " +
                MoviesSaved.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesSaved.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                MoviesSaved.COLUMN_POPULARITY + " LONG NOT NULL, " +
                MoviesSaved.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, " +
                MoviesSaved.COLUMN_VIDEO + " BOOLEAN NOT NULL, " +
                MoviesSaved.COLUMN_VOTE_AVERAGE + " DOUBLE NOT NULL, " +
                MoviesSaved.COLUMN_TRAILER + " TEXT NOT NULL, " +
                MoviesSaved.COLUMN_REVIEWS + " TEXT NOT NULL, " +
                MoviesSaved.COLUMN_FAV + " INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_POPULAR_MOVIES_TABLE);

        //SQLite creation of the table with its basic rules
        String SQL_CREATE_FAV_MOVIES_TABLE = "CREATE TABLE " + MovieContract.FavMovies.TABLE_NAME + " (" +
                MovieContract.FavMovies._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.FavMovies.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.FavMovies.COLUMN_ADULT + " BOOLEAN NOT NULL, " +
                MovieContract.FavMovies.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.FavMovies.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.FavMovies.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.FavMovies.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieContract.FavMovies.COLUMN_ORIGINAL_LANGUAGE + " TEXT NOT NULL, " +
                MovieContract.FavMovies.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.FavMovies.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                MovieContract.FavMovies.COLUMN_POPULARITY + " LONG NOT NULL, " +
                MovieContract.FavMovies.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, " +
                MovieContract.FavMovies.COLUMN_VIDEO + " BOOLEAN NOT NULL, " +
                MovieContract.FavMovies.COLUMN_VOTE_AVERAGE + " DOUBLE NOT NULL, " +
                MovieContract.FavMovies.COLUMN_TRAILER + " TEXT NOT NULL, " +
                MovieContract.FavMovies.COLUMN_REVIEWS + " TEXT NOT NULL, " +
                MovieContract.FavMovies.COLUMN_FAV + " INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_FAV_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
