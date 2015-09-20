package ch.watched.android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.watched.R;
import ch.watched.android.constants.Constants;
import ch.watched.android.fragments.HomeFragment;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gaylor on 09/19/2015.
 * Adapter for the menu of the home activity
 */
public class NavigationAdapter extends BaseAdapter {

    List<HomeFragment> mFragments;

    public NavigationAdapter() {
        mFragments = new LinkedList<>();

        for (Class<?> instance : Constants.HOME_FRAGMENTS) {
            try {
                mFragments.add((HomeFragment) instance.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public HomeFragment getItem(int i) {
        return mFragments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        LinearLayout layout;
        if (view != null) {
            layout = (LinearLayout) view;
        } else {
            layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_navigation, parent, false);
        }

        ((TextView) layout.findViewById(R.id.item_title)).setText(getItem(i).getTitle());

        return layout;
    }
}
