package ch.watched.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import ch.watched.android.constants.Utils;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.database.TVContract;
import ch.watched.android.database.WatcherDbHelper;
import ch.watched.android.service.BaseWebService;
import ch.watched.android.service.utils.RequestCallback;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Gaylor on 07.07.2015.
 * TV Show for MovieDatabase
 */
public class TV implements Media, DatabaseItem, Serializable, Iterable<Episode> {

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
    private List<SeasonWrapper> seasons;
    private List<Genre> genres;
    private ImagesWrapper images;
    private String first_air_date;
    private String last_air_date;

    public TV(Cursor cursor) {
        id = cursor.getInt(1);
        homepage = cursor.getString(2);
        in_production = cursor.getInt(3) == 1;
        name = cursor.getString(4);
        number_of_episodes = cursor.getInt(5);
        number_of_seasons = cursor.getInt(6);
        original_language = cursor.getString(7);
        original_title = cursor.getString(8);
        overview = cursor.getString(9);
        poster_path = cursor.getString(10);
        status = cursor.getString(11);
        type = cursor.getString(12);
        vote_average = cursor.getFloat(13);
        images = new ImagesWrapper();
        images.backdrops = Utils.getObject(cursor.getBlob(14), new TypeToken<List<Backdrop>>(){}.getType());
        genres = Utils.getObject(cursor.getBlob(15), new TypeToken<List<Genre>>(){}.getType());
        first_air_date = cursor.getString(16);
        last_air_date = cursor.getString(17);
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

    @Override
    public String getDate() {
        return first_air_date;
    }

    @Override
    public long[] getGenres() {
        long[] ids = new long[genres.size()];
        for (int i = 0; i < genres.size(); i++) {
            ids[i] = genres.get(i).id;
        }

        return ids;
    }

    @Override
    public void setWatched(boolean isWatched) {
        for (List<Episode> episodes : DatabaseService.getInstance().getEpisodes(id).values()) {
            for (Episode episode : episodes) {
                episode.setWatched(true);
            }
        }
    }

    public List<SeasonWrapper> getSeasons() {
        return seasons;
    }

    public List<Backdrop> getBackdrops() {
        return images.backdrops;
    }

    /** DATABASE_ITEM implementation **/

    @Override
    public void insert(Runnable afterAction) {
        DatabaseService.getInstance().insert(TVContract.TVEntry.TABLE_NAME, getContentValues());
        for (SeasonWrapper season : seasons) {
            BaseWebService.instance.getSeason(id, season.season_number, new RequestCallback<Season>() {
                @Override
                public void onSuccess(Season result) {
                    for (Episode episode : result.episodes) {
                        episode.setTV_ID(id);
                    }

                    result.insert(null);
                }

                @Override
                public void onFailure(Errors error) {
                    // TODO
                }

                @Override
                public Type getType() {
                    return Season.class;
                }
            });
        }

        if (afterAction != null) {
            afterAction.run();
        }
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
        DatabaseService.getInstance().update(TVContract.TVEntry.TABLE_NAME, getContentValues());
        for (SeasonWrapper season : seasons) {
            BaseWebService.instance.getSeason(id, season.season_number, new RequestCallback<Season>() {
                @Override
                public void onSuccess(Season result) {
                    for (Episode episode : result.episodes) {
                        episode.setTV_ID(id);
                    }

                    result.update(null);
                }

                @Override
                public void onFailure(Errors error) {
                    // TODO
                }

                @Override
                public Type getType() {
                    return Season.class;
                }
            });
        }

        if (afterAction != null) {
            afterAction.run();
        }
    }

    @Override
    public boolean exists() {
        return DatabaseService.getInstance().contains(TVContract.TVEntry.TABLE_NAME, id);
    }

    @Override
    public Iterator<Episode> iterator() {
        return DatabaseService.getInstance().getUnwatchedEpisode(id).iterator();
    }

    public class SeasonWrapper implements Serializable {

        private static final long serialVersionUID = -968718735486066593L;

        public long id;
        public int season_number;
    }

    private ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(WatcherDbHelper.COLUMN_ID, id);
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
        values.put(TVContract.TVEntry.COLUMN_BACKDROPS, Utils.getBytes(images.backdrops));
        values.put(TVContract.TVEntry.COLUMN_GENRES, Utils.getBytes(genres));
        values.put(TVContract.TVEntry.COLUMN_FIRST_DATE, first_air_date);
        values.put(TVContract.TVEntry.COLUMN_END_DATE, last_air_date);

        return values;
    }

    private class ImagesWrapper implements Serializable {

        private static final long serialVersionUID = 8207420254683451104L;

        private List<Backdrop> backdrops;
    }
}
