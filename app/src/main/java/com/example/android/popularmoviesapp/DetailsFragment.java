package com.example.android.popularmoviesapp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesapp.data.Movie;
import com.squareup.picasso.Picasso;

/**
 * Created by da7th on 25/09/2016.
 */

public class DetailsFragment extends Fragment {

    Movie mMovie;

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

        Movie currentMovieBundle = mMovie;

        String posterPath = currentMovieBundle.getPosterPath();
        Boolean adult = currentMovieBundle.getAdult();
        String overview = currentMovieBundle.getOverview();
        String releaseDate = currentMovieBundle.getReleaseDate();
        int id = currentMovieBundle.getID();
        String originalTitle = currentMovieBundle.getOriginalTitle();
        String originalLanguage = currentMovieBundle.getOriginalLanguage();
        String title = currentMovieBundle.getTitle();
        String backdropPath = currentMovieBundle.getBackdropPath();
        long popularity = currentMovieBundle.getPopularity();
        int voteCount = currentMovieBundle.getVoteCount();
        Boolean video = currentMovieBundle.getVideo();
        double voteAverage = currentMovieBundle.getVoteAverage();

        final TextView titleTV = (TextView) rootView.findViewById(R.id.fragment_movies_details_title);
        TextView overviewTV = (TextView) rootView.findViewById(R.id.fragment_movies_details_overview);
        TextView ratingsTV = (TextView) rootView.findViewById(R.id.fragment_movies_details_rating);
        TextView releaseDateTV = (TextView) rootView.findViewById(R.id.fragment_movies_details_release_date);
        ImageView thumbnailIV = (ImageView) rootView.findViewById(R.id.fragment_movies_details_thumbnail);

        titleTV.setText(title);
        overviewTV.setText(overview);
        ratingsTV.setText(voteAverage + "/10 (" + voteCount + ")");
        releaseDateTV.setText(releaseDate.substring(0, 4));

        Picasso.with(getActivity()).load(posterPath).into(thumbnailIV);

        Picasso.with(getActivity()).load(backdropPath).into((ImageView) rootView.findViewById(R.id.image_backdrop_background));

        return rootView;

    }
}
