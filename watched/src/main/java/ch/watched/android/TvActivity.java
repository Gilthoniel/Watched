package ch.watched.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import ch.watched.R;
import ch.watched.android.adapters.EpisodeExpandableAdapter;
import ch.watched.android.constants.Constants;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.models.Backdrop;
import ch.watched.android.models.TV;
import ch.watched.android.service.ImageLoader;

public class TvActivity extends AppCompatActivity {

    private EpisodeExpandableAdapter mAdapter;
    private ExpandableListView mEpisodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv);

        Intent intent = getIntent();
        long id = intent.getLongExtra(Constants.KEY_MEDIA_ID, 0);

        TV tv = DatabaseService.getInstance().getTV(id);
        setTitle(tv.getTitle());

        ((TextView) findViewById(R.id.media_title)).setText(tv.getTitle());

        // Backdrops animation
        ViewFlipper flipper = (ViewFlipper) findViewById(R.id.flipper);
        flipper.setInAnimation(getApplicationContext(), R.anim.abc_fade_in);
        flipper.setOutAnimation(getApplicationContext(), R.anim.abc_fade_out);
        for (Backdrop backdrop : tv.getBackdrops()) {
            ImageView image = new ImageView(getApplicationContext());
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ViewFlipper.LayoutParams params = new ViewFlipper.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            image.setLayoutParams(params);

            ImageLoader.instance.get(backdrop.file_path, image, ImageLoader.ImageType.BACKDROP);

            flipper.addView(image);
        }

        // Episodes
        mEpisodes = (ExpandableListView) findViewById(R.id.list_episodes);
        mAdapter = new EpisodeExpandableAdapter(DatabaseService.getInstance().getEpisodes(tv.getID()));
        mEpisodes.setAdapter(mAdapter);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onPostCreate(Bundle states) {
        super.onPostCreate(states);

        for (int i = 0; i < mAdapter.getGroupCount(); i++) {
            if (mAdapter.containsUnwatchedChild(i)) {
                mEpisodes.expandGroup(i, true);
            } else {
                mEpisodes.collapseGroup(i);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tv, menu);
        return true;
    }
}
