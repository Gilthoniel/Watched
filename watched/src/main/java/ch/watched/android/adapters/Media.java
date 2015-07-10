package ch.watched.android.adapters;

import android.content.ContentValues;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import ch.watched.android.database.DatabaseService;

/**
 * Created by Gaylor on 01.07.2015.
 * Fill a view with media information
 */
public interface Media {
    enum MediaState { NONE, PINNED, WATCHED }

    /**
     * Get the layout of the media
     * @param layout Where we find all the views
     * @return
     */
    View inflate(RelativeLayout layout);

    /**
     * Get the values for the DatabaseService
     * @return
     */
    ContentValues getContentValues();

    /**
     * Get the name of the table in the database
     * @return
     */
    String getTableName();

    String[] getColumnsProjection();

    /**
     * Get the id of the media
     * @return
     */
    int getID();

    /**
     * True if the media is already WATCHED, false for PINNED
     * @return
     */
    void updateState();

    int getLayoutID();
}
