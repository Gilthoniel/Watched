package ch.watched.android.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import ch.watched.android.service.utils.DiskTimedCache;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gaylor on 30.07.15.
 *
 */
public class CacheManager {

    private static CacheManager instance;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 20; // 20mo
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
    public <T> T get(String key) {

        synchronized (mDiskCacheLock) {

            try {
                // waiting for initialization of the cache
                while (mDiskCacheStarting) {
                    mDiskCacheLock.wait();
                }
            } catch (InterruptedException ignored) {}

            // Check that the cache's initialization has not failed
            if (mDiskCache == null) {
                return null;
            }

            DiskTimedCache.Accessor accessor = mDiskCache.get(key);

            if (accessor != null) {

                T object;
                try {
                    ObjectInputStream stream = new ObjectInputStream(accessor.getStream());

                    // We know what we put in the cache
                    object = (T) stream.readObject();
                } catch (ClassNotFoundException | IOException e) {

                    e.printStackTrace();

                    object = null;

                } finally {

                    accessor.commit();
                }

                return object;
            }
        }

        return null;
    }

    /**
     * Get the image stores with the key, or get a null result
     * @param key key of the image
     * @return Bitmap or Null
     */
    public Bitmap getImage(String key) {

        synchronized (mDiskCacheLock) {

            try {
                // waiting for initialization of the cache
                while (mDiskCacheStarting) {
                    mDiskCacheLock.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            DiskTimedCache.Accessor accessor = mDiskCache.get(key);

            if (accessor != null) {
                InputStream stream = accessor.getStream();

                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                accessor.commit();

                return bitmap;
            }

        }

        return null;
    }

    /**
     * Add an object to the cache
     * @param key Key of the entry
     * @param object Object : MUST implement Serializable
     */
    public void add(String key, Object object, int expiration) {

        synchronized (mDiskCacheLock) {
            try {
                // waiting for initialization of the cache
                while (mDiskCacheStarting) {
                    mDiskCacheLock.wait();
                }
            } catch (InterruptedException ignored) {}

            try {
                DiskTimedCache.Editor editor = mDiskCache.edit(key, expiration);
                if (editor != null) {
                    ObjectOutputStream stream = new ObjectOutputStream(editor.getStream());
                    stream.writeObject(object);
                    editor.commit();
                }
            } catch (IOException e) {

                e.printStackTrace();
            }

        }
    }

    public void add(String key, Object object) {
        add(key, object, 0);
    }

    /**
     * Store an image in the cache
     * @param key Key of the image
     * @param bitmap Bitmap format of the image
     */
    public void add(String key, Bitmap bitmap) {

        synchronized (mDiskCacheLock) {
            try {
                // waiting for initialization of the cache
                while (mDiskCacheStarting) {
                    mDiskCacheLock.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            DiskTimedCache.Editor editor = mDiskCache.edit(key);
            if (editor != null) {
                OutputStream stream = editor.getStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                editor.commit();
            }

        }
    }

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
