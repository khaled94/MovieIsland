package com.example.nbdell.movieisland;

/**
 * Created by NB DELL on 11/29/2016.
 */

public class Movie {
    final static String ImageBaseURL = "http://image.tmdb.org/t/p/";
    final static String LegoSize = "w185";
    String movieId;
    String imageURL;
    String originalTitle;
    String overview;
    String rating;
    String releaseDate;
    String poster;
    boolean favourite;
    boolean from_favourites;

    public boolean isFrom_favourites() {
        return from_favourites;
    }

    public void setFrom_favourites(boolean from_favourites) {
        this.from_favourites = from_favourites;
    }

    public Movie(String Id) {
        this.movieId = movieId;
        poster="empty";
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getImageFullURL() {
        if( poster == "empty")
            return ImageBaseURL + LegoSize + getImageURL();
        else
            return poster;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }


    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public boolean isFavourite() {
        return favourite;
    }
}
