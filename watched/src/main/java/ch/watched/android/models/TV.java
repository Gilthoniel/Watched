package ch.watched.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import ch.watched.android.database.TVContract;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gaylor on 07.07.2015.
 * TV Show for MovieDatabase
 */
public class TV extends Media implements Serializable {

    private static final long serialVersionUID = -968718735212066593L;

    private String homepage;
    private long id;
    private boolean in_production;
    private String name;
    private int number_of_episodes;
    private int number_of_seasons;
    private String original_language;
    private String original_title;
    private String overview;
    private String poster_path;
    private String status;
    private String type;
    private float vote_average;
    private List<Season> seasons;

    public TV(Cursor cursor) {
        id = cursor.getInt(0);
        homepage = cursor.getString(1);
        in_production = cursor.getInt(2) == 1;
        original_language = cursor.getString(3);
        name = cursor.getString(4);
        number_of_episodes = cursor.getInt(5);
        number_of_seasons = cursor.getInt(6);
        overview = cursor.getString(7);
        poster_path = cursor.getString(8);
        vote_average = cursor.getFloat(9);
        status = cursor.getString(10);
        type = cursor.getString(11);
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public String getPoster() {
        return poster_path;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public float getRating() {
        return vote_average / 2;
    }

    @Override
    public String getOverview() {
        return overview;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    @Override
    public ContentValues getSQLValues() {
        ContentValues values = new ContentValues();
        values.put(TVContract.TVEntry.COLUMN_ID, id);
        values.put(TVContract.TVEntry.COLUMN_HOMEPAGE, homepage);
        values.put(TVContract.TVEntry.COLUMN_IN_PROD, in_production ? 1 : 0);
        values.put(TVContract.TVEntry.COLUMN_LANGUAGE, original_language);
        values.put(TVContract.TVEntry.COLUMN_NAME, name);
        values.put(TVContract.TVEntry.COLUMN_NB_EPISODES, number_of_episodes);
        values.put(TVContract.TVEntry.COLUMN_NB_SEASONS, number_of_seasons);
        values.put(TVContract.TVEntry.COLUMN_ORIGINAL_TITLE, original_title);
        values.put(TVContract.TVEntry.COLUMN_OVERVIEW, overview);
        values.put(TVContract.TVEntry.COLUMN_POSTER, poster_path);
        values.put(TVContract.TVEntry.COLUMN_SCORE, vote_average);
        values.put(TVContract.TVEntry.COLUMN_STATUS, status);
        values.put(TVContract.TVEntry.COLUMN_TYPE, type);

        return values;
    }

    @Override
    public String getSQLTable() {
        return TVContract.TVEntry.TABLE_NAME;
    }

    public class Season {
        public long id;
        public int season_number;
    }
}
