package ch.watched.android.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gaylor on 08/29/2015.
 * TV Show season model
 */
public class Season implements DatabaseItem, Serializable {

    private static final long serialVersionUID = 3856582333013568570L;

    public List<Episode> episodes;

    @Override
    public void insert(Runnable afterAction) {
        for (Episode episode : episodes) {
            episode.insert(null);
        }
    }

    @Override
    public void remove(Runnable afterAction) {
        for (Episode episode : episodes) {
            episode.remove(null);
        }
    }

    @Override
    public void update(Runnable afterAction) {
        for (Episode episode : episodes) {
            episode.update(null);
        }
    }

    @Override
    public boolean exists() {
        throw new UnsupportedOperationException("No database for seasons");
    }
}
