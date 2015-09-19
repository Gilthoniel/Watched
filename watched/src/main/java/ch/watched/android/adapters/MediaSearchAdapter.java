package ch.watched.android.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ch.watched.R;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.models.Media;
import ch.watched.android.service.ImagesManager;
import ch.watched.android.service.utils.ImageObserver;

import java.util.*;

/**
 * Created by gaylor on 08/29/2015.
 *
 */
public class MediaSearchAdapter extends BaseAdapter implements ImageObserver {

    private List<Media> mMedias;
    private Map<String,Bitmap> mImages;
    private Context mContext;

    public MediaSearchAdapter(Context context) {
        mMedias = new LinkedList<>();
        mImages = new HashMap<>();

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
        pinned.setChecked(DatabaseService.getInstance().contains(media));
        pinned.setText(media.getTitle());

        ImageView poster = (ImageView) layout.findViewById(R.id.media_poster);
        if (mImages.containsKey(media.getPoster())) {
            poster.setImageBitmap(mImages.get(media.getPoster()));
        } else {
            mImages.put(media.getPoster(), null);
            ImagesManager.getInstance().get(media.getPoster(), this);
        }

        return layout;
    }

    @Override
    public void onImageLoaded(String url, Bitmap bitmap) {

        mImages.put(url, bitmap);
        notifyDataSetChanged();
    }
}
