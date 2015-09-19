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

    public abstract String getSQLTable();

    public abstract ContentValues getSQLValues();
}
