package ch.watched.android.service;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import ch.watched.android.service.utils.ImageObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gaylor on 23.07.15.
 * Management to load and distribute images to the views
 */
public class ImagesManager {

    private static ImagesManager instance;

    private Map<String, List<ImageObserver>> mObservers;

    private ImagesManager() {
        mObservers = new HashMap<>();
    }

    public static void init() {
        instance = new ImagesManager();
    }

    public static ImagesManager instance() {
        return instance;
    }

    /**
     * Get the singleton
     * @return instance
     */
    static public ImagesManager getInstance() {
        return instance;
    }

    /**
     * Return the image to the observer or execute a request and put the observer in the waiting list
     * @param url of the image use as key
     * @param observer to warn when the image is loaded
     */
    public void get(String url, ImageObserver observer) {

        String key = CacheManager.KeyBuilder.forImage(url);
        if (key == null) {
            return;
        }

        new GetImageTask(url, observer).execute(key);
    }

    /**
     * Add the bitmap to the storage with an certain key
     * @param url key of the cache
     * @param bitmap image
     */
    public void addBitmap(String url, Bitmap bitmap) {
        String key = CacheManager.KeyBuilder.forImage(url);
        if (key == null) {
            return;
        }

        // Inform the observers that the image is there
        if (mObservers.containsKey(key)) {
            for (ImageObserver observer : mObservers.get(key)) {
                observer.onImageLoaded(url, bitmap);
            }

            mObservers.remove(key);
        }
    }

    /**
     * Background task to get the image from the disk or from the web
     */
    private class GetImageTask extends AsyncTask<String, Void, Bitmap> {

        private String mUrl;
        private ImageObserver mObserver;

        public GetImageTask(String url, ImageObserver observer) {
            mUrl = url;
            mObserver = observer;
        }

        @Override
        protected Bitmap doInBackground(String... keys) {

            String key = keys[0];

            // Try to acquire from the disk cache
            Bitmap bitmap = CacheManager.instance().getImage(key);
            if (bitmap != null) {
                return bitmap;
            }

            // Load with an http request

            // add the observer for this image
            if (!mObservers.containsKey(key)) {
                List<ImageObserver> observers = new ArrayList<>();
                observers.add(mObserver);

                mObservers.put(key, observers);

                BaseWebService.instance.loadImage(mUrl);
            } else {
                // The image is already loading
                mObservers.get(key).add(mObserver);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap image) {

            // If we found the image in the disk cache
            if (image != null) {
                mObserver.onImageLoaded(mUrl, image);
            }
        }
    }
}
