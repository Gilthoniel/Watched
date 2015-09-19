package ch.watched.android;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import ch.watched.R;
import ch.watched.android.adapters.SearchPagerAdapter;
import ch.watched.android.views.SlidingTabLayout;

public class SearchActivity extends AppCompatActivity {

    private String mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ViewPager mPager = (ViewPager) findViewById(R.id.pager);
        SearchPagerAdapter mAdapter = new SearchPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);

        SlidingTabLayout mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true);
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        mTabs.setViewPager(mPager);

        Intent intent = getIntent();
        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
        }

        /* Toolbar */
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() == null) {
            return;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(mQuery);
    }

    public String getQuery() {
        return mQuery;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
