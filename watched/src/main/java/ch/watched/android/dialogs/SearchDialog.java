package ch.watched.android.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import ch.watched.R;
import ch.watched.android.adapters.MediaSearchAdapter;
import ch.watched.android.constants.Utils;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.database.MovieContract;
import ch.watched.android.models.Media;
import ch.watched.android.service.BaseWebService;

/**
 * Created by gaylor on 08/30/2015.
 * Dialog to show information about a movie
 */
public class SearchDialog extends DialogFragment {

    private Media mMedia;
    private MediaSearchAdapter mAdapter;

    public void setMedia(Media media) {
        mMedia = media;
    }

    public void setAdapter(MediaSearchAdapter adapter) {
        mAdapter = adapter;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle states) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_search, null);

        ((TextView) view.findViewById(R.id.media_title)).setText(mMedia.getTitle());
        ((RatingBar) view.findViewById(R.id.media_rating)).setRating(mMedia.getRating());
        ((TextView) view.findViewById(R.id.media_overview)).setText(mMedia.getOverview());
        ((TextView) view.findViewById(R.id.media_date)).setText(mMedia.getDate());

        builder.setView(view);

        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });

        if (mMedia.exists()) {
            builder.setPositiveButton("Unpinned", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    mMedia.remove(null);

                    mAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            });
        } else {
            builder.setPositiveButton("Pinned", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {

                    dialog.dismiss();

                    final Dialog progress = Utils.createProgressDialog(mAdapter.getContext());
                    progress.show();

                    mMedia.insert(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            progress.dismiss();
                        }
                    });
                }
            });
        }

        return builder.create();
    }
}
