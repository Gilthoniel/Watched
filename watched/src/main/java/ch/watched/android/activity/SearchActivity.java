package ch.watched.android.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.ListView;
import android.widget.Toast;
import ch.watched.android.R;
import ch.watched.android.adapters.MediaAdapter;
import ch.watched.android.adapters.Media;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.webservice.WebService;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends AppCompatActivity {

    private static Toast toast;

    public static void showToast(CharSequence text) {
        if (toast != null) {
            toast.setText(text);
            toast.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Toast
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);

        SearchAdapter adapter = new SearchAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager) findViewById(R.id.search_view_pager);
        pager.setAdapter(adapter);
        pager.setCurrentItem(0);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(pager);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the intent with the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i("-- Query gotten --", "Data: " + query);
            adapter.search(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Manage the fragments in the pager
     */
    private static class SearchAdapter extends FragmentPagerAdapter {

        private static final int NUM_ITEMS = 2;
        private String tabTitles[] = new String[] { "Movies", "TV Shows" };
        private List<SearchFragment> fragments;

        public SearchAdapter(FragmentManager manager) {
            super(manager);

            // Get the old fragments if they exist
            fragments = new ArrayList<>();
            if (manager.getFragments() != null) {
                for (Fragment fragment : manager.getFragments()) {
                    fragments.add((SearchFragment) fragment);
                }
            }
            // Complete to have the good number of fragments
            while (fragments.size() < 2) {
                fragments.add(new SearchFragment());
            }
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        /**
         * Execute a research with the param
         * @param query param of the research
         */
        public void search(String query) {

            WebService.search("movie", query, fragments.get(0));
            WebService.search("tv", query, fragments.get(1));
        }
    }

    /**
     * Fragment in the pager of the results page
     * where you find the results of a search on the main activity
     */
    public static class SearchFragment extends ListFragment {

        private List<Media> medias;
        private MediaAdapter adapter;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            return inflater.inflate(R.layout.fragment_pager, container, false);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            adapter = new MediaAdapter(getActivity());
            setListAdapter(adapter);
            // The request can be slower than the creation of the fragment
            if (medias != null) {
                adapter.clear();
                adapter.addAll(medias);
            }
        }

        /**
         * When the user click on an item of the list
         * @param list
         * @param view
         * @param position
         * @param id
         */
        @Override
        public void onListItemClick(ListView list, View view, int position, long id) {

            Log.i("-- Click --", "click on item "+position);
        }

        /**
         * Set the list of medias and reload the adapter
         * @param values the list of medias
         */
        public void setMedias(List<Media> values) {
            medias = values;

            // Check if we already have seen those medias
            for (Media media : medias) {
                media.updateState();
            }

            if (adapter != null) {
                adapter.clear();
                adapter.addAll(medias);
            }
        }
    }
}
