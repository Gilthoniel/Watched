package ch.watched.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ch.watched.R;
import ch.watched.android.adapters.EpisodeExpandableAdapter;
import ch.watched.android.constants.Constants;
import ch.watched.android.constants.Utils;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.dialogs.EpisodeDialog;
import ch.watched.android.models.Backdrop;
import ch.watched.android.models.Episode;
import ch.watched.android.models.TV;
import ch.watched.android.service.BaseWebService;
import ch.watched.android.service.ConnectionService;
import ch.watched.android.service.ImageLoader;

import java.util.List;

public class TvActivity extends AppCompatActivity {

    private EpisodeExpandableAdapter mAdapter;
    private ExpandableListView mEpisodes;
    private ProgressDialog mDialog;
    private EpisodeDialog mEpisodeDialog = new EpisodeDialog();
    private long mID;
    private TV mTV;

    @Override
    protected void onCreate(Bundle states) {
        super.onCreate(states);
        setContentView(R.layout.activity_tv);

        Intent intent = getIntent();
        if (intent != null) {
            mID = intent.getLongExtra(Constants.KEY_MEDIA_ID, 0);
        } else if (states != null) {
            mID = states.getLong(Constants.KEY_MEDIA_ID);
        } else {
            mID = 0;
        }

        mTV = DatabaseService.getInstance().getTV(mID);
        setTitle("");

        ((TextView) findViewById(R.id.media_title)).setText(mTV.getTitle());
        ((TextView) findViewById(R.id.media_date)).setText(mTV.getDate());

        // Backdrops animation
        ViewFlipper flipper = (ViewFlipper) findViewById(R.id.flipper);
        flipper.setInAnimation(getApplicationContext(), R.anim.abc_fade_in);
        flipper.setOutAnimation(getApplicationContext(), R.anim.abc_fade_out);
        for (Backdrop backdrop : mTV.getBackdrops().size() > 5 ? mTV.getBackdrops().subList(0, 5) : mTV.getBackdrops()) {
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
        mEpisodes.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int group, int child, long l) {

                mEpisodeDialog.setEpisode(mAdapter.getChild(group, child));
                mEpisodeDialog.show(getSupportFragmentManager(), "episode_dialog");

                return true;
            }
        });
        mEpisodes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

                int itemType = ExpandableListView.getPackedPositionType(id);
                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    int group = ExpandableListView.getPackedPositionGroup(id) - 1;

                    boolean watched = mAdapter.containsUnwatchedChild(group);
                    for (int i = 0; i < mAdapter.getChildrenCount(group); i++) {
                        mAdapter.getChild(group, i).setWatched(watched);
                    }

                    mAdapter.notifyDataSetChanged();
                }

                return true;
            }
        });
        mAdapter = new EpisodeExpandableAdapter(DatabaseService.getInstance().getEpisodes(mTV.getID()));
        mEpisodes.setAdapter(mAdapter);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mDialog = Utils.createProgressDialog(TvActivity.this);
        mDialog.setMessage("Episodes are marking as watched");
    }

    @Override
    public void onPause() {
        super.onPause();

        ImageLoader.instance.cancel();
    }

    @Override
    public void onPostCreate(Bundle states) {
        super.onPostCreate(states);

        boolean alreadyExpand = false;
        for (int i = 0; i < mAdapter.getGroupCount(); i++) {
            if (!alreadyExpand && mAdapter.containsUnwatchedChild(i)) {
                mEpisodes.expandGroup(i, true);
                alreadyExpand = true;
            } else {
                mEpisodes.collapseGroup(i);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle states) {
        super.onSaveInstanceState(states);

        states.putLong(Constants.KEY_MEDIA_ID, mID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tv, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_remove:

                DatabaseService.getInstance().remove(mTV);
                finish();
                return true;

            case R.id.action_refresh:

                BaseWebService.instance.updateTV(mTV, new BaseWebService.Callable() {
                    @Override
                    public void call() {
                        recreate();
                        Toast.makeText(getApplicationContext(), "TV Show updated!", Toast.LENGTH_SHORT).show();
                    }
                });
                return true;

            case R.id.action_watched:

                markAllEpisodes();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void markAllEpisodes() {

        // Progress dialog
        mDialog.show();

        new ActionTask().executeOnExecutor(ConnectionService.instance.getExecutor());
    }

    private class ActionTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            List<Episode> episodes = mAdapter.getEpisodes();
            for (Episode episode : episodes) {
                episode.setWatched(true);
            }

            return null;
        }

        @Override
        public void onPostExecute(Void aVoid) {
            mAdapter.notifyDataSetChanged();
            mDialog.dismiss();
        }
    }
}
