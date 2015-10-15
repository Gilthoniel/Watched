package ch.watched.android.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import ch.watched.R;
import ch.watched.android.models.Episode;
import ch.watched.android.service.ImageLoader;

/**
 * Created by Gaylor on 15.10.2015.
 * Display the information about an episode
 */
public class EpisodeDialog extends DialogFragment {

    private Episode mEpisode;

    public void setEpisode(Episode episode) {
        mEpisode = episode;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle states) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_episode, null);

        ImageLoader.instance.get(mEpisode.getPoster(), (ImageView) view.findViewById(R.id.media_image), ImageLoader.ImageType.STILL);
        ((TextView) view.findViewById(R.id.media_title)).setText(mEpisode.getTitle());
        StringBuilder number = new StringBuilder();
        number.append(mEpisode.getSeasonNumber());
        if (mEpisode.getEpisodeNumber() < 10) {
            number.append("0");
        }
        number.append(mEpisode.getEpisodeNumber());
        ((TextView) view.findViewById(R.id.media_season)).setText(number.toString());
        ((TextView) view.findViewById(R.id.media_overview)).setText(mEpisode.getOverview());

        builder.setView(view);

        builder.setNeutralButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });

        return builder.create();
    }
}
