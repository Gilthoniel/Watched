package ch.watched.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.database.MovieContract;
import ch.watched.android.database.MovieContract.MovieEntry;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gaylor on 30.06.2015.
 * Movie data of the MovieDB
 */
public class Movie extends Media implements Serializable {

    private static final long serialVersionUID = -2183465616196807646L;

    private boolean adult;
    private String backdrop_path;
    private List<Integer> genre_ids;
    private long id;
    private String original_language;
    private String original_title;
    private String overview;
    private String release_date;
    private String poster_path;
    private float popularity;
    private String title;
    private float vote_average;
    private long vote_count;
    private boolean isWatched;

    public Movie(Cursor cursor) {
        id = cursor.getLong(1);
        title = cursor.getString(2);
        isWatched = cursor.getInt(3) == 1;
        overview = cursor.getString(4);
        vote_average = cursor.getFloat(5);
        poster_path = cursor.getString(6);
        release_date = cursor.getString(7);
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public String getTitle() {
        if (title != null) {
            return title;
        } else if (original_title != null) {
            return original_title;
        } else {
            return "";
        }
    }

    @Override
    public String getPoster() {
        return poster_path;
    }

    @Override
    public float getRating() {
        return vote_average / 2;
    }

    public String getOriginalTitle() {
        return original_title;
    }

    @Override
    public String getOverview() {
        return overview;
    }

    public String getDate() {
        return release_date;
    }

    public boolean isWatched() {
        return isWatched;
    }

    @Override
    public ContentValues getSQLValues() {
        ContentValues values = new ContentValues();
        values.put(MovieEntry.COLUMN_MOVIE_ID, id);
        values.put(MovieEntry.COLUMN_TITLE, title);
        values.put(MovieEntry.COLUMN_WATCHED, false);
        values.put(MovieEntry.COLUMN_OVERVIEW, overview);
        values.put(MovieEntry.COLUMN_IMAGE, poster_path);
        values.put(MovieEntry.COLUMN_RELEASE, release_date);
        values.put(MovieEntry.COLUMN_SCORE, vote_average);

        return values;
    }

    @Override
    public String getSQLTable() {
        return MovieEntry.TABLE_NAME;
    }
}
