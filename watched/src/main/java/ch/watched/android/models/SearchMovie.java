package ch.watched.android.models;

import android.content.ContentValues;
import ch.watched.android.database.MovieContract;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gaylor on 08/29/2015.
 * Movie model for a search
 */
public class SearchMovie extends Media implements Serializable {

    private static final long serialVersionUID = -5855023050595050269L;

    private boolean adult;
    private String backdrop_path;
    private int[] genre_ids;
    private long id;
    private String original_language;
    private String original_title;
    private String overview;
    private String release_date;
    private String poster_path;
    private double popularity;
    private String title;
    private boolean video;
    private double vote_average;
    private int vote_count;

    @Override
    public long getID() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getPoster() {
        return poster_path;
    }

    @Override
    public float getRating() {
        return (float) vote_average;
    }

    @Override
    public String getOverview() {
        return overview;
    }

    @Override
    public String getSQLTable() {
        return MovieContract.MovieEntry.TABLE_NAME;
    }

    @Override
    public ContentValues getSQLValues() {

        throw new UnsupportedOperationException("A search media doesn't fit in the database");
    }

    public class Wrapper implements Serializable {

        private static final long serialVersionUID = 6518390442384143610L;

        public List<SearchMovie> results;
    }
}
