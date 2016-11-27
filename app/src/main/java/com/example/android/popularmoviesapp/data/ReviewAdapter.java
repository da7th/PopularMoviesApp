package com.example.android.popularmoviesapp.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.popularmoviesapp.R;

import java.util.ArrayList;

/**
 * Created by da7th on 20/11/2016.
 */

public class ReviewAdapter extends ArrayAdapter<String> {
    public ReviewAdapter(Context context, ArrayList<String> Strings) {
        super(context, 0, Strings);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_list_review, parent, false);
        }

        String currentString = getItem(position);

        TextView reviewTV = (TextView) listItemView.findViewById(R.id.review_title);
        reviewTV.setContentDescription("review" + currentString);
        reviewTV.setText(currentString);


        return listItemView;
    }
}
