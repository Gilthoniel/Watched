package ch.watched.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.database.EpisodeContract;
import ch.watched.android.database.EpisodeContract.EpisodeEntry;
import ch.watched.android.database.WatcherDbHelper;

import java.io.Serializable;

/**
 * Created by Gaylor on 06.07.2015.
 * Episode from the MovieDatabase
 */
public class Episode extends Media implements Serializable {

    private static final long serialVersionUID = -8663364071143389959L;

    public Episode(Cursor cursor) {
        id = cursor.getInt(1);
        air_date = cursor.getString(2);
        episode_number = cursor.getInt(3);
        name = cursor.getString(4);
        overview = cursor.getString(5);
        season_number = cursor.getInt(6);
        still_path = cursor.getString(7);
        vote_average = cursor.getFloat(8);
        tv_id = cursor.getInt(9);
        watched = cursor.getInt(10) == 1;
    }

    private long id;
    private String air_date;
    private int episode_number;
    private String name;
    private String overview;
    private int season_number;
    private String still_path;
    private float vote_average;
    private long tv_id;
    private boolean watched;

    public long getID() {
        return id;
    }

    public String getPoster() {
        return still_path;
    }

    public String getDate() {
        return air_date;
    }

    public int getEpisodeNumber() {
        return episode_number;
    }

    public String getTitle() {
        return name;
    }

    public String getOverview() {
        return overview;
    }

    public int getSeasonNumber() {
        return season_number;
    }

    public String getStillPath() {
        return still_path;
    }

    public float getRating() {
        return vote_average;
    }

    public long getTV_ID() {
        return tv_id;
    }

    public void setTV_ID(long value) {
        tv_id = value;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;

        DatabaseService.getInstance().insert(this);
    }

    @Override
    public ContentValues getSQLValues() {
        ContentValues values = new ContentValues();
        values.put(WatcherDbHelper.COLUMN_ID, id);
        values.put(EpisodeEntry.COLUMN_DATE, air_date);
        values.put(EpisodeEntry.COLUMN_EPISODE_NB, episode_number);
        values.put(EpisodeEntry.COLUMN_NAME, name);
        values.put(EpisodeEntry.COLUMN_OVERVIEW, overview);
        values.put(EpisodeEntry.COLUMN_SEASON_NB, season_number);
        values.put(EpisodeEntry.COLUMN_STILL_PATH, still_path);
        values.put(EpisodeEntry.COLUMN_SCORE, vote_average);
        values.put(EpisodeEntry.COLUMN_TV_ID, tv_id);
        values.put(WatcherDbHelper.COLUMN_WATCHED, watched);

        return values;
    }

    @Override
    public String getSQLTable() {
        return EpisodeEntry.TABLE_NAME;
    }

    @Override
    public String toString() {
        return "Episode["+id+", "+episode_number+", "+season_number+", "+name+"]";
    }
}
