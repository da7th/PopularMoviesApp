package com.example.android.popularmoviesapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.popularmoviesapp.data.GridCursorAdapter;
import com.example.android.popularmoviesapp.data.MovieContract;
import com.example.android.popularmoviesapp.data.MovieDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by da7th on 23/09/2016.
 */

public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIE_LOADER = 0;
    public static int mPosition;
    boolean mTwoPane;
    private SQLiteOpenHelper mDbHelper;
    private GridCursorAdapter mMovieAdapter;


    public MoviesFragment(){
    }

    public static int getPosition() {
        return mPosition;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new MovieDbHelper(getContext());
        setHasOptionsMenu(true);
        mPosition = 0;

        Intent intent = getActivity().getIntent();
        mTwoPane = intent.getBooleanExtra("mTwoPane", false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        mMovieAdapter = new GridCursorAdapter(getContext(), null);

        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.movie_grid);

        View gridEmptyView = rootView.findViewById(R.id.grid_empty_view);

        gridView.setAdapter(mMovieAdapter);
        gridView.setEmptyView(gridEmptyView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    if (mTwoPane == false) {
                    }
                }
            }
        });

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        ConnectivityManager connMg = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMg.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
        populateGrid();
        } else {
            Log.v("ERROR", "Trying to stop it here");
            Toast.makeText(getContext(), "Unable to establish connection error.", Toast.LENGTH_LONG).show();
        }
    }

    public void populateGrid() {
        fetchMoviesTask fetchMovies = new fetchMoviesTask();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOption = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.sort_options_value_popular));

        fetchMovies.execute(sortOption);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = MovieContract.MoviesSaved._ID + " ASC";
        return new CursorLoader(getActivity(),
                MovieContract.BASE_CONTENT_URI.buildUpon().
                        appendPath(MovieContract.PATH_MOVIES).build(),
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mMovieAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    public class fetchMoviesTask extends AsyncTask<String, Void, Void> {

        final private String LOG_TAG = fetchMoviesTask.class.getSimpleName();

        @Override
        protected Void doInBackground(String... params) {

            if (params.length == 0) {

            }


            HttpsURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = "";


            try {

                final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";
                final String ORDER_MODE = params[0];
                final String API_KEY = "api_key";


                Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendPath(ORDER_MODE)
                        .appendQueryParameter(API_KEY, BuildConfig.MOVIES_DB_API_KEY)
                        .build();

                URL url = new URL(uri.toString());

                Log.v(LOG_TAG, url.toString());

                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {

                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {

                    return null;
                }

                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {

                    urlConnection.disconnect();
                }

                if (reader != null) {

                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }


        private void getMovieDataFromJson(String movieJsonStr) throws JSONException {

            final String MDB_POSTER_PATH = "poster_path";
            final String MDB_ADULT = "adult";
            final String MDB_OVERVIEW = "overview";
            final String MDB_RELEASE_DATE = "release_date";
            final String MDB_ID = "id";
            final String MDB_ORIGINAL_TITLE = "original_title";
            final String MDB_ORIGINAL_LANGUAGE = "original_language";
            final String MDB_TITLE = "title";
            final String MDB_BACKDROP_PATH = "backdrop_path";
            final String MDB_POPULARITY = "popularity";
            final String MDB_VOTE_COUNT = "vote_count";
            final String MDB_VIDEO = "video";
            final String MDB_VOTE_AVERAGE = "vote_average";

            final String MDB_RESULTS = "results";

            String posterPath;
            Boolean adult;
            String overview;
            String releaseDate;
            int id;
            String originalTitle;
            String originalLanguage;
            String title;
            String backdropPath;
            long popularity;
            int voteCount;
            Boolean video;
            double voteAverage;

            JSONObject rootObject = new JSONObject(movieJsonStr);

            JSONArray resultsArray = rootObject.getJSONArray(MDB_RESULTS);

            SQLiteDatabase database = mDbHelper.getWritableDatabase();

            database.delete(MovieContract.MoviesSaved.TABLE_NAME, null, null);

            for (int i = 0; i < resultsArray.length(); i++) {

                JSONObject movieObject = resultsArray.getJSONObject(i);

                posterPath = movieObject.getString(MDB_POSTER_PATH);
                adult = movieObject.getBoolean(MDB_ADULT);
                overview = movieObject.getString(MDB_OVERVIEW);
                releaseDate = movieObject.getString(MDB_RELEASE_DATE);
                id = movieObject.getInt(MDB_ID);
                originalTitle = movieObject.getString(MDB_ORIGINAL_TITLE);
                originalLanguage = movieObject.getString(MDB_ORIGINAL_LANGUAGE);
                title = movieObject.getString(MDB_TITLE);
                backdropPath = movieObject.getString(MDB_BACKDROP_PATH);
                popularity = movieObject.getLong(MDB_POPULARITY);
                voteCount = movieObject.getInt(MDB_VOTE_COUNT);
                video = movieObject.getBoolean(MDB_VIDEO);
                voteAverage = movieObject.getDouble(MDB_VOTE_AVERAGE);

                posterPath = "http://image.tmdb.org/t/p/" + getResources().getString(R.string.poster_quality) + posterPath;
                backdropPath = "http://image.tmdb.org/t/p/" + getResources().getString(R.string.backdrop_quality) + backdropPath;

                ContentValues values = new ContentValues();
                values.put(MovieContract.MoviesSaved.COLUMN_POSTER_PATH, posterPath);
                values.put(MovieContract.MoviesSaved.COLUMN_ADULT, adult);
                values.put(MovieContract.MoviesSaved.COLUMN_OVERVIEW, overview);
                values.put(MovieContract.MoviesSaved.COLUMN_RELEASE_DATE, releaseDate);
                values.put(MovieContract.MoviesSaved.COLUMN_MOVIE_ID, id);
                values.put(MovieContract.MoviesSaved.COLUMN_ORIGINAL_TITLE, originalTitle);
                values.put(MovieContract.MoviesSaved.COLUMN_ORIGINAL_LANGUAGE, originalLanguage);
                values.put(MovieContract.MoviesSaved.COLUMN_TITLE, title);
                values.put(MovieContract.MoviesSaved.COLUMN_BACKDROP_PATH, backdropPath);
                values.put(MovieContract.MoviesSaved.COLUMN_POPULARITY, popularity);
                values.put(MovieContract.MoviesSaved.COLUMN_VOTE_COUNT, voteCount);
                values.put(MovieContract.MoviesSaved.COLUMN_VIDEO, video);
                values.put(MovieContract.MoviesSaved.COLUMN_VOTE_AVERAGE, voteAverage);

                database.insert(MovieContract.MoviesSaved.TABLE_NAME, null, values);

                Log.d(MoviesFragment.class.getSimpleName(), posterPath + "\n" + getResources().getString(R.string.poster_quality) + "\n" + adult + "\n" +
                        overview + "\n" + releaseDate + "\n" + id + "\n" + originalTitle + "\n" +
                        originalLanguage + "\n" + title + "\n" + backdropPath + "\n" + getResources().getString(R.string.backdrop_quality) + "\n" + popularity +
                        "\n" + voteCount + "\n" + video + "\n" + voteAverage);
            }
        }

    }
}
