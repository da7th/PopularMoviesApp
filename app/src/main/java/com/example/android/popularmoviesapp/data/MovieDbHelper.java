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
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

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
                MoviesSaved.COLUMN_VOTE_AVERAGE + " DOUBLE NOT NULL );";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
