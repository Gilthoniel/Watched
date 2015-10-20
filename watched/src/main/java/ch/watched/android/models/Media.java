package ch.watched.android.models;

import android.content.ContentValues;

/**
 * Created by gaylor on 08/29/2015.
 *
 */
public abstract class Media {

    public abstract long getID();
    public abstract String getPoster();
    public abstract String getTitle();
    public abstract float getRating();
    public abstract String getOverview();
    public abstract String getDate();

    public String getNextMedia() {
        return "";
    }

    /**
     * Mark the nearest unwatched media as watched
     * @return return true if there's no more to watch for this media
     */
    public boolean next() {
        return false;
    }

    public abstract String getSQLTable();

    public abstract ContentValues getSQLValues();
}
