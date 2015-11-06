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
public class PagerCardAdapter<U extends Media, T extends Media & Iterable<U>> extends PagerAdapter {

    List<T> mMedias;
    SparseArray<View> mViews;
    Class<?> mClass; // Activity opened when a click occurred

    public PagerCardAdapter(List<T> medias, Class<?> activity) {
        super();

        mMedias = medias;
        mViews = new SparseArray<>();
        mClass = activity;
    }

    public void putAll(List<T> medias) {
        mMedias.clear();
        mViews.clear();
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
                final T media = it.next();
                if (view != null && view.getTag() != media) {
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
        final T media = mMedias.get(position);

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

    private void populateView(View view, final T media, final int position) {
        // Set tag for testing if the view is already filled with the good information
        view.setTag(media);

        ((TextView) view.findViewById(R.id.media_title)).setText(media.getTitle());
        ((TextView) view.findViewById(R.id.media_overview)).setText(media.getOverview());

        Iterator<U> it = media.iterator();
        view.findViewById(R.id.button_next).setOnClickListener(new NextOnClickListener(media, view));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), mClass);
                intent.putExtra(Constants.KEY_MEDIA_ID, media.getID());

                view.getContext().startActivity(intent);
            }
        });

        ImageView poster = (ImageView) view.findViewById(R.id.media_poster);
        poster.setImageBitmap(null);
        ImageLoader.instance.get(media.getPoster(), poster);
    }

    /**
     * Action when the user click on the validate button to mark a media as watched and get the next or remove from
     * the unwatched selection
     */
    private class NextOnClickListener implements View.OnClickListener {

        private Iterator<U> mIterator;
        private T mMedia;
        private View mView;
        private TextView mTextNext;
        private U mOld;

        public NextOnClickListener(T media, View parent) {
            mMedia = media;
            mIterator = media.iterator();
            mView = parent;
            mTextNext = (TextView) mView.findViewById(R.id.next_episode);

            if (mIterator.hasNext()) {
                mOld = mIterator.next();
                mTextNext.setText(mOld.toString());
            }
        }

        @Override
        public void onClick(View button) {
            // Mark as watched
            if (mOld != null) {
                mOld.setWatched(true);
            }

            if (mIterator.hasNext()) {
                final U next = mIterator.next();

                // Update text
                mTextNext.animate()
                        .alpha(0.0f)
                        .setDuration(100)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                mTextNext.setText(next.toString());

                                mTextNext.animate()
                                        .alpha(1.0f)
                                        .setDuration(100)
                                        .start();
                            }
                        })
                        .start();

                mOld = next;
            } else {
                mMedias.remove(mMedia);
                notifyDataSetChanged();
            }
        }
    }
}
