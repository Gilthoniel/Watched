package ch.watched.android.database;

import android.app.backup.BackupManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ch.watched.android.adapters.MovieExpandableAdapter;
import ch.watched.android.database.EpisodeContract.EpisodeEntry;
import ch.watched.android.database.MovieContract.MovieEntry;
import ch.watched.android.database.TVContract.TVEntry;
import ch.watched.android.models.Episode;
import ch.watched.android.models.Movie;
import ch.watched.android.models.TV;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Gaylor on 02.07.2015.
 * Service to save information
 */
public class DatabaseService {

    private static DatabaseService instance = new DatabaseService();

    private WatcherDbHelper helper;
    private BackupManager backup;
    private final Object mLock = new Object();

    public static DatabaseService getInstance() {
        return instance;
    }

    public void initHelper(Context context) {
        if (helper == null) {
            helper = new WatcherDbHelper(context);
        }
        backup = new BackupManager(context);
    }

    public void close() {
        helper.close();
        helper = null;
    }

    public Object getLock() {
        return mLock;
    }

    public boolean contains(String table, long id) {
        synchronized (mLock) {
            try (SQLiteDatabase db = helper.getReadableDatabase()) {

                String query = "SELECT id FROM " + table + " WHERE id=" + id;

                try (Cursor cursor = db.rawQuery(query, null)) {
                    return cursor.getCount() > 0;
                }
            }
        }
    }

    public List<Movie> getUnwatchMovies() {
        List<Movie> movies = new LinkedList<>();

        synchronized (mLock) {
            try (SQLiteDatabase db = helper.getReadableDatabase()) {

                Cursor cursor = db.rawQuery("SELECT * FROM " + MovieEntry.TABLE_NAME +
                        " WHERE " + WatcherDbHelper.COLUMN_WATCHED + "=0" +
                        " ORDER BY " + MovieEntry.COLUMN_TITLE + " ASC", null);

                while (cursor.getCount() > 0 && !cursor.isLast()) {
                    cursor.moveToNext();

                    movies.add(new Movie(cursor));
                }

                cursor.close();
            }
        }

        return movies;
    }

    public List<Movie> getMovies(MovieExpandableAdapter adapter) {
        List<Movie> movies = new LinkedList<>();

        synchronized (mLock) {
            try (SQLiteDatabase db = helper.getReadableDatabase()) {

                Cursor cursor = db.rawQuery("SELECT * FROM " + MovieEntry.TABLE_NAME +
                        " ORDER BY " + MovieEntry.COLUMN_TITLE + " ASC", null);
                while (cursor.getCount() > 0 && !cursor.isLast()) {
                    cursor.moveToNext();

                    adapter.addMovie(new Movie(cursor));
                }

                cursor.close();
            }
        }

        return movies;
    }

    public List<TV> getUnwatchedTVs() {
        List<TV> series = new LinkedList<>();

        synchronized (mLock) {
            try (SQLiteDatabase db = helper.getReadableDatabase()) {

                Cursor cursor = db.rawQuery("SELECT * FROM " + TVContract.TVEntry.TABLE_NAME + " WHERE id IN (" +
                        "SELECT DISTINCT tv_id FROM " + EpisodeEntry.TABLE_NAME + " WHERE " + WatcherDbHelper.COLUMN_WATCHED +
                        "=0) ORDER BY " + TVEntry.COLUMN_NAME + " ASC"
                        , null);

                while (cursor.getCount() > 0 && !cursor.isLast()) {
                    cursor.moveToNext();

                    series.add(new TV(cursor));
                }

                cursor.close();
            }
        }

        return series;
    }

    public List<TV> getTVs() {
        List<TV> series = new LinkedList<>();

        synchronized (mLock) {
            try (SQLiteDatabase db = helper.getReadableDatabase()) {

                Cursor cursor = db.rawQuery("SELECT * FROM " + TVEntry.TABLE_NAME +
                        " ORDER BY " + TVEntry.COLUMN_NAME + " ASC", null);

                while (cursor.getCount() > 0 && !cursor.isLast()) {
                    cursor.moveToNext();

                    series.add(new TV(cursor));
                }

                cursor.close();
            }
        }

        return series;
    }

    public Movie getMovie(long id) {

        synchronized (mLock) {
            try (SQLiteDatabase db = helper.getReadableDatabase()) {

                try (Cursor cursor = db.rawQuery("SELECT * FROM " + MovieEntry.TABLE_NAME + " WHERE id=" + id, null)) {

                    if (cursor.getCount() > 0) {
                        cursor.moveToNext();

                        return new Movie(cursor);
                    }
                }
            }
        }

        return null;
    }

    public TV getTV(long id) {

        synchronized (mLock) {
            try (SQLiteDatabase db = helper.getReadableDatabase()) {

                try (Cursor cursor = db.rawQuery("SELECT * FROM " + TVEntry.TABLE_NAME + " WHERE id=" + id, null)) {

                    if (cursor.getCount() > 0) {
                        cursor.moveToNext();

                        return new TV(cursor);
                    }
                }
            }
        }

        return null;
    }

    public Map<Integer, List<Episode>> getEpisodes(long tvID) {

        Map<Integer, List<Episode>> episodes = new HashMap<>();

        synchronized (mLock) {
            try (SQLiteDatabase db = helper.getReadableDatabase()) {

                String selection = "SELECT * FROM " + EpisodeEntry.TABLE_NAME +
                        " WHERE " + EpisodeEntry.COLUMN_TV_ID + "=" + tvID +
                        " ORDER BY " + EpisodeEntry.COLUMN_SEASON_NB + " ASC, " +
                        EpisodeEntry.COLUMN_EPISODE_NB + " ASC";

                try (Cursor cursor = db.rawQuery(selection, null)) {

                    while (cursor.getCount() > 0 && !cursor.isLast()) {
                        cursor.moveToNext();

                        Episode episode = new Episode(cursor);

                        if (!episodes.containsKey(episode.getSeasonNumber())) {
                            episodes.put(episode.getSeasonNumber(), new LinkedList<Episode>());
                        }

                        episodes.get(episode.getSeasonNumber()).add(episode);
                    }
                }
            }
        }

        return episodes;
    }

    public List<Episode> getUnwatchedEpisode(long tvID) {
        List<Episode> episodes = new LinkedList<>();

        synchronized (mLock) {
            try (SQLiteDatabase db = helper.getReadableDatabase()) {

                String selection = "SELECT * FROM " + EpisodeEntry.TABLE_NAME +
                        " WHERE " + EpisodeEntry.COLUMN_TV_ID + "=" + tvID +
                        " AND " + WatcherDbHelper.COLUMN_WATCHED + "=0" +
                        " ORDER BY " + EpisodeEntry.COLUMN_SEASON_NB + " ASC, " +
                        EpisodeEntry.COLUMN_EPISODE_NB + " ASC";

                try (Cursor cursor = db.rawQuery(selection, null)) {

                    while (cursor.getCount() > 0 && !cursor.isLast()) {
                        cursor.moveToNext();
                        episodes.add(new Episode(cursor));
                    }
                }
            }
        }
        return episodes;
    }

    /** Media insertion **/
    public long insert(String table, ContentValues values) {
        long id = values.getAsLong(WatcherDbHelper.COLUMN_ID);

        if (contains(table, id)) {
            return 0;
        } else {

            synchronized (mLock) {
                try (SQLiteDatabase db = helper.getWritableDatabase()) {
                    return db.insert(table, null, values);
                } finally {
                    backup.dataChanged();
                }
            }
        }
    }

    /** Media updating **/
    public int update(String table, ContentValues values) {

        synchronized (mLock) {
            try (SQLiteDatabase db = helper.getWritableDatabase()) {
                long id = values.getAsLong(WatcherDbHelper.COLUMN_ID);

                return db.update(table, values, "id=" + id, null);
            } finally {
                backup.dataChanged();
            }
        }
    }

    /** Media removing **/
    public int remove(String table, long id) {

        synchronized (mLock) {
            try (SQLiteDatabase db = helper.getWritableDatabase()) {

                if (table.equals(TVContract.TVEntry.TABLE_NAME)) {

                    db.delete(EpisodeEntry.TABLE_NAME, EpisodeEntry.COLUMN_TV_ID + "=" + id, null);
                }

                backup.dataChanged();

                return db.delete(table, "id=" + id, null);
            }
        }
    }
}
