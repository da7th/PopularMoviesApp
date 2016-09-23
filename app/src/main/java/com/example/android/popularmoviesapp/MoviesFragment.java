package com.example.android.popularmoviesapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by da7th on 23/09/2016.
 */

public class MoviesFragment extends Fragment {

    private MovieAdapater mMovieAdapter;

    public MoviesFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(MoviesFragment.class.getSimpleName(), "In onCreateView");

        mMovieAdapter = new MovieAdapater(getContext(), new ArrayList<Movie>());
        Log.d(MoviesFragment.class.getSimpleName(), "Movie adapter created");

        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        Log.d(MoviesFragment.class.getSimpleName(), "rootView fragment inflated");

        GridView gridView = (GridView) rootView.findViewById(R.id.movie_grid);
        Log.d(MoviesFragment.class.getSimpleName(), "GridView found");

        gridView.setAdapter(mMovieAdapter);
        Log.d(MoviesFragment.class.getSimpleName(), "adapter set on gridview");

        return rootView;

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(MoviesFragment.class.getSimpleName(), "inside OnStart method");

        populateGrid();
        Log.d(MoviesFragment.class.getSimpleName(), "populate grid called");
    }

    private void populateGrid() {
        fetchMoviesTask fetchMovies = new fetchMoviesTask();
        Log.d(MoviesFragment.class.getSimpleName(), "inside populate grid method, fetchMovie task created");

        fetchMovies.execute("popular");
        Log.d(MoviesFragment.class.getSimpleName(), "inside populate grid, fetchMovies executed");
    }

    public class fetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

        final private String LOG_TAG = fetchMoviesTask.class.getSimpleName();

        @Override
        protected Movie[] doInBackground(String... params) {


            Log.d(fetchMoviesTask.class.getSimpleName(), "inside AsyncTask, doInBackground");

            if (params.length == 0) {

                Log.d(MoviesFragment.class.getSimpleName(), "params length = 0 returning null");

                return null;
            }


            HttpsURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = "";
            Log.d(MoviesFragment.class.getSimpleName(), "instantiated httpconnection, reader and jsonstring variables");


            try {


                Log.d(MoviesFragment.class.getSimpleName(), "inside the try statement");

                final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";
                final String ORDER_MODE = params[0];
                final String API_KEY = "api_key";


                Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendPath(ORDER_MODE)
                        .appendQueryParameter(API_KEY, BuildConfig.MOVIES_DB_API_KEY)
                        .build();
                Log.d(MoviesFragment.class.getSimpleName(), "uri built");

                URL url = new URL(uri.toString());
                Log.d(MoviesFragment.class.getSimpleName(), "url made: " + url);

                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                Log.d(MoviesFragment.class.getSimpleName(), "url connected");

                InputStream inputStream = urlConnection.getInputStream();
                Log.d(MoviesFragment.class.getSimpleName(), "inputstream received");

                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {

                    Log.d(MoviesFragment.class.getSimpleName(), "inputstream = null");
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                Log.d(MoviesFragment.class.getSimpleName(), "reading inputStream to reader");

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                    Log.d(MoviesFragment.class.getSimpleName(), "appended line to buffer");

                }

                if (buffer.length() == 0) {

                    Log.d(MoviesFragment.class.getSimpleName(), "buffer length = 0");
                    return null;
                }

                movieJsonStr = buffer.toString();
                Log.d(MoviesFragment.class.getSimpleName(), "changed buffer to string");

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(MoviesFragment.class.getSimpleName(), "caught IO exception");
            } finally {
                if (urlConnection != null) {

                    Log.d(MoviesFragment.class.getSimpleName(), "url disconnected");
                    urlConnection.disconnect();
                }

                if (reader != null) {

                    try {
                        reader.close();
                        Log.d(MoviesFragment.class.getSimpleName(), "reader closed");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(MoviesFragment.class.getSimpleName(), "closing reader exception caught");
                    }
                }
            }

            try {
                Log.d(MoviesFragment.class.getSimpleName(), "getting movie data from json");
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }


        private Movie[] getMovieDataFromJson(String movieJsonStr) throws JSONException {


            Log.d(MoviesFragment.class.getSimpleName(), "getMovie data from json entered");

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

            Movie[] movies = new Movie[resultsArray.length()];

            for (int i = 0; i < resultsArray.length(); i++) {

                Log.d(MoviesFragment.class.getSimpleName(), "inside the loop for movie objects");

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

                posterPath = "http://image.tmdb.org/t/p/w500" + posterPath;
                backdropPath = "http://image.tmdb.org/t/p/w500" + backdropPath;

                movies[i] = new Movie(posterPath, adult, overview, releaseDate, id, originalTitle,
                        originalLanguage, title, backdropPath, popularity, voteCount, video, voteAverage);

                Log.d(MoviesFragment.class.getSimpleName(), posterPath + "\n" + adult + "\n" +
                        overview + "\n" + releaseDate + "\n" + id + "\n" + originalTitle + "\n" +
                        originalLanguage + "\n" + title + "\n" + backdropPath + "\n" + popularity +
                        "\n" + voteCount + "\n" + video + "\n" + voteAverage);

            }

            Log.d(MoviesFragment.class.getSimpleName(), "returning movies array");
            return movies;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {

            Log.d(MoviesFragment.class.getSimpleName(), "inside post execute");

            if (movies != null) {

                Log.d(MoviesFragment.class.getSimpleName(), "movies array is not null");

                mMovieAdapter.clear();
                Log.d(MoviesFragment.class.getSimpleName(), "adapter cleared");

                for (Movie movie : movies) {
                    Log.d(MoviesFragment.class.getSimpleName(), "movie added to adapter");
                    mMovieAdapter.add(movie);
                }
            }
        }
    }
}
