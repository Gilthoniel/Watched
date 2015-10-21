package ch.watched.android.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import ch.watched.R;
import ch.watched.android.adapters.DiscoverCardAdapter;
import ch.watched.android.constants.Utils;
import ch.watched.android.models.SearchMovie;
import ch.watched.android.service.BaseWebService;
import ch.watched.android.service.utils.RequestCallback;
import ch.watched.android.views.RecyclerItemClickListener;

import java.lang.reflect.Type;

/**
 * Created by Gaylor on 20.10.2015.
 * Get the popular and last movies/TVs
 */
public class DiscoverFragment extends HomeFragment {

    private DiscoverCardAdapter mAdapter;
    private AlertDialog mDialog;
    private ProgressDialog mProgress;
    private SearchMovie mCurrent;

    @Override
    public String getTitle() {
        return "Discover";
    }

    @Override
    public void reload() {
        if (getView() == null) {
            return;
        }

        BaseWebService.instance.discover(new RequestCallback<SearchMovie.Wrapper>() {
            @Override
            public void onSuccess(SearchMovie.Wrapper result) {
                mAdapter.putAll(result.results);
            }

            @Override
            public void onFailure(Errors error) {
                Toast.makeText(getContext(), "An error occurred :(", Toast.LENGTH_SHORT).show();
            }

            @Override
            public Type getType() {
                return SearchMovie.Wrapper.class;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle states) {
        return inflater.inflate(R.layout.activity_discover, parent, false);
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
                mCurrent.insertIntoDatabase(new Runnable() {
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

        mAdapter = new DiscoverCardAdapter();
        recycler.setAdapter(mAdapter);

        reload();
    }
}
