package ch.watched.android.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import ch.watched.R;
import ch.watched.android.models.Media;
import ch.watched.android.service.ImageLoader;

import java.util.Collection;
import java.util.List;

/**
 * Created by gaylor on 09/19/2015.
 * Adapter for Recycler View and Media Card View
 */
public class MediaCardAdapter<T extends Media> extends RecyclerView.Adapter<MediaCardAdapter.ViewHolder> {

    private List<T> mMedias;
    private OnItemClickListener mListener;

    public MediaCardAdapter(List<T> medias) {
        mMedias = medias;
    }

    public void putAll(Collection<T> collection) {

        mMedias.clear();
        mMedias.addAll(collection);

        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_media, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MediaCardAdapter.ViewHolder holder, final int position) {

        final Media media = mMedias.get(position);

        holder.position = position;

        holder.title.setText(media.getTitle());
        holder.overview.setText(media.getOverview());
        holder.textNext.setText(media.getNextMedia());
        holder.poster.setImageBitmap(null);
        ImageLoader.instance.get(media.getPoster(), holder.poster);

        holder.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (media.next()) {
                    notifyItemRemoved(position);
                    mMedias.remove(position);
                    notifyDataSetChanged();
                } else {
                    holder.textNext.setText(media.getNextMedia());
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return mMedias.get(position).getID();
    }

    @Override
    public int getItemCount() {
        return mMedias.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView title;
        public TextView overview;
        public TextView textNext;
        public ImageView poster;
        public ImageButton next;
        public int position;

        public ViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.media_title);
            overview = (TextView) view.findViewById(R.id.media_overview);
            textNext = (TextView) view.findViewById(R.id.next_episode);
            poster = (ImageView) view.findViewById(R.id.media_poster);
            next = (ImageButton) view.findViewById(R.id.button_next);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onClick(view, position);
            }
        }
    }

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }
}
