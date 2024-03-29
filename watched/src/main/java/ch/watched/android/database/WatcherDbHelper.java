package ch.watched.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gaylor on 02.07.2015.
 * Helper to instantiate a database
 * @version 9 : Add date to TV table
 */
public class WatcherDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 9;
    public static final String DATABASE_NAME = "Watcher.db";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_WATCHED = "watched";

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

        if (oldVersion <= 8) {
            // Column for the first air date
            db.execSQL("ALTER TABLE " + TVContract.TVEntry.TABLE_NAME + " ADD COLUMN " +
                    TVContract.TVEntry.COLUMN_FIRST_DATE + " TEXT");
            // Column for the last air date
            db.execSQL("ALTER TABLE " + TVContract.TVEntry.TABLE_NAME + " ADD COLUMN " +
                    TVContract.TVEntry.COLUMN_END_DATE + " TEXT");
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do nothing
    }
}
