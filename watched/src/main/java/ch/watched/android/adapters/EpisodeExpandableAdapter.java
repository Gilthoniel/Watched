package ch.watched.android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ch.watched.R;
import ch.watched.android.models.Episode;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by gaylor on 09/20/2015.
 * Adapter for the TV activity to display the list of episodes per season
 */
public class EpisodeExpandableAdapter extends BaseExpandableListAdapter {

    private Map<Integer, List<Episode>> mEpisodes;
    private List<Integer> mSeasons;

    public EpisodeExpandableAdapter(Map<Integer, List<Episode>> episodes) {
        mEpisodes = episodes;
        mSeasons = new LinkedList<>(mEpisodes.keySet());
        Collections.sort(mSeasons);
    }

    public boolean containsUnwatchedChild(int group) {

        for (Episode episode : mEpisodes.get(mSeasons.get(group))) {
            if (!episode.isWatched()) {
                return true;
            }
        }

        return false;
    }

    public List<Episode> getEpisodes() {
        List<Episode> episodes = new LinkedList<>();

        for (List<Episode> list : mEpisodes.values()) {
            episodes.addAll(list);
        }

        return episodes;
    }

    @Override
    public int getGroupCount() {
        return mSeasons.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mEpisodes.get(mSeasons.get(groupPosition)).size();
    }

    @Override
    public Integer getGroup(int position) {
        return mSeasons.get(position);
    }

    @Override
    public Episode getChild(int group, int position) {
        return mEpisodes.get(mSeasons.get(group)).get(position);
    }

    @Override
    public long getGroupId(int position) {
        return mSeasons.get(position);
    }

    @Override
    public long getChildId(int group, int position) {
        return getChild(group, position).getID();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int position, boolean isExpanded, View view, ViewGroup parent) {

        LinearLayout layout;
        if (view != null) {
            layout = (LinearLayout) view;
        } else {
            layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_group, parent, false);
        }

        ((TextView) layout.findViewById(R.id.group_title)).setText("Season " + getGroupId(position));

        return layout;
    }

    @Override
    public View getChildView(int group, int position, boolean isExpanded, View view, ViewGroup parent) {

        LinearLayout layout;
        if (view != null) {
            layout = (LinearLayout) view;
        } else {
            layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        }

        final Episode episode = mEpisodes.get(mSeasons.get(group)).get(position);

        ((TextView) layout.findViewById(R.id.item_number)).setText(String.valueOf(episode.getEpisodeNumber()));
        ((TextView) layout.findViewById(R.id.item_title)).setText(episode.getTitle());
        final CheckBox watched = (CheckBox) layout.findViewById(R.id.radio_watched);
        watched.setChecked(episode.isWatched());
        watched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                episode.setWatched(watched.isChecked());
            }
        });

        return layout;
    }

    @Override
    public boolean isChildSelectable(int group, int position) {
        return true;
    }
}
