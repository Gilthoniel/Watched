package ch.watched.android;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import ch.watched.R;
import ch.watched.android.adapters.NavigationAdapter;
import ch.watched.android.constants.Constants;
import ch.watched.android.fragments.HomeFragment;

public class HomeActivity extends AppCompatActivity {

    private MenuItem mItemSearchView;
    private ListView mDrawerList;
    private NavigationAdapter mAdapter;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mCurrentIndex;

    @Override
    protected void onCreate(Bundle states) {
        super.onCreate(states);
        setContentView(R.layout.activity_home);

        if (states != null) {
            mCurrentIndex = states.getInt(Constants.KEY_INDEX);
        } else {
            mCurrentIndex = 0;
        }

        /* Navigation Drawer */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mAdapter = new NavigationAdapter();

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.addHeaderView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.drawer_header, mDrawerList, false));
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        /* Toolbar */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                0,
                0
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();

        mDrawerList.performItemClick(null, mCurrentIndex + 1, 0);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    /**
     * Call when the activity is back on foreground
     */
    @Override
    public void onResume() {
        super.onResume();

        // Collapse the search view when we go back to the home activity
        if (mItemSearchView != null) {
            mItemSearchView.collapseActionView();
        }

        mAdapter.getItem(mCurrentIndex).reload();
    }

    @Override
    public void onSaveInstanceState(Bundle states) {
        states.putInt(Constants.KEY_INDEX, mCurrentIndex);

        super.onSaveInstanceState(states);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);

        // initiate the search view
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mItemSearchView = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) mItemSearchView.getActionView();
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setSubmitButtonEnabled(true);
            searchView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

            ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT);
            searchView.setLayoutParams(params);
            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);
            searchView.setMaxWidth(size.x);
        } else {
            Log.e("--NULL--", "SearchView is null");
        }

        return super.onCreateOptionsMenu(menu);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            mCurrentIndex = i - 1;
            HomeFragment fragment = mAdapter.getItem(mCurrentIndex);
            fragment.reload();

            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();

            setTitle(fragment.getTitle());
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }
}
