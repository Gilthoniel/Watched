package ch.watched.android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.watched.R;
import ch.watched.android.models.TV;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Gaylor on 20.09.2015.
 *
 */
public class TvAdapter extends BaseAdapter {

    private List<TV> mMedias = new LinkedList<>();

    public void putAll(Collection<TV> collection) {
        mMedias.clear();
        mMedias.addAll(collection);

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMedias.size();
    }

    @Override
    public TV getItem(int i) {
        return mMedias.get(i);
    }

    @Override
    public long getItemId(int i) {
        return getItem(i).getID();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LinearLayout layout;
        if (view != null) {
            layout = (LinearLayout) view;
        } else {
            layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        }

        TV tv = mMedias.get(position);

        layout.findViewById(R.id.item_number).setVisibility(View.GONE);
        layout.findViewById(R.id.radio_watched).setVisibility(View.GONE);
        ((TextView) layout.findViewById(R.id.item_title)).setText(tv.getTitle());

        return layout;
    }
}
