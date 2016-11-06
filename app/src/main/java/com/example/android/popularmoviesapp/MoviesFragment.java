package com.example.android.popularmoviesapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIE_LOADER = 0;
    private static int COL__ID = 0;
    private GridCursorAdapter mMovieAdapter;
    private int mPosition;
    private GridView mGridView;

    //default constructor for the class
    public MoviesFragment(){
    }

    //first method to be called upon launch
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this activity has an options menu
        setHasOptionsMenu(true);
    }

    //the method for actually creating the fragment view.
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {

        //initialise the cursor adapter with context and null cursor
        mMovieAdapter = new GridCursorAdapter(getContext(), null);

        //define the rootView, gridView, gridView empty
        //and also set the Movie cursor adapter to the gridView as well as the emptyView for the
        //grid
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.movie_grid);
        View gridEmptyView = rootView.findViewById(R.id.grid_empty_view);
        mGridView.setAdapter(mMovieAdapter);
        mGridView.setEmptyView(gridEmptyView);

        //set the onItemClickListener for the gridView
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                mPosition = position;
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(MovieContract.buildMovieUri(
                                    cursor.getInt(COL__ID)
                            ));
                }
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey("mPosition")) {
            mPosition = savedInstanceState.getInt("mPosition", 0);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt("mPosition", mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    //
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        // Prepare the loader. Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        //obtain an instance of the connectivityManager for the connectivity_service,
        //obtain an instance of networkInfo to get the active network information
        //check if the network is both connected and not null,
        //if so make a call to populate the gridView.
        //else show the user a toast to check their network connection
        ConnectivityManager connMg = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMg.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

        populateGrid();
        } else {

            Toast.makeText(getContext(), "Unable to establish connection error.", Toast.LENGTH_LONG).show();
        }
    }

    //method to start the grid population
    public void populateGrid() {

        //create an instance of the fetchMovies task
        //check the shared preferences for the sorting preference and add that to the call before execution.
        fetchMoviesTask fetchMovies = new fetchMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOption = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.sort_options_value_popular));

        //execute the fetchMovies task with the sorting option attached
        fetchMovies.execute(sortOption);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //the sorting order for the loaded items
        //in ascending order of the item's _id
        String sortOrder = MovieContract.MoviesSaved._ID + " ASC";

        //define the new cursor loader.
        return new CursorLoader(getActivity(),
                MovieContract.MoviesSaved.CONTENT_URI,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        //swap the cursor in the cursorAdapter with the one passed on to this method
        mMovieAdapter.swapCursor(cursor);

        if (mPosition != GridView.INVALID_POSITION) {
            mGridView.setSelection(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        //when the loader resets, swap the cursor with null
        mMovieAdapter.swapCursor(null);
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Uri dateUri);
    }

    //background task thread for fetching the data from online and storing it to the database for
    //use in the app
    public class fetchMoviesTask extends AsyncTask<String, Void, Void> {

        final private String LOG_TAG = fetchMoviesTask.class.getSimpleName();

        //the specific background execution method with the input being the String for the sort
        // type to get the data from
        @Override
        protected Void doInBackground(String... params) {

            //initialise the connection parameters
            HttpsURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = "";

            //enclose in a try/catch due to multiple errors that can occur during the connection
            // and data retrieval processes
            try {

                //url building parameters
                final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";
                final String ORDER_MODE = params[0];
                final String API_KEY = "api_key";

                //building the uri and creating the full url
                Uri movieDBUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendPath(ORDER_MODE)
                        .appendQueryParameter(API_KEY, BuildConfig.MOVIES_DB_API_KEY)
                        .build();
                URL movieDBUrl = new URL(movieDBUri.toString());

                //performing the connection to the url
                urlConnection = (HttpsURLConnection) movieDBUrl.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //initialise buffer, grab inputStream, and check the inputstream contents
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                if (inputStream == null) {

                    return null;
                }

                //set the contents of the bufferReader to the result of setting a new
                // inputStreamReader on the inputStream, initialise a String variable line to store
                // the String result line by line into the buffer, and finally check the whether or
                // not the buffer is empty
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {

                    return null;
                }

                //convert the buffer to a string in order to proceed with extracting the data
                movieJsonStr = buffer.toString();
            } catch (IOException e) {

                //error thrown during the connection and assigning the string result processes
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {

                    //if the url is not disconnected by this point, disconnect the connection
                    urlConnection.disconnect();
                }

                if (reader != null) {

                    //if the bufferReader is still open attempt to close it within a try/catch in
                    // case there are errors
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            //attempt to invoke the getMovieDataFromJson helper method to extract the data from the
            // json string created above, any errors are caught and printed (stackTrace, Log.e)
            try {
                getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        private String getTrailer(int id) {

            //initialise the connection parameters
            HttpsURLConnection urlConnection = null;
            BufferedReader reader = null;
            String trailerJsonStr = "";

            //enclose in a try/catch due to multiple errors that can occur during the connection
            // and data retrieval processes
            try {

                //url building parameters
                final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";
                final String TRAILERS_MODE = "videos";
                final String API_KEY = "api_key";

                //building the uri and creating the full url
                Uri movieDBUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendPath("" + id)
                        .appendPath(TRAILERS_MODE)
                        .appendQueryParameter(API_KEY, BuildConfig.MOVIES_DB_API_KEY)
                        .build();
                URL movieDBUrl = new URL(movieDBUri.toString());

                //performing the connection to the url
                urlConnection = (HttpsURLConnection) movieDBUrl.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //initialise buffer, grab inputStream, and check the inputstream contents
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                if (inputStream == null) {
                    return null;
                }

                //set the contents of the bufferReader to the result of setting a new
                // inputStreamReader on the inputStream, initialise a String variable line to store
                // the String result line by line into the buffer, and finally check the whether or
                // not the buffer is empty
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                //convert the buffer to a string in order to proceed with extracting the data
                trailerJsonStr = buffer.toString();

                return trailerJsonStr;
            } catch (IOException e) {

                //error thrown during the connection and assigning the string result processes
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {

                    //if the url is not disconnected by this point, disconnect the connection
                    urlConnection.disconnect();
                }
                if (reader != null) {

                    //if the bufferReader is still open attempt to close it within a try/catch in
                    // case there are errors
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return "No Trailers";
        }

        private String getReviews(int id) {

            //initialise the connection parameters
            HttpsURLConnection urlConnection = null;
            BufferedReader reader = null;
            String reviewsJsonStr = "";

            //enclose in a try/catch due to multiple errors that can occur during the connection
            // and data retrieval processes
            try {

                //url building parameters
                final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";
                final String TRAILERS_MODE = "reviews";
                final String API_KEY = "api_key";

                //building the uri and creating the full url
                Uri movieDBUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendPath("" + id)
                        .appendPath(TRAILERS_MODE)
                        .appendQueryParameter(API_KEY, BuildConfig.MOVIES_DB_API_KEY)
                        .build();
                URL movieDBUrl = new URL(movieDBUri.toString());

                //performing the connection to the url
                urlConnection = (HttpsURLConnection) movieDBUrl.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //initialise buffer, grab inputStream, and check the inputstream contents
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                if (inputStream == null) {
                    return null;
                }

                //set the contents of the bufferReader to the result of setting a new
                // inputStreamReader on the inputStream, initialise a String variable line to store
                // the String result line by line into the buffer, and finally check the whether or
                // not the buffer is empty
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                //convert the buffer to a string in order to proceed with extracting the data
                reviewsJsonStr = buffer.toString();

                return reviewsJsonStr;
            } catch (IOException e) {

                //error thrown during the connection and assigning the string result processes
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {

                    //if the url is not disconnected by this point, disconnect the connection
                    urlConnection.disconnect();
                }
                if (reader != null) {

                    //if the bufferReader is still open attempt to close it within a try/catch in
                    // case there are errors
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return "No Reviews";
        }

        //method to extract the data from the json string, extract the values for each object and
        // store them in a database
        private void getMovieDataFromJson(String movieJsonStr) throws JSONException {

            //String variables for all the json items
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

            //find the root object and extract the jsonArray
            JSONObject rootObject = new JSONObject(movieJsonStr);
            JSONArray resultsArray = rootObject.getJSONArray(MDB_RESULTS);

            //if the connection is successful and a new set of data is acquired delete the old data
            // from the database
            int delete = getContext().getContentResolver().delete(MovieContract.MoviesSaved.CONTENT_URI, null, null);

            //a loop through all the objects within the jsonArray to be stored into a databse
            for (int i = 0; i < resultsArray.length(); i++) {

                JSONObject movieObject = resultsArray.getJSONObject(i);

                //extract the required information from the jsonObject
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

                //format the full path for the poster and backdrop links
                posterPath = "http://image.tmdb.org/t/p/" + getResources().getString(R.string.poster_quality) + posterPath;
                backdropPath = "http://image.tmdb.org/t/p/" + getResources().getString(R.string.backdrop_quality) + backdropPath;

                //create a new ContentValues object and place all the new information into the
                // object to be stored into the database
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
                values.put(MovieContract.MoviesSaved.COLUMN_TRAILER, getTrailer(id));
                values.put(MovieContract.MoviesSaved.COLUMN_REVIEWS, getReviews(id));
                values.put(MovieContract.MoviesSaved.COLUMN_FAV, 0);

                //insert the item to the database through the content provider
                Uri inserted = getContext().getContentResolver().insert(MovieContract.MoviesSaved.CONTENT_URI, values);

                //just logging the inserted items for debugging purposes
                Log.d(MoviesFragment.class.getSimpleName(), title);


            }
        }
    }
}
