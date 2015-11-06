package ch.watched.android.models;

import ch.watched.android.database.DatabaseService;
import ch.watched.android.database.TVContract;
import ch.watched.android.service.BaseWebService;
import ch.watched.android.service.utils.RequestCallback;
import ch.watched.android.service.utils.SimpleRequestCallback;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Gaylor on 01.07.2015.
 * Represent a TV Show
 */
public class SearchTV implements Media, DatabaseItem, Serializable {

    private static final long serialVersionUID = -4165225388926863214L;

    private String backdrop_path;
    private String first_air_date;
    private long[] genre_ids;
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
    public long[] getGenres() {
        return genre_ids;
    }

    @Override
    public void setWatched(boolean isWatched) {
        TV tv = DatabaseService.getInstance().getTV(id);
        if (tv != null) {
            tv.setWatched(true);
        } else {
            throw new IllegalStateException("Error when trying to mark a tv as watched but it is not in the database");
        }
    }

    /** DATABASE_ITEM implementation **/

    @Override
    public void insert(final Runnable afterAction) {
        BaseWebService.instance.getTV(id, new SimpleRequestCallback<TV>(TV.class) {
            @Override
            public void onSuccess(TV result) {
                result.insert(afterAction);
            }
        });
    }

    @Override
    public void remove(Runnable afterAction) {
        DatabaseService.getInstance().remove(TVContract.TVEntry.TABLE_NAME, id);

        if (afterAction != null) {
            afterAction.run();
        }
    }

    @Override
    public void update(Runnable afterAction) {
        TV show = DatabaseService.getInstance().getTV(id);
        if (show != null) {
            show.update(afterAction);
        }
    }

    @Override
    public boolean exists() {
        return DatabaseService.getInstance().contains(TVContract.TVEntry.TABLE_NAME, id);
    }

    public class Wrapper implements Serializable {

        private static final long serialVersionUID = 938274955204277707L;

        public List<SearchTV> results;
    }
}
