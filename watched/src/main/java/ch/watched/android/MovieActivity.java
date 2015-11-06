package ch.watched.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.*;
import ch.watched.R;
import ch.watched.android.constants.Constants;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.models.Backdrop;
import ch.watched.android.models.Movie;
import ch.watched.android.service.BaseWebService;
import ch.watched.android.service.GenreManager;
import ch.watched.android.service.ImageLoader;
import ch.watched.android.service.utils.RequestCallback;
import ch.watched.android.service.utils.SimpleRequestCallback;

import java.lang.reflect.Type;


public class MovieActivity extends AppCompatActivity {

    private long mID;
    private Movie mMovie;
    private RadioButton mWatchedButton;

    @Override
    protected void onCreate(Bundle states) {
        super.onCreate(states);
        setContentView(R.layout.activity_movie);

        setTitle("");

        Intent intent = getIntent();
        if (intent != null) {
            mID = intent.getLongExtra(Constants.KEY_MEDIA_ID, 0);
        } else if (states != null) {
            mID = states.getLong(Constants.KEY_MEDIA_ID);
        } else {
            mID = 0;
        }

        mMovie = DatabaseService.getInstance().getMovie(mID);

        ((TextView) findViewById(R.id.media_title)).setText(mMovie.getTitle());
        GenreManager.instance().populate(mMovie.getGenres(), (TextView) findViewById(R.id.media_genres));
        ((TextView) findViewById(R.id.media_overview)).setText(mMovie.getOverview());
        ((RatingBar) findViewById(R.id.media_rating)).setRating(mMovie.getRating());
        ((TextView) findViewById(R.id.media_date)).setText(mMovie.getDate());
        mWatchedButton = (RadioButton) findViewById(R.id.media_watched);
        checkIcon();

        // Flipper
        ViewFlipper flipper = (ViewFlipper) findViewById(R.id.flipper);
        flipper.setInAnimation(getApplicationContext(), R.anim.abc_fade_in);
        flipper.setOutAnimation(getApplicationContext(), R.anim.abc_fade_out);

        for (Backdrop backdrop : mMovie.getBackdrops().size() > 5 ? mMovie.getBackdrops().subList(0, 5) : mMovie.getBackdrops()) {
            ImageView image = new ImageView(getApplicationContext());
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ViewFlipper.LayoutParams params = new ViewFlipper.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            image.setLayoutParams(params);

            ImageLoader.instance.get(backdrop.file_path, image, ImageLoader.ImageType.BACKDROP);

            flipper.addView(image);
        }

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        ImageLoader.instance.cancel();
    }

    @Override
    public void onSaveInstanceState(Bundle states) {
        super.onSaveInstanceState(states);

        states.putLong(Constants.KEY_MEDIA_ID, mID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_remove:

                mMovie.remove(null);
                finish();
                return true;

            case R.id.action_seen:

                mMovie.setWatched(!mMovie.isWatched());
                mMovie.update(null);
                checkIcon();
                return true;

            case R.id.action_refresh:
                BaseWebService.instance.getMovie(mMovie.getID(), new SimpleRequestCallback<Movie>(Movie.class) {
                    @Override
                    public void onSuccess(Movie result) {
                        result.update(new Runnable() {
                            @Override
                            public void run() {
                                recreate();
                                Toast.makeText(getApplicationContext(), "Movie updated!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkIcon() {
        if (mMovie != null) {
            mWatchedButton.setChecked(mMovie.isWatched());
        }
    }
}
