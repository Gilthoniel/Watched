package ch.watched.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import ch.watched.R;
import ch.watched.android.MovieActivity;
import ch.watched.android.adapters.MovieExpandableAdapter;
import ch.watched.android.constants.Constants;
import ch.watched.android.database.DatabaseService;

/**
 * Created by Gaylor on 20.09.2015.
 * List of all the movies pinned
 */
public class MoviesFragment extends HomeFragment {

    private MovieExpandableAdapter mAdapter;

    @Override
    public String getTitle() {
        return "Movies";
    }

    @Override
    public void reload() {
        if (getView() == null) {
            return;
        }

        mAdapter.clear();

        DatabaseService.getInstance().getMovies(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle states) {

        return inflater.inflate(R.layout.fragment_movies, parent, false);
    }

    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        if (getView() == null) {
            return;
        }

        mAdapter = new MovieExpandableAdapter();
        ExpandableListView list = (ExpandableListView) getView().findViewById(R.id.listview);
        list.setAdapter(mAdapter);
        list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int group, int position, long l) {

                Intent intent = new Intent(getContext(), MovieActivity.class);
                intent.putExtra(Constants.KEY_MEDIA_ID, mAdapter.getChild(group, position).getID());

                startActivity(intent);

                return true;
            }
        });
        list.expandGroup(0);

        reload();
    }
}
