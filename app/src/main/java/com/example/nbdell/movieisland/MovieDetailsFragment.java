package com.example.nbdell.movieisland;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarException;

/**
 * Created by NB DELL on 11/30/2016.
 */

public class MovieDetailsFragment extends Fragment implements Serializable{

    public TextView movieTitle, movieOverview, movieRating, movieReleaseDate;
    public ImageView moviePoster;
    String Title, overView, Rating, Date, poster,MovieID;
    boolean isFavourite,from_Favourite;
    List<String> trailers_list = new ArrayList<>();
    public List<Review> reviews_list = new ArrayList<>();
    Button trailer;
    Button Add_to_Favourite;
    LinearLayout reviewsContainer;
    LinearLayout trailersContainer;
    Movie movie;
    boolean isOnline;

    public MovieDetailsFragment() {

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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.details_fragment, null);
        movieTitle = (TextView) rootView.findViewById(R.id.movie_title);
        movieOverview = (TextView) rootView.findViewById(R.id.overview);
        movieRating = (TextView) rootView.findViewById(R.id.rating);
        movieReleaseDate = (TextView) rootView.findViewById(R.id.date);
        moviePoster = (ImageView) rootView.findViewById(R.id.movie_image);
        trailersContainer = (LinearLayout) rootView.findViewById(R.id.trailers);
        reviewsContainer = (LinearLayout) rootView.findViewById(R.id.reviews);
        Add_to_Favourite = (Button) rootView.findViewById(R.id.favorite);

        /*movie = (Movie) getActivity().getIntent().getSerializableExtra("Movie");
        MovieID = movie.getMovieId();
        Title = movie.getOriginalTitle();
        poster = movie.getImageURL();
        Rating = movie.getRating();
        overView = movie.getOverview();
        Date = movie.getReleaseDate();
        isFavourite = movie.isFavourite();*/
        Intent sentIntent = getActivity().getIntent();
        Bundle sentBundle = getArguments();

        if( sentIntent.getStringExtra("MovieID") != null ) {
            Title = sentIntent.getStringExtra("MovieTitle");
            poster = sentIntent.getStringExtra("MoviePoster");
            Rating = sentIntent.getStringExtra("MovieRating");
            overView = sentIntent.getStringExtra("Movie_overview");
            Date = sentIntent.getStringExtra("MovieDate");
            MovieID = sentIntent.getStringExtra("MovieID");
            isFavourite = sentIntent.getBooleanExtra("Favourite", true);
            from_Favourite = sentIntent.getBooleanExtra("From_Favourite",false);
            movie = new Movie(MovieID);
        }

        else {
            //Log.d("Bundle Data id",sentBundle.getString("MovieID"));
            Title = sentBundle.getString("MovieTitle");
            poster = sentBundle.getString("MoviePoster");
            Rating = sentBundle.getString("MovieRating");
            overView = sentBundle.getString("Movie_overview");
            Date = sentBundle.getString("MovieDate");
            MovieID = sentBundle.getString("MovieID");
            isFavourite = sentBundle.getBoolean("Favourite");
            from_Favourite = sentBundle.getBoolean("From_Favourite",false);
            movie = new Movie(MovieID);
        }

        if (MovieID != null) {
            movieTitle.setText(Title);
            movieOverview.setText(overView);
            movieRating.setText(Rating);
            movieReleaseDate.setText(Date);
            movie.setFavourite(isFavourite);
            Picasso.with(rootView.getContext()).load(poster).into(moviePoster);

           // Log.d("Movie Name -->", Title);
           // Log.d("Favourite -->", String.valueOf(isFavourite));
            isOnline = isNetworkAvailable();
            if( isFavourite == true && from_Favourite == false){
                Add_to_Favourite.setText("Favourite");
                Add_to_Favourite.setBackgroundColor(Color.parseColor("#008560"));
            }
            if( (isFavourite == true && from_Favourite == true) || isOnline == false){
                Add_to_Favourite.setVisibility(View.INVISIBLE);
            }
        }

        if(isOnline) {
            GetTrailers trailers = new GetTrailers();
            GetReviews reviews = new GetReviews();
            trailers.execute();
            reviews.execute();
        }
        else{
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
        Add_to_Favourite.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(View v) {
                DatabaseHelper mDbHelper = DatabaseHelper.getInstance(getContext());
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                if ( isFavourite == false) {
                    Log.d("MDG-->","Movie is Added");
                    ContentValues values = new ContentValues();
                    values.put(DatabaseContract.FeedEntry.Column_Name_Title, Title);
                    values.put(DatabaseContract.FeedEntry.Column_Name_MovieID, MovieID);
                    values.put(DatabaseContract.FeedEntry.Column_Name_Poster, poster);
                    values.put(DatabaseContract.FeedEntry.Column_Name_Date, Date);
                    values.put(DatabaseContract.FeedEntry.Column_Name_Overview, overView);
                    values.put(DatabaseContract.FeedEntry.Column_Name_Rating, Rating);

                    long newRowId = db.insert(DatabaseContract.FeedEntry.TABLE_NAME, null, values);
                    Log.d("ROW", String.valueOf(newRowId));
                    Add_to_Favourite.setText("Favourite");
                    Add_to_Favourite.setBackgroundColor(Color.parseColor("#008560"));
                    isFavourite = true;
                }

                else {
                    Log.d("MDG-->","Movie is deleted");
                    String selection = DatabaseContract.FeedEntry.Column_Name_MovieID+ " LIKE ?";
                    String[] selectionArgs = { MovieID };
                    db.delete(DatabaseContract.FeedEntry.TABLE_NAME, selection, selectionArgs);
                    Add_to_Favourite.setText("Add to Favourites");
                    Add_to_Favourite.setBackgroundColor(Color.parseColor("#ff4040"));
                    isFavourite =false;
                }
            }
        });
        return rootView;
    }

    public class GetTrailers extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String TrailersJsonStr = null;

            try {
                String baseUrl = "http://api.themoviedb.org/3/movie/";
               // String movieID = getActivity().getIntent().getStringExtra("MovieID");
                baseUrl = baseUrl.concat(MovieID);
                String apiKey = "/videos?api_key=d75126b71ee3f3d7d963070c6fdc25cc";
                URL url = new URL(baseUrl.concat(apiKey));

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return trailers_list;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return trailers_list;
                }

                TrailersJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e("Trailers", "Error ", e);
                return trailers_list;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        return trailers_list;
                    }
                }
            }

            try {
                trailers_list = getVideoDataFromJson(TrailersJsonStr);
            } catch (Exception e) {
                return trailers_list;
            }
            return trailers_list;
        }

        protected void onPostExecute(final List<String> trailers) {

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            for (int i = 0; i < trailers.size(); i++) {

                View view = inflater.inflate(R.layout.trailer_item, null);
                trailer = (Button) view.findViewById(R.id.trailer_video);
                trailer.setId(i);
                trailer.setText("Trailer " + (i + 1));
                trailer.setTextSize(20);
                trailer.setTextColor(Color.BLACK);

                trailer.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailers.get(trailer.getId()))));
                    }
                });
                trailersContainer.addView(view);
            }
        }

        public ArrayList<String> getVideoDataFromJson(String json) throws JarException, JSONException {

            ArrayList<String> links = new ArrayList<>();
            JSONObject j = new JSONObject(json);
            JSONArray Array = j.getJSONArray("results");

            for (int i = 0; i < Array.length(); i++) {
                links.add(Array.getJSONObject(i).getString("key"));
            }
            return links;
        }
    }


    public class GetReviews extends AsyncTask<String, Void, List<Review>> {

        @Override
        protected List<Review> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String ReviewsJsonStr = null;

            try {
                Log.d("Review","working ");
                String baseUrl = "http://api.themoviedb.org/3/movie/";
               // String movieID = getActivity().getIntent().getStringExtra("MovieID");
                baseUrl = baseUrl.concat(MovieID);
                String apiKey = "/reviews?api_key=d75126b71ee3f3d7d963070c6fdc25cc";
                URL url = new URL(baseUrl.concat(apiKey));

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return reviews_list;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return reviews_list;
                }

                ReviewsJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e("Reviews", "Error ", e);
                return reviews_list;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                        /*reviews_list = getReviewDataFromJson(ReviewsJsonStr);
                        Log.d("check++-->", reviews_list.get(0).getAuthor());
                        return reviews_list;*/
                    } catch (final IOException e) {
                        return reviews_list;
                    } /*catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                }


                try {
                    reviews_list = getReviewDataFromJson(ReviewsJsonStr);
                      Log.d("check++-->", reviews_list.get(0).getAuthor());
                } catch (Exception e) {
                    return reviews_list;
                }
                return reviews_list;
            }
        }
        protected void onPostExecute(final List<Review> reviews) {
            super.onPostExecute(reviews);
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Log.d("Review Size",String.valueOf(reviews.size()));
            for (int i = 0; i < reviews.size(); i++) {
                View view = inflater.inflate(R.layout.review_item, null);
                TextView author = (TextView) view.findViewById(R.id.author);
                TextView content = (TextView) view.findViewById(R.id.content);
                author.setText(reviews.get(i).getAuthor());
                content.setText(reviews.get(i).getContent());
                reviewsContainer.addView(view);
            }
        }
    }

    public List<Review> getReviewDataFromJson(String json) throws JarException, JSONException {

        List<Review> reviews = new ArrayList<>();
        JSONObject j = new JSONObject(json);
        JSONArray Array = j.getJSONArray("results");
        if( Array.length() == 0 ) {
            String author = "No Reviews Available";
            String content = "";
            Review review = new Review(author,content);
            reviews.add(review);
        }

        else {
            //Log.d("testtt", String.valueOf(Array.length()));
            for (int i = 0; i < Array.length(); i++) {

                JSONObject result = Array.getJSONObject(i);
                String author = result.getString("author");
                String content = result.getString("content");
                     Log.d("Author -->", String.valueOf(author));
                Review review = new Review(author, content);
                reviews.add(review);
            }
        }
        // Log.d("abc-->", String.valueOf(reviews.size()));
        Review TempReview = reviews.get(0);
        //Log.d("xxx-->", TempReview.getAuthor());
        return reviews;
    }
}
