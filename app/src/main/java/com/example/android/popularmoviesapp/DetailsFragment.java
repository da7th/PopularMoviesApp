package com.example.android.popularmoviesapp;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String DETAIL_URI = "URI";
    private static final int DETAIL_LOADER = 0;
    TextView titleTV;
    TextView overviewTV;
    TextView ratingsTV;
    TextView releaseDateTV;
    ImageView thumbnailIV;
    private Uri mUri;

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

        return rootView;
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

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }
}
