package ch.watched.android.webservice;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import ch.watched.android.activity.SearchActivity;
import ch.watched.android.adapters.MediaInflater;
import ch.watched.android.models.Movie;
import ch.watched.android.models.MovieDBConfiguration;
import ch.watched.android.models.Serie;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Gaylor on 30.06.2015.
 * WebService to get information from MovieDB
 */
public class WebService {

    private static ConnectionService service = new ConnectionService();
    private static Gson gson = new Gson();
    private static MovieDBConfiguration conf;

    /**
     * Get an image of Movie Database
     * @param url relative url of the image
     * @param view where we will display the image
     */
    public static void loadImage(String url, ImageView view) {
        // Only if the configuration has been loaded because we need it to set the url
        if (conf != null) {
            Uri.Builder builder = getBuilder();
            // base url
            builder.encodedPath(conf.getUrl());
            builder.appendEncodedPath(conf.getPosterSize());
            // image url
            builder.appendEncodedPath(url);

            service.getImage(builder, view);
        }
    }

    public static void getConfiguration() {
        Uri.Builder builder = getBuilder();
        builder.appendEncodedPath("configuration");

        Callback callback = new Callback() {
            @Override
            public void onSuccess(JsonObject json) {
                conf = gson.fromJson(json, MovieDBConfiguration.class);
            }

            @Override
            public void onFailure() {
                Log.e("-- Load Conf --", "Loading configuration fail");
            }
        };

        service.get(builder, callback);
    }

    public static void search(final String type, String query, final SearchActivity.SearchFragment fragment) {
        Uri.Builder builder = getBuilder();
        builder.appendEncodedPath("search/"+type);
        builder.appendQueryParameter("query", query);

        Callback callback = new Callback() {
            @Override
            public void onSuccess(JsonObject json) {
                Type classToken;
                if (type.equals("movie")) {
                    classToken = new TypeToken<List<Movie>>(){}.getType();
                } else {
                    classToken = new TypeToken<List<Serie>>(){}.getType();
                }

                List<MediaInflater> movies = gson.fromJson(json.get("results"), classToken);
                fragment.setMedias(movies);
            }

            @Override
            public void onFailure() {
                Log.e("-- WebService --", "Result in a failure");
            }
        };

        service.get(builder, callback);
    }

    /* Private Functions */

    private static Uri.Builder getBuilder() {

        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(ConnectionService.DB_BASE_URL);
        builder.appendQueryParameter("api_key", ConnectionService.DB_KEY);

        return builder;
    }
}
