package ch.watched.android.adapters;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import ch.watched.R;
import ch.watched.android.constants.Constants;
import ch.watched.android.models.Media;
import ch.watched.android.service.ImageLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Gaylor on 23.09.2015.
 *
 */
public class PagerCardAdapter<T extends Media> extends PagerAdapter {

    List<T> mMedias;
    Set<SeekBar> mSeekBars;
    Class<?> mClass;

    public PagerCardAdapter(List<T> medias, Class<?> activity) {
        mMedias = medias;
        mSeekBars = new HashSet<>();
        mClass = activity;
    }

    public void putAll(List<T> medias) {
        mMedias.clear();
        mMedias.addAll(medias);

        notifyDataSetChanged();
    }

    public void addSeekBar(SeekBar seekBar) {
        mSeekBars.add(seekBar);

        seekBar.setMax(mMedias.size() - 1);

        if (mMedias.size() <= 0) {
            seekBar.setVisibility(View.GONE);
        }
    }

    public void updateSeekBars(int position) {
        for (SeekBar seekBar : mSeekBars) {
            seekBar.setProgress(position);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

        for (SeekBar seekBar : mSeekBars) {
            seekBar.setMax(mMedias.size() - 1);

            if (mMedias.size() <= 0) {
                seekBar.setVisibility(View.GONE);
            }
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

        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_card_media, container, false);
        ((TextView) view.findViewById(R.id.media_title)).setText(media.getTitle());
        ((TextView) view.findViewById(R.id.media_overview)).setText(media.getOverview());
        final TextView textNext = (TextView) view.findViewById(R.id.next_episode);
        textNext.setText(media.getNextMedia());
        ImageView poster = (ImageView) view.findViewById(R.id.media_poster);
        poster.setImageBitmap(null);
        ImageLoader.instance.get(media.getPoster(), poster);

        view.findViewById(R.id.button_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        view.setTag(media);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(container.getContext(), mClass);
                intent.putExtra(Constants.KEY_MEDIA_ID, media.getID());

                container.getContext().startActivity(intent);
            }
        });

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
