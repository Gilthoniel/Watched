package ch.watched.android.database;

import android.provider.BaseColumns;

/**
 * Created by Gaylor on 07.07.2015.
 */
public class TVContract {
    private TVContract() {}

    public static abstract class TVEntry implements BaseColumns {

        public static final String TABLE_NAME = "tv";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_HOMEPAGE = "homepage";
        public static final String COLUMN_IN_PROD = "in_prod";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_NB_EPISODES = "nb_episodes";
        public static final String COLUMN_NB_SEASONS = "nb_seasons";
        public static final String COLUMN_LANGUAGE = "lang";
        public static final String COLUMN_ORIGINAL_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_SCORE = "score";
        public static final String COLUMN_BACKDROPS = "backdrops";
        public static final String COLUMN_GENRES = "genres";

        public static final String SQL_CREATE = "create table "+TABLE_NAME+" (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_ID + " INTEGER NOT NULL UNIQUE," +
                COLUMN_HOMEPAGE + " TEXT," +
                COLUMN_IN_PROD + " NUMBER," +
                COLUMN_NAME + " TEXT, " +
                COLUMN_NB_EPISODES + " NUMBER," +
                COLUMN_NB_SEASONS + " NUMBER," +
                COLUMN_LANGUAGE + " TEXT," +
                COLUMN_ORIGINAL_TITLE + " TEXT," +
                COLUMN_OVERVIEW + " TEXT," +
                COLUMN_POSTER + " TEXT," +
                COLUMN_STATUS + " TEXT," +
                COLUMN_TYPE + " TEXT," +
                COLUMN_SCORE + " NUMBER," +
                COLUMN_BACKDROPS + " BLOB," +
                COLUMN_GENRES + " BLOB" +
                ");";

        public static final String SQL_DELETE = "drop table if exists " + TABLE_NAME;
    }
}
