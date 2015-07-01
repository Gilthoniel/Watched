package ch.watched.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import ch.watched.android.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Gaylor on 30.06.2015.
 * Adapter to display a movie in a ListFragment
 */
public class MediaAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<MediaInflater> medias;

    public MediaAdapter(Context context, List<MediaInflater> medias) {
        this.inflater = LayoutInflater.from(context);
        this.medias = medias;
    }

    public MediaAdapter(Context context) {
        this(context, new ArrayList<MediaInflater>());
    }

    @Override
    public int getCount() {
        return medias.size();
    }

    @Override
    public Object getItem(int i) {
        return medias.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        LinearLayout layout;
        if (view == null) {
            layout = (LinearLayout) inflater.inflate(R.layout.search_item_movie, parent, false);
        } else {
            layout = (LinearLayout) view;
        }

        MediaInflater media = medias.get(i);
        return media.inflate(layout);
    }

    public void clear() {
        medias.clear();
        notifyDataSetInvalidated();
    }

    public void addAll(Collection<MediaInflater> medias) {
        this.medias.addAll(medias);
        notifyDataSetChanged();
    }
}
