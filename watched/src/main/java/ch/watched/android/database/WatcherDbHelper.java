package ch.watched.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gaylor on 02.07.2015.
 * Helper to instantiate a database
 */
public class WatcherDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 8;
    public static final String DATABASE_NAME = "Watcher.db";

    public WatcherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MovieContract.MovieEntry.SQL_CREATE);
        db.execSQL(TVContract.TVEntry.SQL_CREATE);
        db.execSQL(EpisodeContract.EpisodeEntry.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(MovieContract.MovieEntry.SQL_DELETE);
        db.execSQL(TVContract.TVEntry.SQL_DELETE);
        db.execSQL(EpisodeContract.EpisodeEntry.SQL_DELETE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do nothing
    }
}
