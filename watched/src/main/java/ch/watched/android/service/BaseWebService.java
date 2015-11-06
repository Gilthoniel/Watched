package ch.watched.android.service;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import ch.watched.android.adapters.DiscoverCardAdapter;
import ch.watched.android.adapters.MediaSearchAdapter;
import ch.watched.android.constants.Constants;
import ch.watched.android.models.*;
import ch.watched.android.service.utils.RequestCallback;

import java.io.Serializable;
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

    public void discover(String type, String sorting, int date_ge, final DiscoverCardAdapter adapter) {
        Uri.Builder builder = getBuilder();
        builder.appendEncodedPath("discover/" + type);
        builder.appendQueryParameter("sort_by", sorting);
        builder.appendQueryParameter("primary_release_date.gte", date_ge+"-01-01");

        RequestCallback<? extends Serializable> callback;
        if (type.equals("movie")) {
            callback = new RequestCallback<SearchMovie.Wrapper>() {
                @Override
                public void onSuccess(SearchMovie.Wrapper result) {
                    adapter.putAll(result.results);
                }

                @Override
                public void onFailure(Errors error) {
                    Toast.makeText(adapter.getContext(), "An error occurred :(", Toast.LENGTH_SHORT).show();
                }

                @Override
                public Type getType() {
                    return SearchMovie.Wrapper.class;
                }
            };
        } else {
            callback = new RequestCallback<SearchTV.Wrapper>() {
                @Override
                public void onSuccess(SearchTV.Wrapper result) {
                    adapter.putAll(result.results);
                }

                @Override
                public void onFailure(Errors error) {
                    Toast.makeText(adapter.getContext(), "An error occurred :(", Toast.LENGTH_SHORT).show();
                }

                @Override
                public Type getType() {
                    return SearchTV.Wrapper.class;
                }
            };
        }

        get(builder, callback, null);
    }

    public void getGenres() {
        Uri.Builder builder = getBuilder();
        builder.appendEncodedPath("genre/movie/list");

        RequestCallback<Genre.Wrapper> callback = new RequestCallback<Genre.Wrapper>() {
            @Override
            public void onSuccess(Genre.Wrapper result) {
                GenreManager.instance().addAll(result.genres);
            }

            @Override
            public void onFailure(Errors error) {
                Log.e("--GENRES--", "Error when loading genres: "+error.name());
            }

            @Override
            public Type getType() {
                return Genre.Wrapper.class;
            }
        };
        get(builder, callback, null);

        builder = getBuilder();
        builder.appendEncodedPath("genre/tv/list");
        get(builder, callback, null);
    }

    public void getMovie(final long id, RequestCallback<Movie> callback) {
        Uri.Builder builder = getBuilder();
        builder.appendEncodedPath("movie/" + id);
        builder.appendQueryParameter("append_to_response", "images");

        get(builder, callback, "movie_" + id);
    }

    public void getTV(final long id, RequestCallback<TV> callback) {
        Uri.Builder builder = getBuilder();
        builder.appendEncodedPath("tv/" + id);
        builder.appendQueryParameter("append_to_response", "images");

        // TODO : check in the database before (update timestamp)

        get(builder, callback, "tv_" + id);
    }

    public void getSeason(final long id, int number, RequestCallback<Season> callback) {
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
}
