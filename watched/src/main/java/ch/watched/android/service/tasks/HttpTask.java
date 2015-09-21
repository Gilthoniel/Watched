package ch.watched.android.service.tasks;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Future;

/**
 * Created by gaylor on 27.07.15.
 *
 */
public abstract class HttpTask<T> extends AsyncTask<Uri.Builder, Void, T> {

    @Override
    protected abstract T doInBackground(Uri.Builder... builders);
}
