package ch.watched.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ch.watched.android.adapters.MovieExpandableAdapter;
import ch.watched.android.database.EpisodeContract.EpisodeEntry;
import ch.watched.android.database.MovieContract.MovieEntry;
import ch.watched.android.database.TVContract.TVEntry;
import ch.watched.android.models.*;

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

    public List<Movie> getUnwatchMovies() {
        List<Movie> movies = new LinkedList<>();

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

        return movies;
    }

    public List<Movie> getMovies(MovieExpandableAdapter adapter) {
        List<Movie> movies = new LinkedList<>();

        try (SQLiteDatabase db = helper.getReadableDatabase()) {

            Cursor cursor = db.rawQuery("SELECT * FROM " + MovieEntry.TABLE_NAME +
                    " ORDER BY " + MovieEntry.COLUMN_TITLE + " ASC", null);
            while (cursor.getCount() > 0 && !cursor.isLast()) {
                cursor.moveToNext();

                adapter.addMovie(new Movie(cursor));
            }

            cursor.close();
        }

        return movies;
    }

    public List<TV> getUnwatchedTVs() {
        List<TV> series = new LinkedList<>();

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

        return series;
    }

    public List<TV> getTVs() {
        List<TV> series = new LinkedList<>();

        try (SQLiteDatabase db = helper.getReadableDatabase()) {

            Cursor cursor = db.rawQuery("SELECT * FROM " + TVEntry.TABLE_NAME +
                    " ORDER BY " + TVEntry.COLUMN_NAME + " ASC", null);

            while (cursor.getCount() > 0 && !cursor.isLast()) {
                cursor.moveToNext();

                series.add(new TV(cursor));
            }

            cursor.close();
        }

        return series;
    }

    public Movie getMovie(long id) {

        try (SQLiteDatabase db = helper.getReadableDatabase()) {

            try (Cursor cursor = db.rawQuery("SELECT * FROM " + MovieEntry.TABLE_NAME + " WHERE id=" + id, null)) {

                if (cursor.getCount() > 0) {
                    cursor.moveToNext();

                    return new Movie(cursor);
                }
            }
        }

        return null;
    }

    public TV getTV(long id) {

        try (SQLiteDatabase db = helper.getReadableDatabase()) {

            try (Cursor cursor = db.rawQuery("SELECT * FROM " + TVEntry.TABLE_NAME + " WHERE id="+id, null)) {

                if (cursor.getCount() > 0) {
                    cursor.moveToNext();

                    return new TV(cursor);
                }
            }
        }

        return null;
    }

    public Map<Integer, List<Episode>> getEpisodes(long tvID) {

        Map<Integer, List<Episode>> episodes = new HashMap<>();

        try (SQLiteDatabase db = helper.getReadableDatabase()) {

            String selection = "SELECT * FROM " + EpisodeEntry.TABLE_NAME +
                    " WHERE " + EpisodeEntry.COLUMN_TV_ID + "=" +tvID +
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

        return episodes;
    }

    public Episode getUnwatchedEpisode(long tvID) {

        try (SQLiteDatabase db = helper.getReadableDatabase()) {

            String selection = "SELECT * FROM " + EpisodeEntry.TABLE_NAME +
                    " WHERE " + EpisodeEntry.COLUMN_TV_ID + "=" + tvID +
                    " AND " + WatcherDbHelper.COLUMN_WATCHED + "=0" +
                    " ORDER BY " + EpisodeEntry.COLUMN_SEASON_NB + " ASC, " +
                    EpisodeEntry.COLUMN_EPISODE_NB + " ASC LIMIT 1";

            try (Cursor cursor = db.rawQuery(selection, null)) {

                if (cursor.getCount() > 0) {
                    cursor.moveToNext();
                    return new Episode(cursor);
                } else {

                    return null;
                }
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

    public int update(Media media, boolean updateWatched) {
        try (SQLiteDatabase db = helper.getWritableDatabase()) {

            ContentValues values = media.getSQLValues();
            if (!updateWatched) {
                values.remove(WatcherDbHelper.COLUMN_WATCHED);
            }

            return db.update(media.getSQLTable(), media.getSQLValues(), "id=" + media.getID(), null);
        }
    }

    public int update(Media media) {
        return update(media, true);
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
