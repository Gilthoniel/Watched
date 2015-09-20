package ch.watched.android.models;

import java.io.Serializable;

/**
 * Created by gaylor on 09/20/2015.
 * Genre of medias
 */
public class Genre implements Serializable {
    private static final long serialVersionUID = 2854131969755383112L;

    public long id;
    public String name;

    @Override
    public String toString() {
        return name;
    }
}
