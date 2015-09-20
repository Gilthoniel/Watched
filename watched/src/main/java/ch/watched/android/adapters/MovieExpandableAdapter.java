package ch.watched.android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.watched.R;
import ch.watched.android.models.Movie;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Gaylor on 20.09.2015.
 * Adapter for the list of movie to separate watched/unwatched
 */
public class MovieExpandableAdapter extends BaseExpandableListAdapter {

    List<List<Movie>> mItems = new ArrayList<>();

    public MovieExpandableAdapter() {
        mItems.add(new LinkedList<Movie>());
        mItems.add(new LinkedList<Movie>());
    }

    public void addMovie(Movie movie) {
        mItems.get(movie.isWatched() ? 1 : 0).add(movie);
    }

    public void clear() {

        for (List<Movie> list : mItems) {
            list.clear();
        }

        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mItems.size();
    }

    @Override
    public int getChildrenCount(int group) {
        return mItems.get(group).size();
    }

    @Override
    public List<Movie> getGroup(int group) {
        return mItems.get(group);
    }

    @Override
    public Movie getChild(int group, int position) {
        return mItems.get(group).get(position);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int group, int position) {
        return getChild(group, position).getID();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int group, boolean isExpanded, View view, ViewGroup parent) {

        LinearLayout layout;
        if (view != null) {
            layout = (LinearLayout) view;
        } else {
            layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_group, parent, false);
        }

        ((TextView) layout.findViewById(R.id.group_title)).setText(group == 0 ? "Unwatched" : "Watched");

        return layout;
    }

    @Override
    public View getChildView(int group, int position, boolean isExpanded, View view, ViewGroup parent) {

        LinearLayout layout;
        if (view != null) {
            layout = (LinearLayout) view;
        } else {
            layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        }

        final Movie movie = getChild(group, position);

        layout.findViewById(R.id.item_number).setVisibility(View.GONE);
        ((TextView) layout.findViewById(R.id.item_title)).setText(movie.getTitle());
        final CheckBox checkbox = (CheckBox) layout.findViewById(R.id.radio_watched);
        checkbox.setChecked(movie.isWatched());
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movie.setWatched(checkbox.isChecked());

                if (checkbox.isChecked()) {
                    mItems.get(0).remove(movie);
                    mItems.get(1).add(movie);
                } else {
                    mItems.get(1).remove(movie);
                    mItems.get(0).add(movie);
                }

                notifyDataSetChanged();
            }
        });

        return layout;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
