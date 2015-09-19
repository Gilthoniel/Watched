package ch.watched.android.service.utils;

import android.graphics.Bitmap;

/**
 * Created by gaylor on 23.07.15.
 * Action triggered when an image is loaded
 */
public interface ImageObserver {
    void onImageLoaded(String url, Bitmap bitmap);
}
