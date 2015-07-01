package ch.watched.android.webservice;

import com.google.gson.JsonObject;

/**
 * Created by Gaylor on 30.06.2015.
 * Functions that the Connection Service calls when requests are over
 */
public interface Callback {
    void onSuccess(JsonObject json);
    void onFailure();
}
