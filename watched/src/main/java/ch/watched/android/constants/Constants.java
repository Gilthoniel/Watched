package ch.watched.android.constants;

import ch.watched.android.fragments.ResumeFragment;

/**
 * Created by gaylor on 08/29/2015.
 * Constants
 */
public class Constants {

    public static final int CACHE_EXPIRATION_TIME = 60 * 60 * 1000;
    public static String DB_KEY = "206f79eda0e7499536358bbfd3e47743";
    public static String DB_BASE_URL = "http://api.themoviedb.org/3/";

    public static Class<?>[] HOME_FRAGMENTS = new Class<?>[] {
            ResumeFragment.class
    };
}
