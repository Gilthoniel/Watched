package ch.watched.android.database;

import android.provider.BaseColumns;

/**
 * Created by Gaylor on 02.07.2015.
 * Movie table for the database
 */
public final class MovieContract {
    public MovieContract() {}

    public static abstract class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_SCORE = "score";
        public static final String COLUMN_IMAGE = "poster";
        public static final String COLUMN_RELEASE = "release_date";
        public static final String COLUMN_WATCHED = "watched";
        public static final String COLUMN_BACKDROPS = "backdrops";
        public static final String COLUMN_GENRES = "genres";

        public static final String SQL_CREATE = "create table "+TABLE_NAME+" (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL UNIQUE," +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_WATCHED + " INTEGER," +
                MovieEntry.COLUMN_OVERVIEW + " TEXT," +
                MovieEntry.COLUMN_SCORE + " REAL," +
                MovieEntry.COLUMN_IMAGE + " TEXT," +
                MovieEntry.COLUMN_RELEASE + " TEXT," +
                MovieEntry.COLUMN_BACKDROPS + " BLOB," +
                MovieEntry.COLUMN_GENRES + " BLOB" +
                ");";

        public static final String SQL_DELETE = "drop table if exists " + MovieEntry.TABLE_NAME;
    }
}
