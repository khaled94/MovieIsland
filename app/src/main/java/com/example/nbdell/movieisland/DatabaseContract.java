package com.example.nbdell.movieisland;

import android.provider.BaseColumns;

/**
 * Created by NB DELL on 11/30/2016.
 */

public final class DatabaseContract {
    private DatabaseContract() {

    }
    protected static final String TEXT_TYPE = " TEXT";
    protected static final String COMMA_SEP = ",";

    protected static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.Column_Name_MovieID + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.Column_Name_Title + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.Column_Name_Poster + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.Column_Name_Rating + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.Column_Name_Date + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.Column_Name_Overview + TEXT_TYPE + " )";

    protected static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    /* Inner class for table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "Favourites";
        public static final String Column_Name_Title = "title";
        public static final String Column_Name_Poster = "poster";
        public static final String Column_Name_Rating = "rating";
        public static final String Column_Name_Overview = "overview";
        public static final String Column_Name_Date = "date";
        public static final String Column_Name_MovieID = "MovieID";
    }

}
