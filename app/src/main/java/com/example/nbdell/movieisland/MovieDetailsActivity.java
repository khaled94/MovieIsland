package com.example.nbdell.movieisland;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by NB DELL on 11/30/2016.
 */

public class MovieDetailsActivity extends AppCompatActivity  {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_main);
        MovieDetailsFragment mMovieDetailsFragment = new MovieDetailsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.DetailsFragment, mMovieDetailsFragment, "").commit();
    }

}


