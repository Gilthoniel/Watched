package ch.watched.android.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ch.watched.android.database.EpisodeContract.EpisodeEntry;
import ch.watched.android.database.MovieContract.MovieEntry;
import ch.watched.android.models.Episode;
import ch.watched.android.models.Media;
import ch.watched.android.models.Movie;
import ch.watched.android.models.TV;

/**
 * Created by Gaylor on 02.07.2015.
 * Service to save information
 */
public class DatabaseService {

    private static DatabaseService instance = new DatabaseService();

    private WatcherDbHelper helper;

    public static DatabaseService getInstance() {
        return instance;
    }

    public void initHelper(Context context) {
        helper = new WatcherDbHelper(context);
    }

    public boolean contains(Media media) {
        try (SQLiteDatabase db = helper.getReadableDatabase()) {

            String query = "SELECT id FROM " + media.getSQLTable() + " WHERE id=" + media.getID();

            try (Cursor cursor = db.rawQuery(query, null)) {
                return cursor.getCount() > 0;
            }
        }
    }

    public long insert(Movie movie) {

        return insertMedia(movie);
    }

    public long insert(TV tv) {

        return insertMedia(tv);
    }

    public long insert(Episode episode) {

        return insertMedia(episode);
    }

    public int update(Media media) {

        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            return db.update(MovieEntry.TABLE_NAME, media.getSQLValues(), "id=" + media.getID(), null);
        }
    }

    public int remove(Media media) {

        try (SQLiteDatabase db = helper.getWritableDatabase()) {

            if (media.getSQLTable().equals(TVContract.TVEntry.TABLE_NAME)) {

                db.delete(EpisodeEntry.TABLE_NAME, EpisodeEntry.COLUMN_TV_ID + "=" + media.getID(), null);
            }

            return db.delete(media.getSQLTable(), "id=" + media.getID(), null);
        }
    }

    private long insertMedia(Media media) {

        if (contains(media)) {
            return update(media);
        } else {

            try (SQLiteDatabase db = helper.getWritableDatabase()) {
                return db.insert(media.getSQLTable(), null, media.getSQLValues());
            }
        }
    }
}
