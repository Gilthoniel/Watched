package ch.watched.android.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gaylor on 08/29/2015.
 * TV Show season model
 */
public class Season implements Serializable {

    private static final long serialVersionUID = 3856582333013568570L;

    public List<Episode> episodes;
}
