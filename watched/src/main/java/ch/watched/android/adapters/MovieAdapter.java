package ch.watched.android.adapters;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ch.watched.android.R;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.models.Movie;
import ch.watched.android.webservice.WebService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Gaylor on 03.07.2015.
 * Adapter to fill the list of the movies pinned for watching
 */
public class MovieAdapter extends BaseAdapter {

    private List<Movie> movies;
    private LayoutInflater inflater;
    private Set<Integer> selected;
    private Toolbar mToolbar;

    public MovieAdapter(Context context, Toolbar toolbar) {
        movies = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        selected = new HashSet<>();

        mToolbar = toolbar;
        mToolbar.inflateMenu(R.menu.menu_movies);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        removeMovies();
                        break;
                    case R.id.action_seen:
                        updateMovies(true);
                        break;
                    case R.id.action_unseen:
                        updateMovies(false);
                        break;
                    default:
                        break;
                }

                mToolbar.setVisibility(View.GONE);
                selected.clear();
                notifyDataSetChanged();

                return true;
            }
        });
    }

    public void addMovies(List<Movie> movies) {
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

    public void loadImage(int position, ImageView image) {
        Movie movie = movies.get(position);

        if (movie.getImage() != null) {
            WebService.loadImage(movie.getImage(), image);
        }
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        RelativeLayout layout;
        if (view == null) {
            layout = (RelativeLayout) inflater.inflate(R.layout.item_movie_list, parent, false);
        } else {
            layout = (RelativeLayout) view;
        }

        final Movie movie = movies.get(position);

        View indicator = layout.findViewById(R.id.indicator);
        if (movie.getState() != Media.MediaState.WATCHED) {
            indicator.setBackgroundColor(layout.getContext().getResources().getColor(R.color.media_unseen));
        } else {
            indicator.setBackgroundColor(layout.getContext().getResources().getColor(R.color.media_seen));
        }

        TextView title = (TextView) layout.findViewById(R.id.item_movie_title);
        title.setText(movie.getTitle());

        TextView overview = (TextView) layout.findViewById(R.id.item_movie_overview);
        overview.setText(movie.getDescription());

        RatingBar rating = (RatingBar) layout.findViewById(R.id.item_movie_rating);
        rating.setRating(movie.getVoteAverage());

        TextView release = (TextView) layout.findViewById(R.id.release_date);
        release.setText(movie.getDate());

        CheckBox checkbox = (CheckBox) layout.findViewById(R.id.checkbox_actions);
        checkbox.setChecked(selected.contains(position));
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox button = (CheckBox) view;
                if (button.isChecked()) {
                    selected.add(position);
                    mToolbar.setVisibility(View.VISIBLE);
                } else {
                    selected.remove(position);
                    if (selected.size() == 0) {
                        mToolbar.setVisibility(View.GONE);
                    }
                }
            }
        });

        return layout;
    }

    /** PRIVATE **/

    private void updateMovies(boolean seen) {

        for (Integer position : selected) {
            Movie movie = movies.get(position);
            if (seen) {
                movie.setState(Media.MediaState.WATCHED);
            } else {
                movie.setState(Media.MediaState.PINNED);
            }
        }
    }

    private void removeMovies() {
        for (int position : selected) {
            Movie movie = movies.get(position);
            DatabaseService.getInstance().delete(movie);
            movies.remove(position);
        }
    }
}
