package ch.watched.android.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import ch.watched.android.fragments.SearchMovieFragment;
import ch.watched.android.fragments.SearchTVFragment;

/**
 * Created by gaylor on 08/29/2015.
 * Pager adapter for the search activity
 */
public class SearchPagerAdapter extends FragmentPagerAdapter {

    private static final String[] TITLES = new String[]{
            "Movies",
            "TV Shows"
    };

    public SearchPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new SearchMovieFragment();
            default:
                return new SearchTVFragment();
        }
    }

    @Override
    public String getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }
}
