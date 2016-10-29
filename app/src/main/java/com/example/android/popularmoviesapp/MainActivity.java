package com.example.android.popularmoviesapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MoviesFragment.Callback {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    public boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this will show me the database.
        //SqlScoutServer.create(this, getPackageName());

        //check if the details layout is in the loaded view to see if its a phone or tablet device.
        if (findViewById(R.id.details_layout) != null) {

            //if there is a master detail layout then set the variable to true to change the
            // workings of the application accordingly
            mTwoPane = true;

            //check if the fragment is already loaded or not, if it isn't the load a new one.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.details_layout, new DetailsFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {

            //if the isn't a master detail layout loaded then set the variable to false to change
            // the workings of the application accordingly
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //inflate and options menu where the user can select a setting or the settings menu
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //if the settings menu is selected then load the settings activity
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailsFragment.DETAIL_URI, contentUri);

            DetailsFragment fragment = new DetailsFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.details_layout, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailsActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }
}
