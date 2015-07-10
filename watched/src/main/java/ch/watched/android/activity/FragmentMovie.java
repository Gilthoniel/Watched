package ch.watched.android.activity;

import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ch.watched.android.R;
import ch.watched.android.adapters.Media;
import ch.watched.android.adapters.MovieAdapter;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.database.MovieContract;
import ch.watched.android.models.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gaylor on 03.07.2015.
 * Display the list of followed movies
 */
public class FragmentMovie extends ListFragment {

    private MovieAdapter mAdapter;
    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceStates) {

        super.onCreate(savedInstanceStates);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_seen);
        mToolbar.setTitle("Choose");
        mToolbar.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new MovieAdapter(getActivity().getApplicationContext(), mToolbar);

        // Read all the entries
        List<Movie> movies = new ArrayList<>();
        Cursor cursor = DatabaseService.getInstance().read(MovieContract.MovieEntry.TABLE_NAME, Movie.PROJECTION, null);
        while (!cursor.isLast() && cursor.getCount() > 0) {
            cursor.moveToNext();

            Movie movie = new Movie(cursor);
            movies.add(movie);
        }

        mAdapter.addMovies(movies);
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView list, View view, int position, long id) {

        RelativeLayout box = (RelativeLayout) view.findViewById(R.id.layout_hidden);

        if (box.getVisibility() != View.GONE) {
            box.setVisibility(View.GONE);
        } else {
            mAdapter.loadImage(position, (ImageView) view.findViewById(R.id.item_movie_image));

            box.setVisibility(View.VISIBLE);
        }
    }
}
