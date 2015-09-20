package ch.watched.android;

import android.app.Application;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.service.BaseWebService;
import ch.watched.android.service.CacheManager;

/**
 * Created by gaylor on 08/29/2015.
 *
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CacheManager.init(getApplicationContext());
        DatabaseService.getInstance().initHelper(getApplicationContext());
    }
}
