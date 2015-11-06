package ch.watched.android.service.utils;

import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * Created by Gaylor on 06.11.2015.
 *
 */
public abstract class SimpleRequestCallback<T extends Serializable> implements RequestCallback<T> {

    private final Class<T> typeParameter;

    public SimpleRequestCallback(Class<T> type) {
        typeParameter = type;
    }

    @Override
    public void onFailure(Errors error) {
        Log.e("--WEBSERVIC--", "Message: " + error.name());
    }

    @Override
    public Type getType() {
        return typeParameter;
    }
}
