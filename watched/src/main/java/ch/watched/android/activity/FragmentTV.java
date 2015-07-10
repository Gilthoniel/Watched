package ch.watched.android.activity;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import ch.watched.android.R;
import ch.watched.android.adapters.Media;
import ch.watched.android.adapters.MediaAdapter;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.database.TVContract;
import ch.watched.android.models.TV;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gaylor on 07.07.2015.
 * Display the list of followed tv shows
 */
public class FragmentTV extends ListFragment {

    private MediaAdapter mAdapter;
    private Context mContext;
    private Toolbar mToolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_seen);
        mToolbar.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Save the mContext
        mContext = getActivity().getApplicationContext();

        mAdapter = new MediaAdapter(mContext);

        // Read all the entries
        List<Media> tvShows = new ArrayList<>();
        Cursor cursor = DatabaseService.getInstance().read(TVContract.TVEntry.TABLE_NAME, TV.PROJECTION, null);
        while (!cursor.isLast() && cursor.getCount() > 0) {
            cursor.moveToNext();

            TV show = new TV(cursor);
            tvShows.add(show);
        }

        mAdapter.addAll(tvShows);
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView list, View view, int position, long id) {

        Intent intent = new Intent(mContext, ActivityTV.class);
        intent.putExtra("TV_ID", (int) id);

        startActivity(intent);
    }
}
