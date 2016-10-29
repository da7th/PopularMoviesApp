package com.example.android.popularmoviesapp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class DetailsFragment extends Fragment {

    public DetailsFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_movies_details, container, false);

        final TextView titleTV = (TextView) rootView.findViewById(R.id.fragment_movies_details_title);
        TextView overviewTV = (TextView) rootView.findViewById(R.id.fragment_movies_details_overview);
        TextView ratingsTV = (TextView) rootView.findViewById(R.id.fragment_movies_details_rating);
        TextView releaseDateTV = (TextView) rootView.findViewById(R.id.fragment_movies_details_release_date);
        ImageView thumbnailIV = (ImageView) rootView.findViewById(R.id.fragment_movies_details_thumbnail);

        titleTV.setText("Test: The Movie");
        overviewTV.setText("NONE...we like that.");
        ratingsTV.setText(10 + "/10 (" + 1000000 + ")");
        releaseDateTV.setText("2016-06-23".substring(0, 4));

        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w92/e1mjopzAS2KNsvpbpahQ1a6SkSn.jpg").into(thumbnailIV);

        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w300/ndlQ2Cuc3cjTL7lTynw6I4boP4S.jpg").into((ImageView) rootView.findViewById(R.id.image_backdrop_background));

        return rootView;

    }
}
