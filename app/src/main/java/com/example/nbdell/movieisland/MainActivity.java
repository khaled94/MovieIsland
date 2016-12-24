package com.example.nbdell.movieisland;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements ViewHelper{

    boolean mIsTwoPain = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivityFragment mMainFragment = new MainActivityFragment();
        mMainFragment.setViewHelper(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.MainFragment, mMainFragment, "").commit();
        if( findViewById(R.id.DetailsFragment) != null )
                                                mIsTwoPain = true;
    }

    @Override
    public void setSelectedMovie(Movie movie) {
        //Log.d("MovieID11", movie.getMovieId());
        if(!mIsTwoPain) {
            Intent MovieDetailsActivity = new Intent(getApplication(), MovieDetailsActivity.class);
            MovieDetailsActivity.putExtra("MovieTitle", movie.getOriginalTitle());
            MovieDetailsActivity.putExtra("MoviePoster", movie.getImageFullURL());
            MovieDetailsActivity.putExtra("Movie_overview", movie.getOverview());
            MovieDetailsActivity.putExtra("MovieRating", movie.getRating());
            MovieDetailsActivity.putExtra("MovieDate", movie.getReleaseDate());
            MovieDetailsActivity.putExtra("MovieID", movie.getMovieId());
            MovieDetailsActivity.putExtra("Favourite",movie.isFavourite());
            Log.d("check from fav main act-->",String.valueOf(movie.isFrom_favourites()));
            MovieDetailsActivity.putExtra("From_Favourite",movie.isFrom_favourites());
            startActivity(MovieDetailsActivity);
        }

        else{
            // Intent MovieDetailsActivity = new Intent(getApplication(), MovieDetailsActivity.class);
            MovieDetailsFragment mMovieDetailsFragment = new MovieDetailsFragment();
            Bundle extras= new Bundle();
            extras.putString("MovieTitle", movie.getOriginalTitle());
            extras.putString("MoviePoster", movie.getImageFullURL());
            extras.putString("Movie_overview", movie.getOverview());
            extras.putString("MovieRating", movie.getRating());
            extras.putString("MovieDate", movie.getReleaseDate());
            extras.putString("MovieID", movie.getMovieId());
            Log.d("Movie Id -->",movie.getMovieId());
            mMovieDetailsFragment.setArguments(extras);
            getSupportFragmentManager().beginTransaction().replace(R.id.DetailsFragment,mMovieDetailsFragment,"").commit();
        }
    }
}