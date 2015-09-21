package ch.watched.android.service;

import android.net.Uri;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.watched.android.service.tasks.*;
import ch.watched.android.service.utils.RequestCallback;

/**
 * Created by gaylor on 26.06.15.
 * Asynchronous tasks to interact with the back-end
 */
public class ConnectionService {

    public static final ConnectionService instance = new ConnectionService();

    private ExecutorService executor;
    private Set<HttpTask<?>> requests;

    private ConnectionService() {
        executor = Executors.newCachedThreadPool();
        requests = new HashSet<>();
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    /**
     * Change the implementation of the stack of requests
     * @param set the set
     */
    public synchronized void setRequestsSet(Set<HttpTask<?>> set) {
        requests = set;
    }

    public synchronized void addRequest(HttpTask<?> task) {
        requests.add(task);
    }

    public synchronized void removeRequest(HttpTask<?> task) {
        requests.remove(task);
    }

    /**
     * Cancel running requests
     */
    public void cancel() {
        for (HttpTask<?> task : requests) {
            task.cancel(true);
        }

        requests.clear();
    }

    /**
     * Execute an HTTP request
     * @param builder URL of the request
     * @param callback callback function when the request is over
     * @param <T> Type of the return object
     * @return An instance of the task (cancellable)
     */
    public <T extends Serializable> HttpTask<T> executeGetRequest(Uri.Builder builder, RequestCallback<T> callback, String cacheKey) {

        HttpTask<T> task = new GetTask<>(callback, cacheKey);
        addRequest(task);

        return (HttpTask<T>) task.executeOnExecutor(executor, builder);
    }

    public <T> HttpTask<T> executePostRequest(Uri.Builder builder, RequestCallback<Boolean> callback) {

        PostTask task = new PostTask(callback);
        addRequest(task);

        return (HttpTask<T>) task.executeOnExecutor(executor, builder);
    }
}
