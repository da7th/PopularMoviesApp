package com.example.android.popularmoviesapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popularmoviesapp.data.MovieContract.MoviesSaved;

import static com.example.android.popularmoviesapp.data.MovieContract.CONTENT_AUTHORITY;
import static com.example.android.popularmoviesapp.data.MovieContract.MoviesSaved.TABLE_NAME;
import static com.example.android.popularmoviesapp.data.MovieContract.PATH_MOVIES;

/**
 * Created by da7th on 04/10/2016.
 */

public class MovieProvider extends ContentProvider {

    public static final String LOG_TAG = MovieProvider.class.getSimpleName();
    private static final int MOVIES = 100;
    private static final int MOVIE_ID = 101;
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIES, MOVIES);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIES + "/#", MOVIE_ID);
    }

    private MovieDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);

        switch (match) {

            case MOVIES:

                cursor = database.query(MoviesSaved.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_ID:

                selection = MoviesSaved._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(MoviesSaved.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:

                throw new IllegalArgumentException("Cannot Query Unknown URI " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        //TODO: DATA VALIDATION SHOULD GO HERE


        int match = sUriMatcher.match(uri);
        switch (match) {

            case MOVIES:
                return insertMovie(uri, values);
            default:
                throw new IllegalArgumentException("Insertion not supported for: " + uri);
        }
    }

    private Uri insertMovie(Uri uri, ContentValues values) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(TABLE_NAME, null, values);

        if (id == -1) {

            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:

                return database.delete(MoviesSaved.TABLE_NAME, selection, selectionArgs);
            case MOVIE_ID:

                selection = MoviesSaved._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return database.delete(TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return update(uri, values, selection, selectionArgs);
            case MOVIE_ID:
                selection = MoviesSaved._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateMovie(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for: " + uri);
        }
    }

    private int updateMovie(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //TODO: DATA VALIDATION GOES HERE

        if (values.size() == 0) {

            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        return database.update(TABLE_NAME, values, selection, selectionArgs);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
