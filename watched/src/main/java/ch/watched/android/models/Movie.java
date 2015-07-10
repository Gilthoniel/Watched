package ch.watched.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.view.View;
import android.widget.*;
import ch.watched.android.R;
import ch.watched.android.activity.SearchActivity;
import ch.watched.android.adapters.Media;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.database.MovieContract;
import ch.watched.android.database.MovieContract.MovieEntry;
import ch.watched.android.webservice.WebService;

import java.util.List;

/**
 * Created by Gaylor on 30.06.2015.
 * Movie data of the MovieDB
 */
public class Movie implements Media {

    public static final String[] PROJECTION = new String[] {
            MovieEntry.COLUMN_MOVIE_ID,
            MovieEntry.COLUMN_WATCHED,
            MovieEntry.COLUMN_IMAGE,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.COLUMN_RELEASE,
            MovieEntry.COLUMN_SCORE
    };

    private boolean adult;
    private String backdrop_path;
    private List<Integer> genre_ids;
    private int id;
    private String original_language;
    private String original_title;
    private String overview;
    private String release_date;
    private String poster_path;
    private float popularity;
    private String title;
    private float vote_average;
    private long vote_count;
    private MediaState state;

    public Movie(Cursor cursor) {
        id = cursor.getInt(0);

        if (cursor.getInt(1) == 1) {
            state = MediaState.WATCHED;
        } else {
            state = MediaState.PINNED;
        }

        poster_path = cursor.getString(2);
        title = cursor.getString(3);
        overview = cursor.getString(4);
        release_date = cursor.getString(5);
        vote_average = cursor.getFloat(6);
    }

    public void setState(MediaState value) {
        state = value;
        DatabaseService.getInstance().update(this, value == MediaState.WATCHED ? 1 : 0);
    }

    public MediaState getState() {
        return state;
    }

    public String getTitle() {
        if (title != null) {
            return title;
        } else if (original_title != null) {
            return original_title;
        } else {
            return "";
        }
    }

    public String getOriginalTitle() {
        return original_title;
    }

    public String getDescription() {
        return overview;
    }

    public String getImage() {
        return poster_path;
    }

    public float getVoteAverage() {
        return vote_average / 2;
    }

    public String getDate() {
        return release_date;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(MovieEntry.COLUMN_MOVIE_ID, id);
        values.put(MovieEntry.COLUMN_TITLE, title);
        values.put(MovieEntry.COLUMN_WATCHED, false);
        values.put(MovieEntry.COLUMN_OVERVIEW, overview);
        values.put(MovieEntry.COLUMN_IMAGE, poster_path);
        values.put(MovieEntry.COLUMN_RELEASE, release_date);
        values.put(MovieEntry.COLUMN_SCORE, vote_average);

        return values;
    }

    @Override
    public String getTableName() {
        return MovieEntry.TABLE_NAME;
    }

    @Override
    public String[] getColumnsProjection() {

        return PROJECTION;
    }

    @Override
    public int getLayoutID() {
        return R.layout.item_movie_search;
    }

    @Override
    public View inflate(RelativeLayout layout) {

        final DatabaseService db = DatabaseService.getInstance();

        TextView title = (TextView) layout.findViewById(R.id.search_item_title);
        title.setText(getTitle());

        TextView originalTitle = (TextView) layout.findViewById(R.id.search_item_otitle);
        originalTitle.setText(getOriginalTitle());

        RatingBar rating = (RatingBar) layout.findViewById(R.id.search_item_rating);
        rating.setRating(getVoteAverage());

        SeekBar seekBar = (SeekBar) layout.findViewById(R.id.search_item_seekbar);
        changeState(seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean fromUser) {
                if (fromUser) {
                    switch (position) {
                        case 1:
                            state = MediaState.PINNED;
                            if (db.read(Movie.this).getCount() > 0) {
                                db.update(Movie.this, 0); // unwatched it
                            } else {
                                db.insert(Movie.this);
                            }
                            SearchActivity.showToast("Mark as unwatched !");
                            break;
                        case 2:
                            state = MediaState.WATCHED;
                            db.update(Movie.this, 1);
                            SearchActivity.showToast("Mark as watched !");
                            break;
                        default:
                            state = MediaState.NONE;
                            db.delete(Movie.this);
                            SearchActivity.showToast("Remove from the list !");
                            break;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Nothing
            }
        });

        ImageView image = (ImageView) layout.findViewById(R.id.search_item_image);
        image.setImageResource(android.R.drawable.ic_menu_report_image);
        if (poster_path != null) {
            WebService.loadImage(poster_path, image);
        }

        return layout;
    }

    @Override
    public void updateState() {
        Cursor cursor = DatabaseService.getInstance().read(Movie.this);
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_WATCHED);
            boolean isWatched = cursor.getInt(index) == 1;

            if (isWatched) {
                state = Media.MediaState.WATCHED;
            } else {
                state = Media.MediaState.PINNED;
            }
        } else {
            state = Media.MediaState.NONE;
        }
    }

    /** PRIVATE **/

    private void changeState(SeekBar seekbar) {

        switch (state) {
            case PINNED:
                seekbar.setProgress(1);
                break;
            case WATCHED:
                seekbar.setProgress(2);
                break;
            default:
                seekbar.setProgress(0);
        }
    }
}
