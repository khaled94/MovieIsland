package com.example.nbdell.movieisland;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NB DELL on 11/29/2016.
 */

public class MainActivityFragment extends Fragment {

    private ViewHelper mViewHelper;
    GridView Gridview;
    List<Movie> movielist = new ArrayList<>();
    MovieAdapter adapter;
    String Sorting_Choice = "popular";
    String MovieID;

    void setViewHelper(ViewHelper view_helper) {
        this.mViewHelper = view_helper;
        Log.d("setViewHelper-->", "true");
    }

    public MainActivityFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_order, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    //internet permission
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        getMoviesData data = new getMoviesData();
        boolean isOnline = isNetworkAvailable();
        if (id == R.id.TopRated) {
            if(isOnline){
                Sorting_Choice = "top_rated";
                data.execute();
            }
            else{
                Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
            }
            return true;
        }

        if (id == R.id.MostPopular) {
            if(isOnline) {
                Sorting_Choice = "popular";
                data.execute();
            }
            else{
                Toast.makeText(getActivity(), "No Internet Connection..", Toast.LENGTH_LONG).show();
            }

            return true;
        }

        if (id == R.id.FavouriteMovies) {
            getFavourites favourite = new getFavourites();
            favourite.view_favourite_movies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        boolean isOnline = isNetworkAvailable();
        if(isOnline) {
            View view = inflater.inflate(R.layout.fragment_main, container, false);
            getMoviesData data = new getMoviesData();
            data.execute();
            return view;
        }
        View view = inflater.inflate(R.layout.internet_error, container, false);
        return view;
    }


    public class getMoviesData extends AsyncTask<String, Void, List<Movie>> {
        @Override
        protected List<Movie> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String MoviesJsonStr = null;

            try {
                String baseUrl = "http://api.themoviedb.org/3/movie/";
                baseUrl = baseUrl.concat(Sorting_Choice);
                String apiKey = "?api_key=d75126b71ee3f3d7d963070c6fdc25cc";
                URL url = new URL(baseUrl.concat(apiKey));

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return movielist;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return movielist;
                }

                MoviesJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e("MovieFragment", "Error ", e);
                return movielist;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        return movielist;
                    }
                }
            }

            try {
                movielist = getMovieDataFromJson(MoviesJsonStr);
            } catch (Exception e) {
                return movielist;
            }
            return movielist;
        }

        @Override
        protected void onPostExecute(final List<Movie> movies) {
            super.onPostExecute(movies);
            Gridview = (GridView) getActivity().findViewById(R.id.grid_list);
            adapter = new MovieAdapter(getContext(), movies);
            Gridview.setAdapter(adapter);
            Gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Movie send_movie = movielist.get(position);
                    mViewHelper.setSelectedMovie(send_movie);
                    //Log.d(" Movie Name -->", sentmovie.getOriginalTitle());
                    //Log.d("Favouroite -->",String.valueOf(sentmovie.isFavourite()));
                }
            });
        }


        private List<Movie> getMovieDataFromJson(String jsonResponse) {
            //    Log.d("Action-->","Parsing.......");
            List<Movie> movies = new ArrayList<>();
            List<Movie> favourite_movies = new ArrayList<>();
            getFavourites favourites = new getFavourites();
            favourite_movies = favourites.GetFavouriteMovies();
            String results_key = "results";
            String poster_path_key = "poster_path";
            String id_key = "id";
            String title_key = "original_title";
            String overView_key = "overview";
            String rating_key = "vote_average";
            String release_Date_key = "release_date";

            try {
                JSONObject movieJSON = new JSONObject(jsonResponse);
                if (movieJSON.has(results_key)) {
                    JSONArray movieList = movieJSON.getJSONArray(results_key);
                    if (movieList.length() > 0) {
                        for (int i = 0; i < movieList.length(); i++) {
                            JSONObject movieInJSON = (JSONObject) movieList.get(i);
                            MovieID = movieInJSON.getString(id_key);
                            Movie movie = new Movie(MovieID);
                             Log.d("Favourite Movies Size-->",String.valueOf(favourite_movies.size()));
                             for (int j = 0; j < favourite_movies.size(); j++) {
                              //   Log.d("Current movie id -->",MovieID);
                              //   Log.d("favorite Movie Id -->",favourite_movies.get(j).getMovieId());
                                if (favourite_movies.get(j).getMovieId().equals(MovieID))
                            //Log.d("favorite check -->", "make movie Favourite");
                                  movie.setFavourite(true);
                             }
                            //}
                            String poster_path = movieInJSON.getString(poster_path_key);
                            movie.setImageURL(poster_path);
                            //Log.d("Poster==>  ",poster_path );
                            movie.setMovieId(MovieID);
                            movie.setOriginalTitle(movieInJSON.getString(title_key));
                            movie.setOverview(movieInJSON.getString(overView_key));
                            movie.setRating(movieInJSON.getString(rating_key));
                            movie.setReleaseDate(movieInJSON.getString(release_Date_key));
                            movie.setFrom_favourites(false);
                            movies.add(movie);
                        }

                    }
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }

            return movies;
        }
        }

        public class getFavourites {

            private void view_favourite_movies() {

                List<Movie> fav_movies = new ArrayList<>();
                fav_movies = GetFavouriteMovies();
                Gridview = (GridView) getActivity().findViewById(R.id.grid_list);
                adapter = new MovieAdapter(getContext(), fav_movies);
                Gridview.setAdapter(adapter);
                final List<Movie> finalFav_movies = fav_movies;
                Gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Intent MovieDetailsActivity = new Intent(getActivity(), MovieDetailsActivity.class);
                        Movie send_movie = finalFav_movies.get(position);
                        mViewHelper.setSelectedMovie(send_movie);
                   /*     MovieDetailsActivity.putExtra("MovieTitle", sentmovie.getOriginalTitle());
                        MovieDetailsActivity.putExtra("MoviePoster", sentmovie.getImageFullURL());
                        MovieDetailsActivity.putExtra("Movie_overview", sentmovie.getOverview());
                        MovieDetailsActivity.putExtra("MovieRating", sentmovie.getRating());
                        MovieDetailsActivity.putExtra("MovieDate", sentmovie.getReleaseDate());
                        MovieDetailsActivity.putExtra("MovieID", sentmovie.getMovieId());
                        startActivity(MovieDetailsActivity);*/
                    }
                });
            }


            private List<Movie> GetFavouriteMovies() {
                //    Log.d("Action-->","Parsing.......");
                List<Movie> movies = new ArrayList<>();
                DatabaseHelper mDbHelper = DatabaseHelper.getInstance(getContext());
                SQLiteDatabase db = mDbHelper.getReadableDatabase();

                String[] projection = {
                        DatabaseContract.FeedEntry._ID,
                        DatabaseContract.FeedEntry.Column_Name_MovieID,
                        DatabaseContract.FeedEntry.Column_Name_Title,
                        DatabaseContract.FeedEntry.Column_Name_Poster,
                        DatabaseContract.FeedEntry.Column_Name_Overview,
                        DatabaseContract.FeedEntry.Column_Name_Rating,
                        DatabaseContract.FeedEntry.Column_Name_Date,
                };

                Cursor cursor = db.query(
                        DatabaseContract.FeedEntry.TABLE_NAME,     // The table to query
                        projection,                               // The columns to return
                        null,                                     // The columns for the WHERE clause
                        null,                                      // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        null                                      // The sort order
                );

                if (cursor != null) {

                    if (cursor.moveToFirst()) {
                        //    Log.d("Check", "First");
                        int i = 0;
                        do {
                            MovieID = cursor.getString(cursor.getColumnIndex(DatabaseContract.FeedEntry.Column_Name_MovieID));
                            Movie movie = new Movie(MovieID);

                            movie.setMovieId(MovieID);
                            String poster_path = cursor.getString(cursor.getColumnIndex(DatabaseContract.FeedEntry.Column_Name_Poster));
                            //Log.d("Movies Poster ", poster_path);
                            movie.setPoster(poster_path);
                            movie.setOriginalTitle(cursor.getString(cursor.getColumnIndex(DatabaseContract.FeedEntry.Column_Name_Title)));
                            movie.setOverview(cursor.getString(cursor.getColumnIndex(DatabaseContract.FeedEntry.Column_Name_Overview)));
                            movie.setRating(cursor.getString(cursor.getColumnIndex(DatabaseContract.FeedEntry.Column_Name_Rating)));
                            movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(DatabaseContract.FeedEntry.Column_Name_Date)));
                            movie.setFavourite(true);
                            movie.setFrom_favourites(true);
                            //Log.d("Favourite Movies Name --> ", movie.getOriginalTitle());
                            movies.add(movie);
                            // Log.d("Movies ID 2 --> ", String.valueOf(movies.get(i).getMovieId()));
                            i++;
                        } while (cursor.moveToNext());
                        //Log.d("check 3 ","Another Problem");
                        cursor.moveToFirst();
                        //Log.d("check 4 ","Move to first problem");
                    }
                }
                //Log.d("Image --> ", String.valueOf(movies.get(0).getImageFullURL()));
                //  Log.d("check 5 ","Done with Get Favorites");
                return movies;

               /* long itemId = cursor.getLong(
                        cursor.getColumnIndexOrThrow(DatabaseContract.FeedEntry._ID)
                );*/
            }
        }
    }