package ch.watched.android.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import ch.watched.android.models.MovieDBConfiguration;
import ch.watched.android.service.utils.RequestCallback;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;

/**
 * Created by gaylor on 09/19/2015.
 *
 */
public class ImageLoader {

    private static final int MAXIMUM_IMAGES_STACK = 20;

    public enum ImageType { POSTER, BACKDROP, STILL }

    public static ImageLoader instance = new ImageLoader();

    private LinkedList<String> mStack;
    private Map<String, Bitmap> mImages;
    private Map<String, Set<ImageView>> mViews;
    private MovieDBConfiguration mConfig;
    private final Object mLock;

    private ImageLoader() {
        mLock = new Object();

        mStack = new LinkedList<>();
        mViews = new HashMap<>();
        mImages = new HashMap<>();

        BaseWebService.instance.getConfiguration(new RequestCallback<MovieDBConfiguration>() {
            @Override
            public void onSuccess(MovieDBConfiguration result) {
                mConfig = result;

                synchronized (mLock) {
                    mLock.notifyAll();
                }
            }

            @Override
            public void onFailure(Errors error) {
                Log.e("--IMAGE--", "ImageLoader can't load the MovieDB config file");
            }

            @Override
            public Type getType() {
                return MovieDBConfiguration.class;
            }
        });
    }

    public void cancel() {
        mViews.clear();
    }

    public void get(final String url, final ImageView image, ImageType type) {
        if (url == null || image == null) {
            return;
        }

        // Looking for the bitmap in the memory cache
        if (mImages.containsKey(url)) {
            image.setImageBitmap(mImages.get(url));
            image.setVisibility(View.VISIBLE);
            image.invalidate();
            return;
        }

        image.setImageBitmap(null);
        image.invalidate();

        // Looking for a request already launched
        if (mViews.containsKey(url)) {
            mViews.get(url).add(image);
            return;
        } else {

            mViews.put(url, new HashSet<ImageView>());
            mViews.get(url).add(image);
        }

        new ImageTask(type).executeOnExecutor(ConnectionService.instance.getExecutor(), url);
    }

    public void get(final String url, final ImageView image) {
        get(url, image, ImageType.POSTER);
    }

    private class ImageTask extends AsyncTask<String, Void, Bitmap> {

        private ImageType mType;
        private String mUrl;

        public ImageTask(ImageType type) {
            mType = type;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {

            // Wait for the initialization
            synchronized (mLock) {
                while (mConfig == null) {
                    try {
                        mLock.wait();
                    } catch (InterruptedException ignored) {}
                }
            }

            mUrl = urls[0];

            String key = CacheManager.KeyBuilder.forImage(mUrl);
            Bitmap bitmap = CacheManager.instance().getImage(key);
            if (bitmap != null) {
                return bitmap;
            }

            // base url
            Uri.Builder builder = new Uri.Builder();
            builder.encodedPath(mConfig.getUrl());
            switch (mType) {
                case POSTER:
                    builder.appendEncodedPath(mConfig.getPosterSize());
                    break;
                case BACKDROP:
                    builder.appendEncodedPath(mConfig.getBackdropSize());
                    break;
                case STILL:
                    builder.appendEncodedPath(mConfig.getStillSize());
                    break;
            }

            // image url
            builder.appendEncodedPath(mUrl);

            try {
                InputStream stream = new URL(builder.toString()).openConnection().getInputStream();

                bitmap = BitmapFactory.decodeStream(new BufferedInputStream(stream));

                if (bitmap != null) {
                    CacheManager.instance().add(key, bitmap);
                }

                return bitmap;

            } catch (IOException e) {
                Log.e("--IMAGE--", "Error when loading image");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (bitmap != null) {

                mStack.remove(mUrl);
                mStack.push(mUrl);
                mImages.put(mUrl, bitmap);

                if (mStack.size() > MAXIMUM_IMAGES_STACK) {

                    String key = mStack.pop();
                    mImages.remove(key);
                }

                if (mViews.containsKey(mUrl)) {
                    for (ImageView view : mViews.get(mUrl)) {
                        view.setImageBitmap(bitmap);
                        view.setVisibility(View.VISIBLE);
                        view.invalidate();
                    }
                }

            } else {

                Log.e("--IMAGE--", "Null value for an image");
            }

            mViews.remove(mUrl);
        }
    }
}
