package ch.watched.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import ch.watched.R;
import ch.watched.android.TvActivity;
import ch.watched.android.adapters.TvAdapter;
import ch.watched.android.constants.Constants;
import ch.watched.android.database.DatabaseService;

/**
 * Created by Gaylor on 20.09.2015.
 *
 */
public class TVsFragment extends HomeFragment {

    private TvAdapter mAdapter;

    @Override
    public String getTitle() {
        return "TV Shows";
    }

    @Override
    public void reload() {
        mAdapter.putAll(DatabaseService.getInstance().getTVs());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle states) {

        return inflater.inflate(R.layout.fragment_series, parent, false);
    }

    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        if (getView() == null) {
            return;
        }

        mAdapter = new TvAdapter();
        ListView list = (ListView) getView().findViewById(R.id.listview);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getContext(), TvActivity.class);
                intent.putExtra(Constants.KEY_MEDIA_ID, mAdapter.getItem(position).getID());

                startActivity(intent);
            }
        });

        reload();
    }
}
