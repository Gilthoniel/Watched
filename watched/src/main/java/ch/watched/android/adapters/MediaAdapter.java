package ch.watched.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import ch.watched.android.database.DatabaseService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Gaylor on 30.06.2015.
 * Adapter to display a movie in a ListFragment
 */
public class MediaAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Media> medias;

    public MediaAdapter(Context context, List<Media> medias) {
        this.inflater = LayoutInflater.from(context);
        this.medias = medias;
    }

    public MediaAdapter(Context context) {
        this(context, new ArrayList<Media>());
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
        return medias.get(i).getID();
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        Media media = medias.get(i);

        RelativeLayout layout;
        if (view == null) {
            layout = (RelativeLayout) inflater.inflate(media.getLayoutID(), parent, false);
        } else {
            layout = (RelativeLayout) view;
        }

        return media.inflate(layout);
    }

    public void clear() {
        medias.clear();
        notifyDataSetInvalidated();
    }

    public void addAll(Collection<Media> medias) {
        this.medias.addAll(medias);
        notifyDataSetChanged();
    }
}
