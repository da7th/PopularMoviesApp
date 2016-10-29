package com.example.android.popularmoviesapp.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.popularmoviesapp.R;
import com.squareup.picasso.Picasso;

/**
 * Created by da7th on 05/10/2016.
 */

public class GridCursorAdapter extends CursorAdapter {
    public GridCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //return and inflate a new grid item to the layout.
        return LayoutInflater.from(context).inflate(R.layout.item_grid, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //get the poster path from the cursor input and use picasso to set it to a grid item.
        ImageView gridItem = (ImageView) view.findViewById(R.id.grid_item);
        String posterPath = cursor.getString(cursor.getColumnIndex(MovieContract.MoviesSaved.COLUMN_POSTER_PATH));
        Picasso.with(context).load(posterPath).into(gridItem);
    }
}
