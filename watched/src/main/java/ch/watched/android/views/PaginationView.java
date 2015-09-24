package ch.watched.android.views;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by Gaylor on 24.09.2015.
 * Pagination for ViewPager
 */
public class PaginationView extends RadioGroup {

    private ViewPager mViewPager;
    private ViewGroup.LayoutParams mParams;
    private SparseIntArray mIDs;

    public PaginationView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mIDs = new SparseIntArray();
        mParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public void setViewPager(final ViewPager pager) {
        mViewPager = pager;

        setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup container, int checkedID) {
                int position = mIDs.indexOfValue(checkedID);

                if (pager.getCurrentItem() != position) {
                    pager.setCurrentItem(position, true);
                }
            }
        });

        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                check(mIDs.get(position));
            }
        });
        pager.getAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                buildPagination();
            }
        });

        buildPagination();
    }

    private void buildPagination() {

        int oldIndex = mIDs.indexOfValue(getCheckedRadioButtonId());

        removeAllViews();

        for (int i = 0; i < mViewPager.getAdapter().getCount(); i++) {
            RadioButton button = new RadioButton(mViewPager.getContext());
            mIDs.put(i, View.generateViewId());
            button.setId(mIDs.get(i));

            addView(button, mParams);
        }

        if (mIDs.size() > 0) {

            int index = 0;
            if (oldIndex > 0) {
                index = oldIndex < mIDs.size() ? oldIndex : mIDs.size() - 1;
            }

            check(mIDs.get(index));
        }
    }
}
