package ch.watched.android.service;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import ch.watched.android.adapters.MediaSearchAdapter;
import ch.watched.android.constants.Constants;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.models.*;
import ch.watched.android.service.utils.RequestCallback;

import java.lang.reflect.Type;

/**
 * Created by gaylor on 08/29/2015.
 * WebService to get information from the external database
 */
public class BaseWebService extends WebService {

    public static final BaseWebService instance = new BaseWebService();

    private static MovieDBConfiguration conf;

    private BaseWebService() {}

    /**
     * Get an image of Movie Database
     * @param url relative url of the image
     */
    public void loadImage(String url) {
        // Only if the configuration has been loaded because we need it to set the url
        if (conf != null) {
            Uri.Builder builder = getBuilder();
            // base url
            builder.encodedPath(conf.getUrl());
            builder.appendEncodedPath(conf.getPosterSize());
            // image url
            builder.appendEncodedPath(url);

            ConnectionService.instance.loadImage(builder);
        } else {
            Log.e("-- CONF --", "Conf for MovieDB is null");
        }
    }

    public void getConfiguration() {
        Uri.Builder builder = getBuilder();
        builder.appendEncodedPath("configuration");

        RequestCallback callback = new RequestCallback<MovieDBConfiguration>() {
            @Override
            public void onSuccess(MovieDBConfiguration result) {
                conf = result;
            }

            @Override
            public void onFailure(Errors error) {
                Log.e("-- Load Conf --", "Loading configuration fail : "+error.name());
            }

            @Override
            public Type getType() {
                return MovieDBConfiguration.class;
            }
        };

        get(builder, callback, null);
    }

    public void searchMovie(String query, final MediaSearchAdapter adapter) {
        Uri.Builder builder = getBuilder();
        builder.appendEncodedPath("search/movie");
        builder.appendQueryParameter("query", query);

        RequestCallback callback = new RequestCallback<SearchMovie.Wrapper>() {
            @Override
            public void onSuccess(SearchMovie.Wrapper wrapper) {

                adapter.addAll(wrapper.results);
            }

            @Override
            public void onFailure(Errors error) {
                Log.e("-- WebService --", "Result in a failure: " + error.name());
            }

            @Override
            public Type getType() {
                return SearchMovie.Wrapper.class;
            }
        };

        get(builder, callback, null);
    }

    public void searchTV(String query, final MediaSearchAdapter adapter) {
        Uri.Builder builder = getBuilder();
        builder.appendEncodedPath("search/tv");
        builder.appendQueryParameter("query", query);

        RequestCallback callback = new RequestCallback<SearchTV.Wrapper>() {
            @Override
            public void onSuccess(SearchTV.Wrapper wrapper) {

                adapter.addAll(wrapper.results);
            }

            @Override
            public void onFailure(Errors error) {
                Log.e("-- WebService --", "Result in a failure: " + error.name());
            }

            @Override
            public Type getType() {
                return SearchTV.Wrapper.class;
            }
        };

        get(builder, callback, null);
    }

    public void insertMovie(final long id, final MediaSearchAdapter adapter) {
        Uri.Builder builder = getBuilder();
        builder.appendEncodedPath("movie/" + id);

        RequestCallback callback = new RequestCallback<Movie>() {
            @Override
            public void onSuccess(Movie movie) {
                DatabaseService.getInstance().insert(movie);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Errors error) {
                Log.e("-- WebService --", "Result in a failure: " + error);
                Toast.makeText(adapter.getContext(), "Error when trying to add the movie", Toast.LENGTH_LONG);
            }

            @Override
            public Type getType() {
                return Movie.class;
            }
        };

        get(builder, callback, "movie_"+id);
    }

    public void insertTV(final long id, final MediaSearchAdapter adapter) {
        Uri.Builder builder = getBuilder();
        builder.appendEncodedPath("tv/" + id);

        RequestCallback callback = new RequestCallback<TV>() {
            @Override
            public void onSuccess(TV result) {

                // Insert the TV Show
                DatabaseService.getInstance().insert(result);

                // Insert the episodes
                for (TV.Season season : result.getSeasons()) {

                    BaseWebService.instance.insertSeason(id, season.season_number);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Errors error) {
                Log.e("-- WebService --", "Result in a failure: " + error);
                Toast.makeText(adapter.getContext(), "Error when trying to add the tv show", Toast.LENGTH_LONG);
            }

            @Override
            public Type getType() {
                return TV.class;
            }
        };

        get(builder, callback, "tv_" + id);
    }

    public void insertSeason(final long id, int number) {
        Uri.Builder builder = getBuilder();
        builder.appendEncodedPath("tv/"+id+"/season/"+number);

        RequestCallback callback = new RequestCallback<Season>() {
            @Override
            public void onSuccess(Season season) {

                for (Episode episode : season.episodes) {
                    episode.setTV_ID(id);

                    DatabaseService.getInstance().insert(episode);
                }
            }

            @Override
            public void onFailure(Errors error) {
                Log.e("-- WebService --", "Result in a failure");
            }

            @Override
            public Type getType() {
                return Season.class;
            }
        };

        get(builder, callback, "season_"+id+"_"+number);
    }

    /* Private Functions */

    private static Uri.Builder getBuilder() {

        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Constants.DB_BASE_URL);
        builder.appendQueryParameter("api_key", Constants.DB_KEY);

        return builder;
    }
}
