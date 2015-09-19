package ch.watched.android.service.utils;

/**
 * Created by gaylor on 23.07.15.
 *
 */
public interface RequestObserver<T> {
    void onSuccess(T object);
}
