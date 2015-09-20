package ch.watched.android.constants;

import ch.watched.android.fragments.MoviesFragment;
import ch.watched.android.fragments.ResumeFragment;
import ch.watched.android.fragments.TVsFragment;

/**
 * Created by gaylor on 08/29/2015.
 * Constants
 */
public class Constants {

    public static final int CACHE_EXPIRATION_TIME = 60 * 60 * 1000;
    public static String DB_KEY = "206f79eda0e7499536358bbfd3e47743";
    public static String DB_BASE_URL = "http://api.themoviedb.org/3/";

    public static Class<?>[] HOME_FRAGMENTS = new Class<?>[] {
            ResumeFragment.class,
            MoviesFragment.class,
            TVsFragment.class
    };

    /* INTENTS */

    public static final String KEY_INDEX = "ch.watched.android.KEY_INDEX";
    public static final String KEY_MEDIA_ID = "ch.watched.android.KEY_MEDIA_ID";
    public static final String KEY_SEARCH = "ch.watched.android.KEY_SEARCH";

}
