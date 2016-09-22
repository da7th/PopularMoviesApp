package com.example.android.popularmoviesapp;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Created by da7th on 23/09/2016.
 */
//a custom adapter to take in movie type objects to extract and sort the relevant information accor
// -dingly in the grid layout

public class MovieAdapater extends ArrayAdapter<Movie> {


    public MovieAdapater(Context context, int resource) {
        super(context, resource);
    }
}
