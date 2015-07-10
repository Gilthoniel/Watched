package ch.watched.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.view.View;
import android.widget.*;
import ch.watched.android.R;
import ch.watched.android.adapters.Media;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.database.TVContract;
import ch.watched.android.webservice.WebService;

import java.util.List;

/**
 * Created by Gaylor on 01.07.2015.
 * Represent a TV Show
 */
public class SearchTV implements Media {

    private String backdrop_path;
    private String first_air_date;
    private List<Integer> genre_ids;
    private int id;
    private String original_language;
    private String original_name;
    private String overview;
    private List<String> origin_country;
    private String poster_path;
    private double popularity;
    private String name;
    private double vote_average;
    private double vote_count;
    private MediaState state;

    public double getVoteAverage() {
        return vote_average / 2;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public ContentValues getContentValues() {
        throw new UnsupportedOperationException("SearchTV can't insert in the database. Use TV.");
    }

    @Override
    public String getTableName() {
        return TVContract.TVEntry.TABLE_NAME;
    }

    @Override
    public String[] getColumnsProjection() {
        return TV.PROJECTION;
    }

    @Override
    public int getLayoutID() {
        return R.layout.item_tv_search;
    }

    @Override
    public View inflate(RelativeLayout layout) {

        TextView title = (TextView) layout.findViewById(R.id.search_item_title);
        title.setText(name);

        RatingBar rating = (RatingBar) layout.findViewById(R.id.search_item_rating);
        rating.setRating((float) getVoteAverage());

        ImageView image = (ImageView) layout.findViewById(R.id.search_item_image);
        image.setImageBitmap(null); // reset the old img
        if (poster_path != null) {
            WebService.loadImage(poster_path, image);
        }

        Switch button = (Switch) layout.findViewById(R.id.button_follow);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Switch switchButton = (Switch) view;
                if (switchButton.isChecked()) {
                    WebService.insertTV(id);
                } else {
                    DatabaseService.getInstance().delete(SearchTV.this);
                }
            }
        });

        // Update the state of the button related to the state of the media
        if (state != MediaState.NONE) {
            button.setChecked(true);
        }

        return layout;
    }

    @Override
    public void updateState() {
        Cursor cursor = DatabaseService.getInstance().read(SearchTV.this);
        if (cursor.getCount() > 0) {
            state = MediaState.PINNED;
        } else {
            state = MediaState.NONE;
        }
    }
}
