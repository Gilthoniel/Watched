package ch.watched.android.service;

import android.widget.TextView;
import ch.watched.android.models.Genre;

import java.util.*;

/**
 * Created by Gaylor on 20.10.2015.
 * Manage the list of genre with a map id -> title
 */
public class GenreManager {

    private static final GenreManager instance = new GenreManager();

    private Map<Long, String> mGenres;
    private List<Tuple> mQueue;

    private GenreManager() {
        mGenres = new HashMap<>();
        mQueue = new LinkedList<>();
    }

    public static GenreManager instance() {
        return instance;
    }

    public synchronized void addAll(Collection<Genre> genres) {
        for (Genre genre : genres) {
            mGenres.put(genre.id, genre.name);
        }

        Iterator<Tuple> it = mQueue.iterator();
        while (it.hasNext()) {
            Tuple tuple = it.next();
            boolean hasEverything = true;

            for (long id : tuple.ids) {
                if (!mGenres.containsKey(id)) {
                    hasEverything = false;
                }
            }

            if (hasEverything) {
                populate(tuple.ids, tuple.view, false);
                it.remove();
            }
        }
    }

    public void populate(long[] ids, TextView view, boolean firstTry) {
        StringBuilder builder = new StringBuilder();
        for (long id : ids) {
            if (mGenres.containsKey(id)) {
                if (builder.length() > 0) {
                    builder.append(", ");
                }

                builder.append(mGenres.get(id));
            } else if (firstTry) {
                mQueue.add(new Tuple(view, ids));
                BaseWebService.instance.getGenres();
                return;
            }
        }

        view.setText(builder.toString());
        view.invalidate();
    }

    public void populate(long[] ids, TextView view) {
        populate(ids, view, true);
    }

    private class Tuple {
        public TextView view;
        public long[] ids;

        public Tuple(TextView view, long[] ids) {
            this.view = view;
            this.ids = ids;
        }
    }
}
