package ch.watched.android.models;

import android.content.ContentValues;
import ch.watched.android.database.TVContract;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gaylor on 01.07.2015.
 * Represent a TV Show
 */
public class SearchTV extends Media implements Serializable {

    private static final long serialVersionUID = -4165225388926863214L;

    private String backdrop_path;
    private String first_air_date;
    private List<Integer> genre_ids;
    private long id;
    private String original_language;
    private String original_name;
    private String overview;
    private List<String> origin_country;
    private String poster_path;
    private double popularity;
    private String name;
    private double vote_average;
    private double vote_count;

    @Override
    public long getID() {
        return id;
    }

    @Override
    public String getTitle() {
        return name;
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
    public String getDate() {
        return first_air_date;
    }

    @Override
    public String getSQLTable() {

        return TVContract.TVEntry.TABLE_NAME;
    }

    @Override
    public ContentValues getSQLValues() {
        throw new UnsupportedOperationException("A search media doesn't fit in the database");
    }

    public class Wrapper implements Serializable {

        private static final long serialVersionUID = 938274955204277707L;

        public List<SearchTV> results;
    }
}
