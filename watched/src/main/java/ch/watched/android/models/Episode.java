package ch.watched.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.view.View;
import android.widget.RelativeLayout;
import ch.watched.android.adapters.Media;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.database.EpisodeContract;
import ch.watched.android.database.EpisodeContract.EpisodeEntry;

/**
 * Created by Gaylor on 06.07.2015.
 * Episode from the MovieDatabase
 */
public class Episode implements Media {

    public static final String[] PROJECTION = new String[] {
            EpisodeEntry.COLUMN_EPISOD_ID,
            EpisodeEntry.COLUMN_DATE,
            EpisodeEntry.COLUMN_EPISODE_NB,
            EpisodeEntry.COLUMN_NAME,
            EpisodeEntry.COLUMN_OVERVIEW,
            EpisodeEntry.COLUMN_SCORE,
            EpisodeEntry.COLUMN_SEASON_NB,
            EpisodeEntry.COLUMN_STILL_PATH,
            EpisodeEntry.COLUMN_TV_ID,
            EpisodeEntry.COLUMN_WACTHED
    };

    public Episode(Cursor cursor) {
        id = cursor.getInt(0);
        air_date = cursor.getString(1);
        episode_number = cursor.getInt(2);
        name = cursor.getString(3);
        overview = cursor.getString(4);
        vote_average = cursor.getFloat(5);
        season_number = cursor.getInt(6);
        still_path = cursor.getString(7);
        tv_id = cursor.getInt(8);
        watched = cursor.getInt(9) == 1;
    }

    private int id;
    private String air_date;
    private int episode_number;
    private String name;
    private String overview;
    private int season_number;
    private String still_path;
    private float vote_average;
    private int tv_id;
    private boolean watched;

    public String getDate() {
        return air_date;
    }

    public int getEpisodeNumber() {
        return episode_number;
    }

    public String getName() {
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

    public float getScore() {
        return vote_average;
    }

    public int getTV_ID() {
        return tv_id;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean value) {
        watched = value;
        DatabaseService.getInstance().update(this, value ? 1 : 0);
    }

    @Override
    public View inflate(RelativeLayout layout) {
        return null;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(EpisodeEntry.COLUMN_EPISOD_ID, id);
        values.put(EpisodeEntry.COLUMN_DATE, air_date);
        values.put(EpisodeEntry.COLUMN_EPISODE_NB, episode_number);
        values.put(EpisodeEntry.COLUMN_NAME, name);
        values.put(EpisodeEntry.COLUMN_OVERVIEW, overview);
        values.put(EpisodeEntry.COLUMN_SEASON_NB, season_number);
        values.put(EpisodeEntry.COLUMN_STILL_PATH, still_path);
        values.put(EpisodeEntry.COLUMN_SCORE, vote_average);
        values.put(EpisodeEntry.COLUMN_TV_ID, tv_id);
        values.put(EpisodeEntry.COLUMN_WACTHED, watched ? 1 : 0);

        return values;
    }

    public void setTV_ID(int id) {
        tv_id = id;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getTableName() {
        return EpisodeContract.EpisodeEntry.TABLE_NAME;
    }

    @Override
    public String[] getColumnsProjection() {
        return new String[] {

        };
    }

    @Override
    public int getLayoutID() {
        return -1;
    }

    @Override
    public void updateState() {
        // TODO
    }

    @Override
    public String toString() {
        return "Episode["+id+", "+episode_number+", "+season_number+", "+name+"]";
    }
}
