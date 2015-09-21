package ch.watched.android.service;

import android.app.Dialog;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import ch.watched.android.adapters.MediaSearchAdapter;
import ch.watched.android.constants.Constants;
import ch.watched.android.constants.Utils;
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

    private BaseWebService() {}

    public void getConfiguration(RequestCallback<MovieDBConfiguration> callback) {
        Uri.Builder builder = getBuilder();
        builder.appendEncodedPath("configuration");

        get(builder, callback, "moviedb_config");
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

        final Dialog progress = Utils.createProgressDialog(adapter.getContext());
        progress.show();

        getMovie(id, new RequestCallback<Movie>() {
            @Override
            public void onSuccess(Movie movie) {
                DatabaseService.getInstance().insert(movie);
                adapter.notifyDataSetChanged();

                progress.dismiss();
            }

            @Override
            public void onFailure(Errors error) {
                Log.e("-- WebService --", "Result in a failure: " + error);
                Toast.makeText(adapter.getContext(), "Error when trying to add the movie", Toast.LENGTH_LONG).show();

                progress.dismiss();
            }

            @Override
            public Type getType() {
                return Movie.class;
            }
        });
    }

    public void updateMovie(final Movie old, final Callable callable) {

        getMovie(old.getID(), new RequestCallback<Movie>() {
            @Override
            public void onSuccess(Movie movie) {
                movie.setWatched(old.isWatched());

                callable.call();
            }

            @Override
            public void onFailure(Errors error) {
                Log.e("-- WebService --", "Result in a failure: " + error);
            }

            @Override
            public Type getType() {
                return Movie.class;
            }
        });
    }

    public void insertTV(final long id, final MediaSearchAdapter adapter) {

        final Dialog progress = Utils.createProgressDialog(adapter.getContext());
        progress.show();

        getTV(id, new RequestCallback<TV>() {
            @Override
            public void onSuccess(TV result) {

                // Insert the TV Show
                DatabaseService.getInstance().insert(result);

                // Insert the episodes
                for (TV.Season season : result.getSeasons()) {

                    if (season.season_number > 0) {
                        BaseWebService.instance.insertSeason(id, season.season_number);
                    }
                }

                adapter.notifyDataSetChanged();

                progress.dismiss();
            }

            @Override
            public void onFailure(Errors error) {
                Log.e("-- WebService --", "Result in a failure: " + error);

                progress.dismiss();

                Toast.makeText(adapter.getContext(), "Error when trying to add the tv show", Toast.LENGTH_LONG).show();
            }

            @Override
            public Type getType() {
                return TV.class;
            }
        });
    }

    public void updateTV(final TV tv, final Callable callable) {

        getTV(tv.getID(), new RequestCallback<TV>() {
            @Override
            public void onSuccess(TV result) {
                // Update the media
                DatabaseService.getInstance().insert(result);
                // Update the episodes
                for (TV.Season season : result.getSeasons()) {

                    if (season.season_number > 0) {
                        BaseWebService.instance.insertSeason(tv.getID(), season.season_number);
                    }
                }

                callable.call();
            }

            @Override
            public void onFailure(Errors error) {
                Log.e("-- WebService --", "Result in a failure: " + error);
            }

            @Override
            public Type getType() {
                return TV.class;
            }
        });
    }

    private void insertSeason(final long id, int number) {

        getSeason(id, number, new RequestCallback<Season>() {
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
        });
    }

    private void getMovie(final long id, RequestCallback<Movie> callback) {
        Uri.Builder builder = getBuilder();
        builder.appendEncodedPath("movie/" + id);
        builder.appendQueryParameter("append_to_response", "images");

        get(builder, callback, "movie_" + id);
    }

    private void getTV(final long id, RequestCallback<TV> callback) {
        Uri.Builder builder = getBuilder();
        builder.appendEncodedPath("tv/" + id);
        builder.appendQueryParameter("append_to_response", "images");

        get(builder, callback, "tv_" + id);
    }

    private void getSeason(final long id, int number, RequestCallback<Season> callback) {
        Uri.Builder builder = getBuilder();
        builder.appendEncodedPath("tv/" + id + "/season/" + number);

        get(builder, callback, "season_" + id + "_" + number);
    }

    /* Private Functions */

    private static Uri.Builder getBuilder() {

        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Constants.DB_BASE_URL);
        builder.appendQueryParameter("api_key", Constants.DB_KEY);

        return builder;
    }

    public interface Callable {
        void call();
    }
}
