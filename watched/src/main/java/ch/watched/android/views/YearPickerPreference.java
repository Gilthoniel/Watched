package ch.watched.android.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import ch.watched.R;

import java.util.Calendar;

/**
 * Created by Gaylor on 23.10.2015.
 *
 */
public class YearPickerPreference extends DialogPreference {

    private static final int DEFAULT_VALUE = 2000;
    private NumberPicker mPicker;
    private int mCurrent;

    public YearPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.pref_year_picker);
        setPositiveButtonText("Ok");
        setNegativeButtonText("Cancel");
    }

    public int getValue() {
        return mCurrent;
    }

    @Override
    protected void onDialogClosed(boolean positive) {
        if (positive) {
            mCurrent = mPicker.getValue();
            persistInt(mCurrent);
        }
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue) {
        if (restore) {
            mCurrent = this.getPersistedInt(DEFAULT_VALUE);
        } else {
            mCurrent = (Integer) defaultValue;
            persistInt(mCurrent);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray array, int index) {
        return array.getInteger(index, DEFAULT_VALUE);
    }

    @Override
    protected void onBindDialogView(@NonNull View view) {
        mPicker = (NumberPicker) view.findViewById(R.id.numberPicker);
        mPicker.setMinValue(1900);
        Calendar calendar = Calendar.getInstance();
        mPicker.setMaxValue(calendar.get(Calendar.YEAR) + 50);
        mPicker.setValue(mCurrent);
    }
}
