package ch.watched.android.views;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Space;
import ch.watched.R;

/**
 * Created by Gaylor on 24.09.2015.
 * Pagination for ViewPager
 */
public class PaginationView extends HorizontalScrollView {

    private ViewPager mViewPager;
    private View mInternalView;

    public PaginationView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setFocusable(false);
        setFocusableInTouchMode(false);
        setClickable(false);

        LinearLayout mLayout = new LinearLayout(context);
        mLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 10));
        mLayout.setOrientation(LinearLayout.HORIZONTAL);
        addView(mLayout);

        mInternalView = new Space(context);
        mLayout.addView(mInternalView);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return false;
    }

    public void setViewPager(final ViewPager pager) {
        mViewPager = pager;
        pager.getAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                buildPagination();
            }
        });
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                smoothScrollTo(mViewPager.getMeasuredWidth() * position, 0);
            }
        });

        buildPagination();
    }

    private void buildPagination() {

        post(new Runnable() {
            @Override
            public void run() {
                int width = mViewPager.getAdapter().getCount() * getMeasuredWidth();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, 10);
                mInternalView.setLayoutParams(params);

                smoothScrollTo(mViewPager.getCurrentItem() * mViewPager.getMeasuredWidth(), 0);
            }
        });
    }
}
