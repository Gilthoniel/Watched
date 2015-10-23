package ch.watched.android;

import android.app.Application;
import android.preference.PreferenceManager;
import android.util.Log;
import ch.watched.BuildConfig;
import ch.watched.R;
import ch.watched.android.constants.Utils;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.service.BaseWebService;
import ch.watched.android.service.CacheManager;
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by gaylor on 08/29/2015.
 * Android application base implementation
 */
@ReportsCrashes(
        mailTo = "gaylor.bosson@gmail.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.report_bug
)
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        PreferenceManager.setDefaultValues(this, R.xml.pref_discover, false);
        Utils.setLocaleDate(getResources().getConfiguration().locale);

        CacheManager.init(getApplicationContext());
        DatabaseService.getInstance().initHelper(getApplicationContext());

        if (BuildConfig.DEBUG) {
            Log.d("DEBUG MODE", "ACRA disabled");
        } else {
            ACRA.init(this);
        }
    }
}
