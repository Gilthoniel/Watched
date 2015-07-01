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
 * Created by Gaylor on 01.07.2015.
 * Represent a TV Show
 */
public class Serie implements MediaInflater {

    private String backdrop_path;
    private String first_air_date;
    private List<Integer> genre_ids;
    private long id;
    private String original_language;
    private String original_name;
    private String overview;
    private List<String> origin_country;
    private String poster_path;
    private double popularity;
    private String name;
    private double vote_average;
    private double vote_count;

    public double getVoteAverage() {
        return vote_average / 2;
    }

    @Override
    public View inflate(View view) {
        TextView title = (TextView) view.findViewById(R.id.search_item_title);
        title.setText(name);

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
