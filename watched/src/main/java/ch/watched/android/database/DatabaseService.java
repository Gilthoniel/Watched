package ch.watched.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ch.watched.android.adapters.Media;
import ch.watched.android.database.MovieContract.MovieEntry;
import ch.watched.android.models.Episode;
import ch.watched.android.models.Movie;
import ch.watched.android.models.SearchTV;
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

    /** INSETIONS **/

    public long insert(Media media) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = media.getContentValues();
        //Log.i("-- Database Service", "Insert "+values.toString());

        return db.insert(media.getTableName(), null, values);
    }

    /** READING **/

    public Cursor read(String tableName, String[] projection, String select) {
        SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(tableName, projection, select, null, null, null, null, null);
    }

    public Cursor read(Media media) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = media.getColumnsProjection();

        String[] args = { String.valueOf(media.getID()) };
        //Log.i("-- Database Service", "Read "+args[0]);

        return db.query(media.getTableName(), projection, projection[0] + "=?", args, null, null, null, null);
    }

    public Cursor readTV(int id, boolean allValues) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String selection = EpisodeContract.EpisodeEntry.COLUMN_TV_ID + "=" + id;
        if (!allValues) {
            selection = selection + " AND " + EpisodeContract.EpisodeEntry.COLUMN_WACTHED + "=0";
        }

        String order = EpisodeContract.EpisodeEntry.COLUMN_SEASON_NB + " ASC, " +
                EpisodeContract.EpisodeEntry.COLUMN_EPISODE_NB + " ASC";

        return db.query(EpisodeContract.EpisodeEntry.TABLE_NAME, Episode.PROJECTION, selection, null, null, null, order, null);
    }

    /** DELETION **/

    public int delete(Movie movie) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String selection = MovieEntry.COLUMN_MOVIE_ID + "=" + movie.getID();

        return db.delete(MovieEntry.TABLE_NAME, selection, null);
    }

    public int delete(TV tv) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String selection = TVContract.TVEntry.COLUMN_ID + "=" + tv.getID();

        int result = db.delete(TVContract.TVEntry.TABLE_NAME, selection, null);

        selection = EpisodeContract.EpisodeEntry.COLUMN_TV_ID + "=" + tv.getID();
        result += db.delete(EpisodeContract.EpisodeEntry.TABLE_NAME, selection, null);

        return result;
    }

    public int delete(SearchTV tv) {
        Cursor cursor = read(tv);
        if (cursor.getCount() > 0 && cursor.moveToNext()) {
            TV dataTV = new TV(cursor);

            return delete(dataTV);
        } else {
            return 0;
        }
    }

    /** UPDATES **/

    public int update(Media media, int watched) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = media.getContentValues();
        values.put(MovieEntry.COLUMN_WATCHED, watched);

        String selection = MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] args = { String.valueOf(media.getID()) };

        return db.update(MovieEntry.TABLE_NAME, values, selection, args);
    }

    public int update(Episode episode, int watched) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EpisodeContract.EpisodeEntry.COLUMN_WACTHED, watched);

        String selection = EpisodeContract.EpisodeEntry.COLUMN_EPISOD_ID + "=" + episode.getID();

        return db.update(EpisodeContract.EpisodeEntry.TABLE_NAME, values, selection, null);
    }
}
