package ch.watched.android.service.utils;

import java.lang.reflect.Type;

/**
 * Created by gaylor on 26.06.15.
 * Action to perform when a request is over
 */
public interface RequestCallback<T> {

    enum Errors {SUCCESS, JSON, CONNECTION}

    /**
     * Call when the request onSuccess the result
     * @param result Instance of the result
     */
    void onSuccess(T result);

    /**
     * Call if the request fails
     */
    void onFailure(Errors error);

    /**
     * Return the type of the result
     * @return Type reflection
     */
    Type getType();
}
