package ch.watched.android.service;

import android.net.Uri;
import ch.watched.android.service.utils.RequestCallback;

import java.io.Serializable;

/**
 * Created by gaylor on 27.06.15.
 * Base of a web service implementation
 */
public abstract class WebService {

    /**
     * GET HTTP request
     * @param builder queries parameter
     * @param callback call when the request is over
     * @param <T> Type of the object requested
     */
    public <T extends Serializable> void get(Uri.Builder builder, RequestCallback<T> callback, String cacheKey) {

        ConnectionService.instance.executeGetRequest(builder, callback, cacheKey);
    }

    /**
     * POST HTTP request
     * @param builder query parameters
     * @param callback callback function
     * @param <T> type of the return
     */
    public <T> void post(Uri.Builder builder, RequestCallback<Boolean> callback) {

        // POST request without caching
        ConnectionService.instance.executePostRequest(builder, callback);
    }
}
