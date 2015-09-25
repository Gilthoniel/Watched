package ch.watched.android.adapters;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ch.watched.R;
import ch.watched.android.constants.Constants;
import ch.watched.android.models.Media;
import ch.watched.android.service.ImageLoader;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Gaylor on 23.09.2015.
 *
 */
public class PagerCardAdapter<T extends Media> extends PagerAdapter {

    List<T> mMedias;
    SparseArray<View> mViews;
    Class<?> mClass;

    public PagerCardAdapter(List<T> medias, Class<?> activity) {
        super();

        mMedias = medias;
        mViews = new SparseArray<>();
        mClass = activity;
    }

    public void putAll(List<T> medias) {
        mMedias.clear();
        mMedias.addAll(medias);

        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

        Iterator<T> it = mMedias.iterator();
        int removalIndex = -1;
        for (int i = 0; i < mViews.size() && removalIndex < 0; i++) {
            final View view = mViews.get(i);
            if (it.hasNext()) {
                final Media media = it.next();
                if (view.getTag() != media) {
                    populateView(view, media, i);
                }
            } else {
                removalIndex = i;
            }
        }
    }

    @Override
    public int getItemPosition(Object object) {

        if (mMedias.contains(((View) object).getTag())) {
            return POSITION_UNCHANGED;
        } else {
            return POSITION_NONE;
        }
    }

    @Override
    public int getCount() {
        return mMedias.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {

        return view == o;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final Media media = mMedias.get(position);

        final View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_card_media, container, false);
        mViews.put(position, view);
        populateView(view, media, position);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private void populateView(View view, final Media media, final int position) {
        // Set tag for testing if the view is already filled with the good information
        view.setTag(media);

        ((TextView) view.findViewById(R.id.media_title)).setText(media.getTitle());
        ((TextView) view.findViewById(R.id.media_overview)).setText(media.getOverview());
        final TextView textNext = (TextView) view.findViewById(R.id.next_episode);
        textNext.setText(media.getNextMedia());
        ImageView poster = (ImageView) view.findViewById(R.id.media_poster);
        poster.setImageBitmap(null);
        ImageLoader.instance.get(media.getPoster(), poster);

        view.findViewById(R.id.button_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                if (media.next()) {
                    mMedias.remove(position);
                    notifyDataSetChanged();
                } else {
                    textNext.animate()
                            .alpha(0.0f)
                            .setDuration(500)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    textNext.setText(media.getNextMedia());

                                    textNext.animate()
                                            .alpha(1.0f)
                                            .setDuration(500)
                                            .start();
                                }
                            })
                            .start();
                }
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), mClass);
                intent.putExtra(Constants.KEY_MEDIA_ID, media.getID());

                view.getContext().startActivity(intent);
            }
        });
    }
}
