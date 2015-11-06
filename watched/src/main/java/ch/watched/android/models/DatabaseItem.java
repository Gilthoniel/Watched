package ch.watched.android.models;

import android.support.annotation.Nullable;

/**
 * Created by Gaylor on 05.11.2015.
 * Database management of the object
 */
public interface DatabaseItem {

    void insert(@Nullable Runnable afterAction);
    void remove(@Nullable Runnable afterAction);
    void update(@Nullable Runnable afterAction);
    boolean exists();
}
