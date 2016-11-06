package com.example.android.popularmoviesapp;


import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesapp.data.MovieContract;
import com.example.android.popularmoviesapp.data.TrailerAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String DETAIL_URI = "URI";
    private static final int DETAIL_LOADER = 0;
    private final ArrayList<String> mTrailers = new ArrayList<String>();
    TextView titleTV;
    TextView overviewTV;
    TextView ratingsTV;
    TextView releaseDateTV;
    ImageView thumbnailIV;
    ListView trailers;
    FetchTrailerTask trailersTask;
    TrailerAdapter adapter;
    Button favBtn;
    int mFav;
    Cursor mCursor;
    private Uri mUri;
    private String[] mReviews;

    public DetailsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailsFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_movies_details, container, false);

        titleTV = (TextView) rootView.findViewById(R.id.fragment_movies_details_title);
        overviewTV = (TextView) rootView.findViewById(R.id.fragment_movies_details_overview);
        ratingsTV = (TextView) rootView.findViewById(R.id.fragment_movies_details_rating);
        releaseDateTV = (TextView) rootView.findViewById(R.id.fragment_movies_details_release_date);
        thumbnailIV = (ImageView) rootView.findViewById(R.id.fragment_movies_details_thumbnail);
        trailers = (ListView) rootView.findViewById(R.id.trailer_list);

        favBtn = (Button) rootView.findViewById(R.id.mark_fav_button);

        mFav = 0;
        mCursor = null;

        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFav != 1) {
                    favBtn.setText("A Favourite!");

                    if (mCursor != null) {
                        Uri inserted = getContext().getContentResolver().insert(MovieContract.FavMovies.CONTENT_URI, getContentValues(mCursor));
                    }
                }

                Toast.makeText(getContext(), "clicking me works!", Toast.LENGTH_SHORT).show();
            }
        });

        adapter = new TrailerAdapter(getContext(), null);
        trailersTask = new FetchTrailerTask();

        return rootView;
    }

    private ContentValues getContentValues(Cursor cursor) {

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
        String trailerList;
        String reviewsList;
        int favStatus;

        posterPath = mCursor.getString(1);
        adult = mCursor.getInt(2) > 0;
        overview = mCursor.getString(3);
        releaseDate = mCursor.getString(4);
        id = mCursor.getInt(5);
        originalTitle = mCursor.getString(6);
        originalLanguage = mCursor.getString(7);
        title = mCursor.getString(8);
        backdropPath = mCursor.getString(9);
        popularity = mCursor.getLong(10);
        voteCount = mCursor.getInt(11);
        video = mCursor.getInt(12) > 0;
        voteAverage = mCursor.getDouble(13);
        trailerList = mCursor.getString(14);
        reviewsList = mCursor.getString(15);
        favStatus = 1;


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
        values.put(MovieContract.MoviesSaved.COLUMN_TRAILER, trailerList);
        values.put(MovieContract.MoviesSaved.COLUMN_REVIEWS, reviewsList);
        values.put(MovieContract.MoviesSaved.COLUMN_FAV, favStatus);

        return values;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    null,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            titleTV.setText(data.getString(8));
            overviewTV.setText(data.getString(3));
            ratingsTV.setText(data.getDouble(13) + "/10 (" + data.getString(11) + ")");
            releaseDateTV.setText(data.getString(4).substring(0, 4));

            Picasso.with(getActivity()).load(data.getString(1)).into(thumbnailIV);

            Picasso.with(getActivity()).load(data.getString(9)).into((ImageView) getActivity().findViewById(R.id.image_backdrop_background));

            trailersTask.execute(data.getString(14));

            mFav = data.getInt(16);

            mCursor = data;

            Uri favCheckUri = ContentUris.withAppendedId(MovieContract.FavMovies.CONTENT_URI, data.getInt(0));

            Cursor favCheckCursor = getActivity().getContentResolver().query(favCheckUri, null, null, null, null);

            int favCheck = 0;

            if (favCheckCursor.getCount() > 0) {

                favCheck = favCheckCursor.getInt(16);
            }

            Log.v("onLoadFinished:", "favCheck is: " + favCheck);

            mFav = favCheck;

            if (mFav == 1) {

                favBtn.setText("A Favourite!");
            }


//            if (trailersTask.getStatus() != AsyncTask.Status.FINISHED) {
//
//                Log.v("onLoadFinished", "done");
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        adapter = new TrailerAdapter(getContext(), mTrailers);
//                        trailers.setAdapter(adapter);
//                    }
//                });
//
//            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }

    ////////////////////////////////////////////////////////////////////////////////


    private class FetchTrailerTask extends AsyncTask<Object, Object, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.v("onPostExecute:", "finished supposedly");


        }

        @Override
        protected Void doInBackground(Object... params) {

            Object trailerJsonStr = params[0];
            String trailerPath = "";
            //find the root object and extract the jsonArray
            JSONObject rootObject = null;
            try {
                rootObject = new JSONObject((String) trailerJsonStr);

                JSONArray resultsArray = rootObject.getJSONArray("results");
                String[] links = new String[resultsArray.length()];

                for (int i = 0; i < resultsArray.length(); i++) {

                    JSONObject trailerObject = resultsArray.getJSONObject(i);

                    trailerPath = trailerObject.getString("key");

                    links[i] = "https://www.youtube.com/watch?v=" + trailerPath;
                    Log.v("Links: ", links[i]);
                    mTrailers.add(i, links[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class fetchReviewsTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {

            String trailerJsonStr = params[0];
            String trailerPath = "";

            //find the root object and extract the jsonArray
            JSONObject rootObject = null;

            try {
                rootObject = new JSONObject(trailerJsonStr);

                JSONArray resultsArray = rootObject.getJSONArray("results");
                String[] links = new String[resultsArray.length()];

                for (int i = 0; i < resultsArray.length(); i++) {

                    JSONObject trailerObject = resultsArray.getJSONObject(i);

                    trailerPath = trailerObject.getString("key");

                    links[i] = "https://www.youtube.com/watch?v=" + trailerPath;

                }

                return links;

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}