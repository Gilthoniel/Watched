package ch.watched.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import ch.watched.R;
import ch.watched.android.models.Media;
import ch.watched.android.service.GenreManager;
import ch.watched.android.service.ImageLoader;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Gaylor on 20.10.2015.
 *
 */
public class DiscoverCardAdapter extends RecyclerView.Adapter<DiscoverCardAdapter.ViewHolder> {

    private List<Media> mMedias;
    private Context mContext;

    public DiscoverCardAdapter(Context context) {
        mMedias = new LinkedList<>();
        mContext = context;
    }

    public void putAll(Collection<? extends Media> collection) {
        mMedias.clear();
        mMedias.addAll(collection);
        notifyDataSetChanged();
    }

    public void addAll(Collection<? extends Media> collection) {
        mMedias.addAll(collection);
        notifyDataSetChanged();
    }

    public Media getItem(int position) {
        return mMedias.get(position);
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_discover, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Media movie = mMedias.get(position);

        holder.image.setImageBitmap(null);
        ImageLoader.instance.get(movie.getPoster(), holder.image, ImageLoader.ImageType.POSTER);
        holder.title.setText(movie.getTitle());
        holder.date.setText(movie.getDate());
        holder.description.setText(movie.getOverview());
        GenreManager.instance().populate(movie.getGenres(), holder.genres);
        holder.pinned.setChecked(movie.exists());
    }

    @Override
    public int getItemCount() {
        return mMedias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView title;
        public TextView date;
        public TextView description;
        public TextView genres;
        public RadioButton pinned;

        public ViewHolder(View view) {
            super(view);

            image = (ImageView) view.findViewById(R.id.media_poster);
            title = (TextView) view.findViewById(R.id.media_title);
            date = (TextView) view.findViewById(R.id.media_date);
            description = (TextView) view.findViewById(R.id.media_description);
            genres = (TextView) view.findViewById(R.id.media_genres);
            pinned = (RadioButton) view.findViewById(R.id.pin_indicator);
        }
    }
}
