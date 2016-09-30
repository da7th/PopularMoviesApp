package com.example.android.popularmoviesapp;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by da7th on 25/09/2016.
 */

public class DetailsActivity extends AppCompatActivity {


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_movies_details);

        Intent receivedIntent = getIntent();
        Movie currentMovieBundle = receivedIntent.getParcelableExtra("movie");

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

        final TextView titleTV = (TextView) findViewById(R.id.fragment_movies_details_title);
        TextView overviewTV = (TextView) findViewById(R.id.fragment_movies_details_overview);
        TextView ratingsTV = (TextView) findViewById(R.id.fragment_movies_details_rating);
        TextView releaseDateTV = (TextView) findViewById(R.id.fragment_movies_details_release_date);
        ImageView thumbnailIV = (ImageView) findViewById(R.id.fragment_movies_details_thumbnail);

        titleTV.setText(title);
        overviewTV.setText(overview);
        ratingsTV.setText(voteAverage + "/10 (" + voteCount + ")");
        releaseDateTV.setText(releaseDate.substring(0, 4));

        Picasso.with(this).load(posterPath).into(thumbnailIV);

        Picasso.with(this).load(backdropPath).into((ImageView) findViewById(R.id.image_backdrop_background));
    }
}
