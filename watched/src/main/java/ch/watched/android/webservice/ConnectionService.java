package ch.watched.android.webservice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Gaylor on 30.06.2015.
 * HTTP commands
 */
public class ConnectionService {

    public static String DB_KEY = "206f79eda0e7499536358bbfd3e47743";
    public static String DB_BASE_URL = "http://api.themoviedb.org/3/";

    private ExecutorService executor = Executors.newCachedThreadPool();
    private JsonParser parser = new JsonParser();
    private final LruCache<String, Bitmap> imageCache = new LruCache<>(100); // 100 entries

    public void get(Uri.Builder builder, Callback callback) {

        GetTask task = new GetTask(callback);
        task.executeOnExecutor(executor, builder);
    }

    public void getImage(Uri.Builder builder, ImageView view) {

        ImageTask task = new ImageTask(view);
        task.executeOnExecutor(executor, builder);
    }

    /* PRIVATE */

    private class  GetTask extends AsyncTask<Uri.Builder, Integer, JsonObject> {

        private Callback callback;

        public GetTask(Callback action) {
            callback = action;
        }

        @Override
        protected JsonObject doInBackground(Uri.Builder... paths) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(paths[0].toString());
                connection = (HttpURLConnection) url.openConnection();
                String result = IOUtils.toString(connection.getInputStream());

                return parser.parse(result).getAsJsonObject();
            } catch (IOException e) {

                Log.e("-- IO Exception --", e.getMessage());
            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(JsonObject json) {
            if (json != null) {
                callback.onSuccess(json);
            } else {
                callback.onFailure();
            }
        }
    }

    private class ImageTask extends AsyncTask<Uri.Builder, Void, Bitmap> {

        private ImageView view;

        public ImageTask(ImageView image) {
            view = image;
        }

        @Override
        protected Bitmap doInBackground(Uri.Builder... urls) {
            String url = urls[0].toString();
            Bitmap image = null;

            // Try to acquire the image in the cache
            synchronized (imageCache) {
                if (imageCache.get(url) != null) {
                    return imageCache.get(url);
                }
            }

            try {
                InputStream in = new URL(url).openStream();
                image = BitmapFactory.decodeStream(in);

                synchronized (imageCache) {
                    if (imageCache.get(url) == null) {
                        imageCache.put(url, image);
                    }
                }
            } catch (Exception e) {
                Log.e("-- Image Loading --", e.getMessage());
            }
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            if (result == null) {
                view.setImageResource(android.R.drawable.ic_menu_report_image);
            } else {
                view.setImageBitmap(result);
            }
        }
    }
}
