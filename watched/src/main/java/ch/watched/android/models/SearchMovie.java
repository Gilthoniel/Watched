package ch.watched.android.models;

import android.content.ContentValues;
import ch.watched.android.constants.Utils;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.database.MovieContract;
import ch.watched.android.service.BaseWebService;
import ch.watched.android.service.utils.RequestCallback;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by gaylor on 08/29/2015.
 * Movie model for a search
 */
public class SearchMovie extends Media implements Serializable {

    private static final long serialVersionUID = -5855023050595050269L;

    private boolean adult;
    private String backdrop_path;
    private long[] genre_ids;
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
    public String getDate() {
        return Utils.parseDate(release_date);
    }

    public long[] getGenres() {
        return genre_ids;
    }

    @Override
    public String getSQLTable() {
        return MovieContract.MovieEntry.TABLE_NAME;
    }

    @Override
    public ContentValues getSQLValues() {

        throw new UnsupportedOperationException("A search media doesn't fit in the database");
    }

    @Override
    public void insertIntoDatabase(final Runnable runnable) {
        BaseWebService.instance.getMovie(id, new RequestCallback<Movie>() {
            @Override
            public void onSuccess(Movie movie) {
                movie.insertIntoDatabase(runnable);
            }

            @Override
            public void onFailure(Errors error) {}

            @Override
            public Type getType() {
                return Movie.class;
            }
        });
    }

    public class Wrapper implements Serializable {

        private static final long serialVersionUID = 6518390442384143610L;

        public List<SearchMovie> results;
    }
}
