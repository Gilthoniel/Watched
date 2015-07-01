package ch.watched.android.models;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import ch.watched.android.R;
import ch.watched.android.adapters.MediaInflater;
import ch.watched.android.webservice.WebService;

import java.util.List;

/**
 * Created by Gaylor on 30.06.2015.
 * Movie data of the MovieDB
 */
public class Movie implements MediaInflater {

    private boolean adult;
    private String backdrop_path;
    private List<Integer> genre_ids;
    private long id;
    private String original_language;
    private String original_title;
    private String overview;
    private String release_date;
    private String poster_path;
    private double popularity;
    private String title;
    private double vote_average;
    private long vote_count;

    public String getTitle() {
        return title;
    }

    public String getOriginalTitle() {
        return original_title;
    }

    public String getDescription() {
        return overview;
    }

    public String getImage() {
        return poster_path;
    }

    public long getID() {
        return id;
    }

    public double getVoteAverage() {
        return vote_average / 2;
    }

    @Override
    public View inflate(View view) {
        TextView title = (TextView) view.findViewById(R.id.search_item_title);
        title.setText(getTitle());

        TextView originalTitle = (TextView) view.findViewById(R.id.search_item_otitle);
        originalTitle.setText(getOriginalTitle());

        RatingBar rating = (RatingBar) view.findViewById(R.id.search_item_rating);
        rating.setRating((float) getVoteAverage());

        ImageView image = (ImageView) view.findViewById(R.id.search_item_image);
        image.setImageBitmap(null); // reset the old img
        if (poster_path != null) {
            WebService.loadImage(poster_path, image);
        }

        return view;
    }
}
