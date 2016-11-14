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
import static com.example.android.popularmoviesapp.data.MovieContract.PATH_FAV;
import static com.example.android.popularmoviesapp.data.MovieContract.PATH_MOVIES;


public class MovieProvider extends ContentProvider {

    public static final String LOG_TAG = MovieProvider.class.getSimpleName();

    //variables for each type of uri that might be used by the user to communicate with the provider
    private static final int MOVIES = 100;
    private static final int MOVIE_ID = 101;
    private static final int FAV = 102;
    private static final int FAV_ID = 103;
    private MovieDbHelper mDbHelper;

    //uri matcher initialised as a NO_MATCH by default
    static UriMatcher sUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String authority = CONTENT_AUTHORITY;

        sUriMatcher.addURI(authority, PATH_MOVIES, MOVIES);
        sUriMatcher.addURI(authority, PATH_MOVIES + "/#", MOVIE_ID);
        sUriMatcher.addURI(authority, PATH_MOVIES + "/" + PATH_FAV, FAV);
        sUriMatcher.addURI(authority, PATH_MOVIES + "/" + PATH_FAV + "/#", FAV_ID);

        return sUriMatcher;
    }

    //create a connection with the mDbHelper to communicate with the database
    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    //the query method allows the seach for all/specific items from the database
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        //get a readable instance of the database to perform the query on
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        //match the input uri the available cases
        int match = sUriMatcher().match(uri);

        switch (match) {
            case MOVIES:

                //if the uri is matched to all movies in the database then the return cursor is set
                // to all the movies.
                cursor = database.query(MoviesSaved.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_ID:

                //if the uri is matched to a specific id, then the selection is set to the _id and
                // the selectionArgs is set to the input id to be retrieved
                selection = MoviesSaved._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(MoviesSaved.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case FAV:

                //if the uri is matched to all movies in the database then the return cursor is set
                // to all the movies.
                cursor = database.query(MovieContract.FavMovies.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case FAV_ID:

                //if the uri is matched to a specific id, then the selection is set to the _id and
                // the selectionArgs is set to the input id to be retrieved
                selection = MovieContract.FavMovies.COLUMN_TITLE + "=?";
                cursor = database.query(MovieContract.FavMovies.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:

                cursor = database.query(MoviesSaved.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
                //throw an exception if the input uri doesn't match any of the cases
            //throw new IllegalArgumentException("Cannot Query Unknown URI " + uri);
        }

        //notify the contentResolver of changes made
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        //return the cursor of the successful query
        return cursor;
    }

    //method to insert new content to the database
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        //TODO: DATA VALIDATION SHOULD GO HERE

        Uri inserted;
        int match = sUriMatcher().match(uri);
        switch (match) {

            case MOVIES:
                inserted = insertMovie(uri, values);
                break;
            case FAV:
                inserted = insertFavMovie(uri, values);
                break;
            default:
                throw new IllegalArgumentException("Insertion not supported for: " + uri);
        }

        //notify the uri of a change
        getContext().getContentResolver().notifyChange(uri, null);

        return inserted;
    }

    //helper method for the insert method
    private Uri insertMovie(Uri uri, ContentValues values) {

        //calls a writable instance of the database to insert new data to it
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //perform the insert method and check the return value for successful entry to the database
        long id = database.insert(TABLE_NAME, null, values);
        if (id == -1) {

            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //return the new content uri and the assigned id to the insert method.
        return ContentUris.withAppendedId(uri, id);
    }

    //helper method for the insert method
    private Uri insertFavMovie(Uri uri, ContentValues values) {

        //calls a writable instance of the database to insert new data to it
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //perform the insert method and check the return value for successful entry to the database
        long id = database.insert(MovieContract.FavMovies.TABLE_NAME, null, values);
        if (id == -1) {

            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //return the new content uri and the assigned id to the insert method.
        return ContentUris.withAppendedId(uri, id);
    }

    //this method helps delete either a single item or all items from the database
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        //get a writable instance of the database to be able to delete from it
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //number of rows deleted
        int rowsDeleted = 0;

        //match the input uri to the available cases
        int match = sUriMatcher().match(uri);
        switch (match) {
            case MOVIES:

                //this case deletes all entries from the database
                rowsDeleted = database.delete(MoviesSaved.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_ID:

                //if the uri is matched to a specific id, then the selection is set to the _id and
                // the selectionArgs is set to the input id to be deleted
                selection = MoviesSaved._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case FAV:

                //this case deletes all entries from the database
                rowsDeleted = database.delete(MovieContract.FavMovies.TABLE_NAME, selection, selectionArgs);
                break;
            case FAV_ID:

                //if the uri is matched to a specific id, then the selection is set to the _id and
                // the selectionArgs is set to the input id to be deleted
                selection = MovieContract.FavMovies._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(MovieContract.FavMovies.TABLE_NAME, selection, selectionArgs);
                break;
            default:

                //an exceptions is thrown for an unmatched case
                throw new IllegalArgumentException("Deletion is not supported for: " + uri);
        }

        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    //this method updates the database or the database elements.
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //number of rows updated
        int rowsUpdated = 0;

        //match the input uri to the available cases
        int match = sUriMatcher().match(uri);
        switch (match) {
            case MOVIES:

                //for the case of all items, replace all the content with the input parameters
                rowsUpdated = update(uri, values, selection, selectionArgs);
                break;
            case MOVIE_ID:

                //for the case of a single item, replace the item at the given id with the new input values.
                selection = MoviesSaved._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = updateMovie(uri, values, selection, selectionArgs);
                break;
            case FAV:

                //for the case of all items, replace all the content with the input parameters
                rowsUpdated = update(uri, values, selection, selectionArgs);
                break;
            case FAV_ID:

                //for the case of a single item, replace the item at the given id with the new input values.
                selection = MovieContract.FavMovies._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = updateFavMovie(uri, values, selection, selectionArgs);
                break;
            default:

                throw new IllegalArgumentException("Update is not supported for: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    //database update helper method to check if the input values are valid and return the database update
    private int updateMovie(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //TODO: DATA VALIDATION GOES HERE

        //check the size of the input values, so that if they're empty to stop the method.
        if (values.size() == 0) {

            return 0;
        }

        //get a writable instance of the database to allow updating
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //return the update of the database
        return database.update(TABLE_NAME, values, selection, selectionArgs);
    }

    //database update helper method to check if the input values are valid and return the database update
    private int updateFavMovie(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //TODO: DATA VALIDATION GOES HERE

        //check the size of the input values, so that if they're empty to stop the method.
        if (values.size() == 0) {

            return 0;
        }

        //get a writable instance of the database to allow updating
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //return the update of the database
        return database.update(MovieContract.FavMovies.TABLE_NAME, values, selection, selectionArgs);
    }

    //getType method for the content provider.
    @Nullable
    @Override
    public String getType(Uri uri) {

        //match the input uri to the available cases and return the correct String uri for the type
        int match = sUriMatcher().match(uri);

        switch (match) {

            case MOVIE_ID:
                return MoviesSaved.CONTENT_ITEM_TYPE;
            case MOVIES:
                return MoviesSaved.CONTENT_TYPE;
            case FAV:
                return MoviesSaved.CONTENT_ITEM_TYPE;
            case FAV_ID:
                return MoviesSaved.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}
