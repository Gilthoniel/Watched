package ch.watched.android.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ch.watched.android.R;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.models.Episode;
import ch.watched.android.webservice.WebService;

import java.util.*;

/**
 * Created by Gaylor on 08.07.2015.
 * Adapter to display the list of episodes
 */
public class TVAdapter extends BaseExpandableListAdapter {

    private Map<Integer, List<Episode>> mMapEpisodes;
    private List<Integer> mSeasons;
    private LayoutInflater inflater;
    private int mID;
    private Toolbar mToolbar;
    private Set<Long> selected;

    public TVAdapter(Context context, int id, Toolbar toolbar) {
        inflater = LayoutInflater.from(context);
        mID = id;
        selected = new HashSet<>();

        mToolbar = toolbar;
        mToolbar.inflateMenu(R.menu.menu_episodes_actions);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_seen:
                        updateEpisodes(true);
                        break;
                    case R.id.action_unseen:
                        updateEpisodes(false);
                        break;
                    default:
                        break;
                }

                return true;
            }
        });

        readData(true);
    }

    public void loadImage(ImageView image, int groupPos, int childPos) {
        WebService.loadImage(mMapEpisodes.get(mSeasons.get(groupPos)).get(childPos).getStillPath(), image);
    }

    public void update(boolean allValues) {
        readData(allValues);
    }

    @Override
    public int getGroupCount() {
        return mSeasons.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mMapEpisodes.get(mSeasons.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return mMapEpisodes.get(mSeasons.get(i));
    }

    @Override
    public Object getChild(int group, int child) {
        return mMapEpisodes.get(mSeasons.get(group)).get(child);
    }

    @Override
    public long getGroupId(int i) {
        return mSeasons.get(i);
    }

    @Override
    public long getChildId(int group, int child) {
        return mMapEpisodes.get(mSeasons.get(group)).get(child).getID();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int position, boolean isExpanded, View convert, ViewGroup parent) {

        if (convert == null) {
            convert = inflater.inflate(R.layout.list_group, parent, false);
        }

        TextView title = (TextView) convert.findViewById(R.id.groupTitle);
        title.setText("Season "+mSeasons.get(position));

        CheckBox checkbox = (CheckBox) convert.findViewById(R.id.select_box);
        checkbox.setChecked(selected.contains(getGroupId(position)));
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox button = (CheckBox) view;
                if (button.isChecked()) {
                    selected.add(getGroupId(position));
                    for (int i = 0; i < getChildrenCount(position); i++) {
                        selected.add(getChildId(position, i));
                    }

                    // Display the Toolbar
                    mToolbar.setVisibility(View.VISIBLE);
                } else {
                    selected.remove(getGroupId(position));
                    for (int i = 0; i < getChildrenCount(position); i++) {
                        selected.remove(getChildId(position, i));
                    }

                    // if there's no more selected items, we hide the toolbar
                    if (selected.size() == 0) {
                        mToolbar.setVisibility(View.GONE);
                    }
                }

                notifyDataSetChanged();
            }
        });

        return convert;
    }

    @Override
    public View getChildView(final int position, final int childPosition, boolean isLastChild, View convert, ViewGroup parent) {

        if (convert == null) {
            convert = inflater.inflate(R.layout.list_episode, parent, false);
        }

        final Episode episode = mMapEpisodes.get(mSeasons.get(position)).get(childPosition);

        View indicator = convert.findViewById(R.id.seen_indicator);
        if (episode.isWatched()) {
            indicator.setBackgroundColor(parent.getContext().getResources().getColor(R.color.media_seen));
        } else {
            indicator.setBackgroundColor(parent.getContext().getResources().getColor(R.color.media_unseen));
        }

        TextView number = (TextView) convert.findViewById(R.id.episode_number);
        number.setText(String.valueOf(episode.getEpisodeNumber()));

        TextView title = (TextView) convert.findViewById(R.id.episodeTitle);
        title.setText(episode.getName());

        ImageView image = (ImageView) convert.findViewById(R.id.image);
        image.setVisibility(View.GONE);

        TextView overview = (TextView) convert.findViewById(R.id.overview);
        overview.setText(episode.getOverview());
        overview.setVisibility(View.GONE);

        CheckBox checkbox = (CheckBox) convert.findViewById(R.id.episode_select);
        checkbox.setChecked(selected.contains(getChildId(position, childPosition)));
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox button = (CheckBox) view;
                if (button.isChecked()) {
                    selected.add(getChildId(position, childPosition));
                    mToolbar.setVisibility(View.VISIBLE);
                } else {
                    selected.remove(getChildId(position, childPosition));
                    if (selected.size() == 0) {
                        mToolbar.setVisibility(View.GONE);
                    }
                }
            }
        });

        return convert;
    }

    @Override
    public boolean isChildSelectable(int group, int child) {
        return true;
    }

    /** PRIVATE **/

    private void readData(boolean all) {
        mMapEpisodes = new TreeMap<>();

        // Load the episodes
        Cursor cursor = DatabaseService.getInstance().readTV(mID, all);
        while (cursor.getCount() > 0 && !cursor.isLast()) {
            cursor.moveToNext();
            Episode episode = new Episode(cursor);

            int key = episode.getSeasonNumber();
            if (!mMapEpisodes.containsKey(key)) {
                mMapEpisodes.put(key, new ArrayList<Episode>());
            }

            mMapEpisodes.get(key).add(episode);
        }

        mSeasons = new ArrayList<>(mMapEpisodes.keySet());
        notifyDataSetChanged();
    }

    private void updateEpisodes(boolean seen) {

        // Check each episode (because we can't access easily to an episode with only its ID
        for (List<Episode> list : mMapEpisodes.values()) {
            for (Episode episode : list) {

                if (selected.contains((long) episode.getID())) {
                    episode.setWatched(seen);
                }
            }
        }

        // Clear selected items
        selected.clear();
        notifyDataSetChanged();

        // Hide the toolbar
        mToolbar.setVisibility(View.GONE);
    }
}
