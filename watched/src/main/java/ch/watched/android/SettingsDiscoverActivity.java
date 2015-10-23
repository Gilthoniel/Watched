package ch.watched.android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import android.widget.NumberPicker;
import ch.watched.R;
import ch.watched.android.views.YearPickerPreference;

public class SettingsDiscoverActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle states) {
        super.onCreate(states);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsDiscoverFragment())
                .commit();

    }

    public static class SettingsDiscoverFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle states) {
            super.onCreate(states);

            addPreferencesFromResource(R.xml.pref_discover);

            ListPreference typePref = (ListPreference) findPreference("pref_discover_type");
            typePref.setSummary(typePref.getEntry());

            ListPreference sortPref = (ListPreference) findPreference("pref_discover_sort");
            sortPref.setSummary(sortPref.getEntry());

            YearPickerPreference datePref = (YearPickerPreference) findPreference("pref_discover_date_ge");
            datePref.setSummary("Display only the movies or shows released after " + datePref.getValue());
        }

        @Override
        public void onActivityCreated(Bundle states) {
            super.onActivityCreated(states);

            PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

            Preference pref = findPreference(key);

            switch (key) {
                case "pref_discover_type":
                    pref.setSummary(((ListPreference) pref).getEntry());
                    break;
                case "pref_discover_sort":
                    pref.setSummary(((ListPreference) pref).getEntry());
                    break;
                case "pref_discover_date_ge":
                    pref.setSummary("Display only the movies or shows released after " + ((YearPickerPreference) pref).getValue());
                default:
                    break;
            }
        }
    }
}
