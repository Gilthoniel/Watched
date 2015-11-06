package ch.watched.android.models;

/**
 * Created by gaylor on 08/29/2015.
 *
 */
public interface Media extends DatabaseItem {

    long getID();
    String getPoster();
    String getTitle();
    float getRating();
    String getOverview();
    String getDate();
    long[] getGenres();

    void setWatched(boolean isWatched);
}
