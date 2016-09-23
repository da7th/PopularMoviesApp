package com.example.android.popularmoviesapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by da7th on 23/09/2016.
 */
//a custom adapter to take in movie type objects to extract and sort the relevant information accor
// -dingly in the grid layout

public class MovieAdapater extends ArrayAdapter<Movie> {


    public MovieAdapater(Context context, ArrayList<Movie> resource) {
        super(context, 0, resource);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView = convertView;
        if (gridView == null) {
            gridView = LayoutInflater.from(getContext()).inflate(R.layout.item_grid, parent, false);
        }

        Movie currentMovie = getItem(position);


        //set the data to their views


        return gridView;
    }
}