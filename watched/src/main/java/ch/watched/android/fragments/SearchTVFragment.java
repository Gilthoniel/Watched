package ch.watched.android.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import ch.watched.R;
import ch.watched.android.SearchActivity;
import ch.watched.android.adapters.MediaSearchAdapter;
import ch.watched.android.dialogs.SearchDialog;
import ch.watched.android.service.BaseWebService;

/**
 * Created by gaylor on 08/29/2015.
 * Fragment to display result of a search for tv shows
 */
public class SearchTVFragment extends Fragment implements DialogInterface.OnDismissListener{

    private SearchDialog mDialog = new SearchDialog();
    private MediaSearchAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle states) {

        return inflater.inflate(R.layout.fragment_search, parent, false);
    }

    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        if (getView() == null) {
            return;
        }

        mAdapter = new MediaSearchAdapter(getActivity());
        mDialog.setAdapter(mAdapter);

        GridView grid = (GridView) getView().findViewById(R.id.gridView);
        grid.setAdapter(mAdapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                mDialog.setMedia(mAdapter.getItem(position));

                mDialog.show(getFragmentManager(), "media_dialog");
            }
        });

        // Perform the search
        SearchActivity activity = (SearchActivity) getActivity();
        BaseWebService.instance.searchTV(activity.getQuery(), mAdapter);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mAdapter.notifyDataSetChanged();
    }
}
