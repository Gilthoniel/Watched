package ch.watched.android.models;

import ch.watched.android.constants.Utils;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.database.MovieContract;
import ch.watched.android.service.BaseWebService;
import ch.watched.android.service.WebService;
import ch.watched.android.service.utils.RequestCallback;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by gaylor on 08/29/2015.
 * Movie model for a search
 */
public class SearchMovie implements Media, DatabaseItem, Serializable {

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

    /** MEDIA INTERFACE **/
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

    @Override
    public long[] getGenres() {
        return genre_ids;
    }

    @Override
    public void setWatched(boolean isWatched) {
        Movie movie = DatabaseService.getInstance().getMovie(id);
        if (movie != null) {
            movie.setWatched(true);
        } else {
            throw new IllegalStateException("Error when trying to mark a movie as watched but it is not in the database");
        }
    }

    /** DATABASE_ITEM **/

    @Override
    public void insert(final Runnable afterAction) {
        BaseWebService.instance.getMovie(id, new RequestCallback<Movie>() {
            @Override
            public void onSuccess(Movie result) {
                result.insert(afterAction);
            }

            @Override
            public void onFailure(Errors error) {
                // TODO
            }

            @Override
            public Type getType() {
                return Movie.class;
            }
        });
    }

    @Override
    public void remove(Runnable afterAction) {
        DatabaseService.getInstance().remove(MovieContract.MovieEntry.TABLE_NAME, id);

        if (afterAction != null) {
            afterAction.run();
        }
    }

    @Override
    public void update(Runnable afterAction) {
        Movie movie = DatabaseService.getInstance().getMovie(id);
        if (movie != null) {
            movie.update(afterAction);
        }
    }

    @Override
    public boolean exists() {
        return DatabaseService.getInstance().contains(MovieContract.MovieEntry.TABLE_NAME, id);
    }

    public class Wrapper implements Serializable {

        private static final long serialVersionUID = 6518390442384143610L;

        public List<SearchMovie> results;
    }
}
