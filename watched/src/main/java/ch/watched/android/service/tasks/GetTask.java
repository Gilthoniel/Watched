package ch.watched.android.service.tasks;

import android.net.Uri;
import android.util.Log;
import ch.watched.android.constants.Constants;
import ch.watched.android.service.CacheManager;
import ch.watched.android.service.ConnectionService;
import ch.watched.android.service.MessageParser;
import ch.watched.android.service.utils.RequestCallback;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by gaylor on 27.07.15.
 * Execute an HTTP request to get JSON information and return into a java class
 */
public class GetTask<T> extends HttpTask<T> {

    private Future<T> mFuture;
    private RequestCallback<T> callback;
    private RequestCallback.Errors error;
    private String mKey;

    public GetTask(RequestCallback<T> callback, String cacheKey) {
        this.callback = callback;

        error = RequestCallback.Errors.SUCCESS;
        mKey = cacheKey;

        mFuture = null;
    }

    @Override
    public void cancel() {

        if (mFuture != null) {
            mFuture.cancel(true);
            Log.d("__FUTURE__", "Future cancelled");
        }

        super.cancel(true);
    }

    @Override
    protected T doInBackground(final Uri.Builder... builders) {
        // Try to acquire from the cache
        if (mKey != null) {
            T object = CacheManager.instance().get(mKey);
            if (object != null) {
                return object;
            }
        }

        if (builders.length <= 0) {
            return null;
        }

        try {
            URL url = new URL(builders[0].build().toString());
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            Log.d("__INTERNET__", "Connection open for params:" + url);

            try {
                mFuture = ConnectionService.instance.getExecutor().submit(new Callable<T>() {
                    @Override
                    public T call() throws Exception {

                        try {

                            T object = MessageParser.fromJson(new InputStreamReader(connection.getInputStream()), callback.getType());
                            if (object != null && mKey != null) {
                                CacheManager.instance().add(mKey, object, Constants.CACHE_EXPIRATION_TIME);
                            }

                            return object;

                        } catch (JsonSyntaxException e) {

                            error = RequestCallback.Errors.JSON;
                            Log.e("Json error", "Message: " + e.getMessage());

                        }

                        return null;
                    }
                });

                return mFuture.get();

            }  catch (InterruptedException | ExecutionException | CancellationException ignored) {

                Log.e("__FUTURE__", "Class: "+ignored.getMessage());
            } finally {

                connection.disconnect();
            }

        } catch (IOException e) {

            Log.e("__CONNECTION_SERVICE__", "Message: " + e.getMessage());
            error = RequestCallback.Errors.CONNECTION;
        }

        return null;
    }

    @Override
    protected void onPostExecute(T result) {

        // Remove from the active request list
        ConnectionService.instance.removeRequest(this);

        // Use the callback here, because this function is executed in the UI Thread !
        if (result != null) {
            callback.onSuccess(result);
        } else {
            callback.onFailure(error);
        }
    }

    @Override
    protected void onCancelled(T result) {

        ConnectionService.instance.removeRequest(this);
    }
}
