package ch.watched.android.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ch.watched.android.R;
import ch.watched.android.adapters.Media;
import ch.watched.android.database.TVContract;
import ch.watched.android.webservice.WebService;

/**
 * Created by Gaylor on 07.07.2015.
 * TV Show for MovieDatabase
 */
public class TV implements Media {

    public static final String[] PROJECTION = new String[] {
            TVContract.TVEntry.COLUMN_ID,
            TVContract.TVEntry.COLUMN_HOMEPAGE,
            TVContract.TVEntry.COLUMN_IN_PROD,
            TVContract.TVEntry.COLUMN_LANGUAGE,
            TVContract.TVEntry.COLUMN_NAME,
            TVContract.TVEntry.COLUMN_NB_EPISODES,
            TVContract.TVEntry.COLUMN_NB_SEASONS,
            TVContract.TVEntry.COLUMN_OVERVIEW,
            TVContract.TVEntry.COLUMN_POSTER,
            TVContract.TVEntry.COLUMN_SCORE,
            TVContract.TVEntry.COLUMN_STATUS,
            TVContract.TVEntry.COLUMN_TYPE
    };

    private String homepage;
    private int id;
    private boolean in_production;
    private String name;
    private int number_of_episodes;
    private int number_of_seasons;
    private String original_language;
    private String original_title;
    private String overview;
    private String poster_path;
    private String status;
    private String type;
    private float vote_average;

    private MediaState state;

    public TV(Cursor cursor) {
        id = cursor.getInt(0);
        homepage = cursor.getString(1);
        in_production = cursor.getInt(2) == 1;
        original_language = cursor.getString(3);
        name = cursor.getString(4);
        number_of_episodes = cursor.getInt(5);
        number_of_seasons = cursor.getInt(6);
        overview = cursor.getString(7);
        poster_path = cursor.getString(8);
        vote_average = cursor.getFloat(9);
        status = cursor.getString(10);
        type = cursor.getString(11);

        state = MediaState.PINNED;
    }

    public String getImage() {
        return poster_path;
    }

    public String getTitle() {
        return name;
    }

    public float getScore() {
        return vote_average / 2;
    }

    public String getOverview() {
        return overview;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public View inflate(RelativeLayout layout) {

        TextView title = (TextView) layout.findViewById(R.id.title);
        title.setText(name);

        return layout;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(TVContract.TVEntry.COLUMN_ID, id);
        values.put(TVContract.TVEntry.COLUMN_HOMEPAGE, homepage);
        values.put(TVContract.TVEntry.COLUMN_IN_PROD, in_production ? 1 : 0);
        values.put(TVContract.TVEntry.COLUMN_LANGUAGE, original_language);
        values.put(TVContract.TVEntry.COLUMN_NAME, name);
        values.put(TVContract.TVEntry.COLUMN_NB_EPISODES, number_of_episodes);
        values.put(TVContract.TVEntry.COLUMN_NB_SEASONS, number_of_seasons);
        values.put(TVContract.TVEntry.COLUMN_ORIGINAL_TITLE, original_title);
        values.put(TVContract.TVEntry.COLUMN_OVERVIEW, overview);
        values.put(TVContract.TVEntry.COLUMN_POSTER, poster_path);
        values.put(TVContract.TVEntry.COLUMN_SCORE, vote_average);
        values.put(TVContract.TVEntry.COLUMN_STATUS, status);
        values.put(TVContract.TVEntry.COLUMN_TYPE, type);

        return values;
    }

    @Override
    public String getTableName() {
        return TVContract.TVEntry.TABLE_NAME;
    }

    @Override
    public String[] getColumnsProjection() {
        return PROJECTION;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public int getLayoutID() {
        return R.layout.item_tv_list;
    }

    @Override
    public void updateState() {
        state = MediaState.PINNED;
    }
}
