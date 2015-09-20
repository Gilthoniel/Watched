package ch.watched.android.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import ch.watched.android.service.utils.DiskTimedCache;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gaylor on 30.07.15.
 *
 */
public class CacheManager {

    private static CacheManager instance;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 100; // 100mo
    private static final String DISK_CACHE_NAME = "webcache";

    private final Object mDiskCacheLock;
    private boolean mDiskCacheStarting;
    private DiskTimedCache mDiskCache;

    private CacheManager() {
        mDiskCacheLock = new Object();

        mDiskCacheStarting = true;
    }

    public static void init(Context context) {
        instance = new CacheManager();

        instance.initCacheDisk(context);
    }

    public static CacheManager instance() {
        return instance;
    }

    /**
     * Initialize the disk cache in background
     * @param context of the application
     */
    public void initCacheDisk(Context context) {

        File external = context.getExternalCacheDir();
        File cacheDir;
        if (external != null && external.canWrite()) {
            cacheDir = new File(external, DISK_CACHE_NAME);
        } else {
            cacheDir = new File(context.getCacheDir(), DISK_CACHE_NAME);
        }

        Log.d("__CACHE__", cacheDir.getAbsolutePath());
        new InitDiskCacheTask().execute(cacheDir);
    }

    /**
     * Get an object of the cache
     * @param key Key of the entry
     * @param <T> Type of the object
     * @return the object
     */
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T get(String key) {

        synchronized (mDiskCacheLock) {

            try {
                // waiting for initialization of the cache
                while (mDiskCacheStarting) {
                    mDiskCacheLock.wait();
                }
            } catch (InterruptedException ignored) {}

            // Check that the cache's initialization has not failed
            if (mDiskCache == null || !mDiskCache.contains(key)) {
                return null;
            }

            return (T) mDiskCache.get(key);
        }
    }

    public Bitmap getImage(String key) {

        synchronized (mDiskCacheLock) {

            try {
                // waiting for initialization of the cache
                while (mDiskCacheStarting) {
                    mDiskCacheLock.wait();
                }
            } catch (InterruptedException ignored) {}

            // Check that the cache's initialization has not failed
            if (mDiskCache == null || !mDiskCache.contains(key)) {
                return null;
            }

            return mDiskCache.getImage(key);
        }
    }

    /**
     * Add an object to the cache
     * @param key Key of the entry
     * @param object Object : MUST implement Serializable
     */
    public <T extends Object & Serializable> void add(String key, T object, int expiration) {

        synchronized (mDiskCacheLock) {
            try {
                // waiting for initialization of the cache
                while (mDiskCacheStarting) {
                    mDiskCacheLock.wait();
                }
            } catch (InterruptedException ignored) {}

            mDiskCache.add(key, object, System.currentTimeMillis() + expiration);
        }
    }

    public void add(String key, Bitmap bitmap) {

        synchronized (mDiskCacheLock) {
            try {
                // waiting for initialization of the cache
                while (mDiskCacheStarting) {
                    mDiskCacheLock.wait();
                }
            } catch (InterruptedException ignored) {}

            mDiskCache.add(key, bitmap);
        }
    }

    /**
     * Add an object to the cache without expiration time
     * @param key Key of the object
     * @param object Must implement Serializable
     */
    public <T extends Object & Serializable> void add(String key, T object) {
        add(key, object, 0);
    }

    /**
     * Empty the cache
     */
    public void clear() {

        synchronized (mDiskCacheLock) {
            try {
                // waiting for initialization of the cache
                while (mDiskCacheStarting) {
                    mDiskCacheLock.wait();
                }
            } catch (InterruptedException ignored) {}

            mDiskCache.clear();
        }
    }

    /**
     * Helper for the keys of the cache
     * Keys must correspond to mPattern
     */
    public static class KeyBuilder {

        public static final KeyBuilder instance = new KeyBuilder();

        private static Pattern mPattern;
        private static Pattern mCleaner;

        private KeyBuilder() {
            mPattern = Pattern.compile("([a-zA-Z0-9-_]{1,64})");

            mCleaner = Pattern.compile("[^a-zA-Z0-9-_]");
        }

        /**
         *
         * @param url url of the image
         * @return the key version
         */
        public static String forImage(String url) {
            if (url == null || url.length() == 0) {
                return null;
            }
            Matcher matcher = mPattern.matcher(url);

            if (matcher.find(url.lastIndexOf("/"))) {
                return matcher.group(1).toLowerCase();
            } else {
                return null;
            }
        }

        private static String clean(String sequence) {

            Matcher matcher = mCleaner.matcher(sequence);
            // Clear the wrong characters, the key need to satisfy [a-z0-9]
            return matcher.replaceAll("").toLowerCase();
        }
    }

    /**
     * Async task to init the disk cache mechanism
     */
    private class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... params) {
            synchronized (mDiskCacheLock) {
                File cacheDir = params[0];
                try {
                    mDiskCache = DiskTimedCache.Factory.instance.open(cacheDir, 0, DISK_CACHE_SIZE);
                } catch (IOException e) {

                    Log.e("__CACHE__", "Initialize with empty cache");
                    Log.e("__CACHE__", "Message: " + e.getMessage());
                    mDiskCache = DiskTimedCache.Factory.instance.empty();
                }

                mDiskCacheStarting = false;
                mDiskCacheLock.notifyAll();
            }

            return null;
        }
    }

}
