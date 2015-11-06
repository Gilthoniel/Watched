package ch.watched.android.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.*;
import ch.watched.R;
import ch.watched.android.SettingsDiscoverActivity;
import ch.watched.android.adapters.DiscoverCardAdapter;
import ch.watched.android.constants.Utils;
import ch.watched.android.models.Media;
import ch.watched.android.service.BaseWebService;
import ch.watched.android.views.RecyclerItemClickListener;

/**
 * Created by Gaylor on 20.10.2015.
 * Get the popular and last movies/TVs
 */
public class DiscoverFragment extends HomeFragment {

    private DiscoverCardAdapter mAdapter;
    private AlertDialog mDialog;
    private ProgressDialog mProgress;
    private Media mCurrent;

    @Override
    public String getTitle() {
        return "Discover";
    }

    @Override
    public void reload() {
        if (getView() == null || getContext() == null) {
            return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String type = prefs.getString("pref_discover_type", "movie");
        String sort = prefs.getString("pref_discover_sort", "popularity.desc");
        int date_ge = prefs.getInt("pref_discover_date_ge", 1900);

        BaseWebService.instance.discover(type, sort, date_ge, mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle states) {
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_discover, parent, false);
    }

    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        if (getView() == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Pin to watch later ?");
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                mProgress.show();
                mCurrent.insert(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        mProgress.dismiss();
                    }
                });
            }
        });
        mDialog = builder.create();
        mProgress = Utils.createProgressDialog(getContext());

        RecyclerView recycler = (RecyclerView) getView().findViewById(R.id.recycler_view);
        recycler.setHasFixedSize(true);
        recycler.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(final View view, int position) {

                mCurrent = mAdapter.getItem(position);
                view.findViewById(R.id.media_poster).animate()
                        .scaleX(0.8f)
                        .scaleY(0.8f)
                        .setDuration(100)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                view.findViewById(R.id.media_poster).animate()
                                        .scaleX(1.0f)
                                        .scaleY(1.0f)
                                        .setDuration(100)
                                        .withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                mDialog.show();
                                            }
                                        })
                                        .start();
                            }
                        })
                        .start();
            }
        }));

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recycler.setLayoutManager(layoutManager);

        mAdapter = new DiscoverCardAdapter(getContext());
        recycler.setAdapter(mAdapter);

        reload();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_home_discover, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_filters && getView() != null) {
            Intent intent = new Intent(getContext(), SettingsDiscoverActivity.class);
            startActivity(intent);
        }

        return false;
    }
}
