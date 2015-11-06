package ch.watched.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import ch.watched.R;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.models.Media;
import ch.watched.android.service.ImageLoader;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by gaylor on 08/29/2015.
 *
 */
public class MediaSearchAdapter extends BaseAdapter {

    private List<Media> mMedias;
    private Context mContext;

    public MediaSearchAdapter(Context context) {
        mMedias = new LinkedList<>();

        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public <T extends Media> void addAll(Collection<T> collection) {
        mMedias.addAll(collection);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMedias.size();
    }

    @Override
    public Media getItem(int i) {
        return mMedias.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        RelativeLayout layout;
        if (view != null) {
            layout = (RelativeLayout) view;
        } else {
            layout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.item_grid_search, parent, false);
        }

        Media media = mMedias.get(position);

        RadioButton pinned = (RadioButton) layout.findViewById(R.id.radio_pinned);
        pinned.setChecked(media.exists());
        pinned.setText(media.getTitle());

        ImageView poster = (ImageView) layout.findViewById(R.id.media_poster);
        poster.setImageBitmap(null);
        ImageLoader.instance.get(media.getPoster(), poster);

        return layout;
    }
}
