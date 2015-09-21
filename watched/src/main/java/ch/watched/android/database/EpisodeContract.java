package ch.watched.android.database;

import android.provider.BaseColumns;

/**
 * Created by Gaylor on 06.07.2015.
 * Episodes table for the database
 */
public class EpisodeContract {
    private EpisodeContract(){}

    public static abstract class EpisodeEntry implements BaseColumns {
        public static final String TABLE_NAME = "episode";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_EPISODE_NB = "episode_number";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_SEASON_NB = "season_number";
        public static final String COLUMN_STILL_PATH = "still";
        public static final String COLUMN_SCORE = "score";
        public static final String COLUMN_TV_ID = "tv_id";

        public static final String SQL_CREATE = "create table "+TABLE_NAME+" (" +
                _ID + " INTEGER PRIMARY KEY," +
                WatcherDbHelper.COLUMN_ID + " INTEGER UNIQUE," +
                COLUMN_DATE + " TEXT," +
                COLUMN_EPISODE_NB + " NUMBER," +
                COLUMN_NAME + " TEXT," +
                COLUMN_OVERVIEW + " TEXT," +
                COLUMN_SEASON_NB + " NUMBER," +
                COLUMN_STILL_PATH + " TEXT," +
                COLUMN_SCORE + " NUMBER," +
                COLUMN_TV_ID + " NUMBER," +
                WatcherDbHelper.COLUMN_WATCHED + " INTEGER" +
                ");";

        public static final String SQL_DELETE = "drop table if exists " + EpisodeEntry.TABLE_NAME;
    }
}
