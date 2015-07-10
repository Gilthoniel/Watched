package ch.watched.android.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import ch.watched.android.R;
import ch.watched.android.adapters.TVAdapter;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.database.TVContract.TVEntry;
import ch.watched.android.models.TV;
import ch.watched.android.webservice.WebService;

public class ActivityTV extends AppCompatActivity {

    private TVAdapter mAdapter;
    private ExpandableListView mListView;
    private int mID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv);

        // Get the id of the TV
        Intent intent = getIntent();
        mID = intent.getIntExtra("TV_ID", -1);

        // Get the header view
        View header = getLayoutInflater().inflate(R.layout.header_tv, null);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView overview = (TextView) view.findViewById(R.id.overview);
                if (overview.getLineCount() > 1) {
                    overview.setSingleLine(true);
                } else {
                    overview.setSingleLine(false);
                }
            }
        });

        // Get the information
        Cursor cursor = DatabaseService.getInstance().read(TVEntry.TABLE_NAME, TV.PROJECTION, TVEntry.COLUMN_ID+"="+mID);
        cursor.moveToFirst();
        TV tv = new TV(cursor);

        // Populate widget
        ImageView image = (ImageView) header.findViewById(R.id.image);
        WebService.loadImage(tv.getImage(), image);

        TextView status = (TextView) header.findViewById(R.id.status);
        status.setText(tv.getStatus());

        RatingBar rating = (RatingBar) header.findViewById(R.id.rating);
        rating.setRating(tv.getScore());

        TextView overview = (TextView) header.findViewById(R.id.overview);
        overview.setText(tv.getOverview());

        Toolbar actions_watched = (Toolbar) findViewById(R.id.toolbar_actions);
        mListView = (ExpandableListView) findViewById(R.id.tv_listview);
        mListView.addHeaderView(header);
        mAdapter = new TVAdapter(getApplicationContext(), mID, actions_watched);

        mListView.setAdapter(mAdapter);
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView list, View parent, int group, int child, long id) {

                ImageView image = (ImageView) parent.findViewById(R.id.image);
                TextView overview = (TextView) parent.findViewById(R.id.overview);

                int state;
                if (image.getVisibility() != View.VISIBLE) {
                    state = View.VISIBLE;
                    mAdapter.loadImage(image, group, child);
                } else {
                    state = View.GONE;
                }

                image.setVisibility(state);
                overview.setVisibility(state);

                return true;
            }
        });

        // ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(tv.getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_tv, menu);

        // Active the switch button
        Switch button = (Switch) menu.findItem(R.id.switch_minimize).getActionView();
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                mAdapter.update(!checked);
            }
        });

        return true;
    }
}
