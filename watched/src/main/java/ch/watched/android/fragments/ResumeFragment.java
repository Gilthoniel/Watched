package ch.watched.android.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import ch.watched.R;
import ch.watched.android.MovieActivity;
import ch.watched.android.TvActivity;
import ch.watched.android.adapters.PagerCardAdapter;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.models.Movie;
import ch.watched.android.models.TV;
import ch.watched.android.views.PaginationView;

/**
 * Created by gaylor on 09/19/2015.
 *
 */
public class ResumeFragment extends HomeFragment {

    private PagerCardAdapter<Movie> mMoviesAdapter;
    private PagerCardAdapter<TV> mSeriesAdapter;

    public String getTitle() {
        return "Selections";
    }

    @Override
    public void reload() {
        if (getView() == null) {
            return;
        }

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

        ViewPager movies = (ViewPager) getView().findViewById(R.id.pager_movies);
        mMoviesAdapter = new PagerCardAdapter<>(DatabaseService.getInstance().getUnwatchMovies(), MovieActivity.class);
        movies.setAdapter(mMoviesAdapter);

        PaginationView paginationMovies = (PaginationView) getView().findViewById(R.id.pagination_movies);
        paginationMovies.setViewPager(movies);

        ViewPager series = (ViewPager) getView().findViewById(R.id.pager_series);
        mSeriesAdapter = new PagerCardAdapter<>(DatabaseService.getInstance().getUnwatchedTVs(), TvActivity.class);
        series.setAdapter(mSeriesAdapter);

        PaginationView paginationSeries = (PaginationView) getView().findViewById(R.id.pagination_series);
        paginationSeries.setViewPager(series);

        reload();
    }
}
