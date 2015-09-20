package ch.watched.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.watched.R;
import ch.watched.android.TvActivity;
import ch.watched.android.adapters.MediaCardAdapter;
import ch.watched.android.constants.Constants;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.models.Movie;
import ch.watched.android.models.TV;

/**
 * Created by gaylor on 09/19/2015.
 *
 */
public class ResumeFragment extends HomeFragment {

    private MediaCardAdapter<Movie> mMoviesAdapter;
    private MediaCardAdapter<TV> mSeriesAdapter;

    public String getTitle() {
        return "Selections";
    }

    @Override
    public void reload() {
        mMoviesAdapter.putAll(DatabaseService.getInstance().getUnwatchMovies());
        mSeriesAdapter.putAll(DatabaseService.getInstance().getUnwatchedTVs());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle states) {

        return inflater.inflate(R.layout.fragment_resume, parent, false);
    }

    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        if (getView() == null) {
            return;
        }

        RecyclerView movies = (RecyclerView) getView().findViewById(R.id.recycler_movies);
        movies.setHasFixedSize(true);

        LinearLayoutManager moviesManager = new LinearLayoutManager(getContext());
        moviesManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        movies.setLayoutManager(moviesManager);

        mMoviesAdapter = new MediaCardAdapter<>(DatabaseService.getInstance().getUnwatchMovies());
        movies.setAdapter(mMoviesAdapter);

        RecyclerView series = (RecyclerView) getView().findViewById(R.id.recycler_series);
        series.setHasFixedSize(true);

        LinearLayoutManager seriesManager = new LinearLayoutManager(getContext());
        seriesManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        series.setLayoutManager(seriesManager);

        mSeriesAdapter = new MediaCardAdapter<>(DatabaseService.getInstance().getUnwatchedTVs());
        mSeriesAdapter.setOnItemClickListener(new MediaCardAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getActivity(), TvActivity.class);
                intent.putExtra(Constants.KEY_MEDIA_ID, mSeriesAdapter.getItemId(position));

                startActivity(intent);
            }
        });
        series.setAdapter(mSeriesAdapter);
    }
}
