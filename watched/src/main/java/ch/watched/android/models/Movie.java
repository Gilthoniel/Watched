package ch.watched.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import ch.watched.android.constants.Utils;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.database.MovieContract;
import ch.watched.android.database.MovieContract.MovieEntry;
import ch.watched.android.database.WatcherDbHelper;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gaylor on 30.06.2015.
 * Movie data of the MovieDB
 */
public class Movie extends Media implements Serializable {

    private static final long serialVersionUID = -2183465616196807646L;

    private boolean adult;
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
    private ImagesWrapper images;
    private List<Genre> genres;

    public Movie(Cursor cursor) {
        id = cursor.getLong(1);
        title = cursor.getString(2);
        isWatched = cursor.getInt(3) == 1;
        overview = cursor.getString(4);
        vote_average = cursor.getFloat(5);
        poster_path = cursor.getString(6);
        release_date = cursor.getString(7);
        images = new ImagesWrapper();
        images.backdrops = Utils.getObject(cursor.getBlob(8), new TypeToken<List<Backdrop>>(){}.getType());
        genres = Utils.getObject(cursor.getBlob(9), new TypeToken<List<Genre>>(){}.getType());
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

    public void setWatched(boolean value) {
        isWatched = value;
        DatabaseService.getInstance().insert(this);
    }

    public List<Backdrop> getBackdrops() {
        return images.backdrops;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    @Override
    public boolean next() {
        setWatched(true);
        return true;
    }

    @Override
    public ContentValues getSQLValues() {
        ContentValues values = new ContentValues();
        values.put(WatcherDbHelper.COLUMN_ID, id);
        values.put(MovieEntry.COLUMN_TITLE, title);
        values.put(WatcherDbHelper.COLUMN_WATCHED, isWatched);
        values.put(MovieEntry.COLUMN_OVERVIEW, overview);
        values.put(MovieEntry.COLUMN_IMAGE, poster_path);
        values.put(MovieEntry.COLUMN_RELEASE, release_date);
        values.put(MovieEntry.COLUMN_SCORE, vote_average);
        values.put(MovieEntry.COLUMN_BACKDROPS, Utils.getBytes(images.backdrops));
        values.put(MovieEntry.COLUMN_GENRES, Utils.getBytes(genres));

        return values;
    }

    @Override
    public String getSQLTable() {
        return MovieEntry.TABLE_NAME;
    }

    private class ImagesWrapper implements Serializable {
        private static final long serialVersionUID = 2399617677302005418L;

        private List<Backdrop> backdrops;
    }
}
