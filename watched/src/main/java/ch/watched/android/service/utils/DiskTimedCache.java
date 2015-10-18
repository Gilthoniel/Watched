package ch.watched.android.service.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by gaylor on 04-Aug-15.
 *
 * Cache on the disk with LRU policy and expiration time for each object
 */
public class DiskTimedCache {

    private LinkedList<String> mStack;
    private Map<String, CacheFile> mFiles;
    private File mDirectory;
    private long mMaxSize;

    private DiskTimedCache() {

        // Stack to manage the LRU policy
        mStack = new LinkedList<>();
        mFiles = new HashMap<>();
    }

    /**
     * Return true if the key is in the cache and the element is not expired
     * @param key Key of the object
     * @return true or false
     */
    public boolean contains(String key) {
        return mStack.contains(key) && !mFiles.get(key).isExpired() && mFiles.get(key).exists();
    }

    /**
     * Return an object with the key
     * @param key Key of the object
     * @return An Object or null
     */
    public Object get(String key) {

        CacheFile file = searchCacheFile(key);
        if (file == null) {
            return null;
        }

        // Put the element at the top of the list
        mStack.remove(key);
        mStack.push(key);

        ObjectInputStream stream = null;
        try {

            stream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));

            return stream.readObject();

        } catch (ClassNotFoundException | IOException e) {

            Log.e("__CACHE__", "Can't find a file which must exist");
            return null;

        } finally {

            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ignored) {}
            }
        }
    }

    /**
     * Return an image with the key
     * @param key Key of the image
     * @return a Bitmap or null
     */
    public Bitmap getImage(String key) {

        CacheFile file = searchCacheFile(key);
        if (file == null) {
            return null;
        }

        // Put the element at the top of the list
        mStack.remove(key);
        mStack.push(key);

        InputStream stream = null;
        try {
            stream = new BufferedInputStream(new FileInputStream(file));

            return BitmapFactory.decodeStream(stream);

        } catch (IOException e) {

            Log.e("__CACHE__", "Can't read image file");
            return null;

        } finally {

            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ignored) {}
            }
        }
    }

    /**
     * Add an object to the cache with an expiration time in ms
     * @param key Key of the object
     * @param object Object
     * @param expiration Timestamp of the expiration
     * @param <T> Special type for the object
     */
    public <T extends Serializable> void add(String key, T object, long expiration) {

        CacheFile file = searchCacheFile(key, expiration);
        if (file == null) {
            return;
        }

        ObjectOutputStream stream = null;
        try {
            // Create the file
            stream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            stream.writeObject(object);

            stream.flush();

            // Populate the stack and map
            mStack.push(key);
            mFiles.put(key, file);

            checkMaxSize();
        } catch (IOException e) {

            Log.e("__CACHE__", "Can't write a file cache");

        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Add an object with no expiration time
     * @param key Key of the object
     * @param object Object which implements Serializable
     * @param <T> Special type for the object
     */
    public <T extends Serializable> void add(String key, T object) {

        add(key, object, 0);
    }

    /**
     * Add an image to the cache
     * @param key Key of the image
     * @param bitmap Bitmap of the image
     */
    public void add(String key, Bitmap bitmap) {
        CacheFile file = searchCacheFile(key, 0);
        if (file == null) {
            return;
        }

        OutputStream stream = null;
        try {
            stream = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            stream.flush();

            // Populate the stack and map
            mStack.push(key);
            mFiles.put(key, file);

        } catch (IOException e) {

            Log.e("__CACHE__", "Can't save bitmap into file");

        } finally {

            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Empty the cache and clear the directory
     */
    public void clear() {
        mStack.clear();
        mFiles.clear();
        cleanDirectory();
    }

    /**
     * Singleton to create and instantiate the cache
     */
    public static class Factory {

        public static final Factory instance = new Factory();

        private Factory() {}

        /**
         * Create a cache in a specific directory
         * @param file Directory where to populate the cache
         * @param maxSize maximum size of the cache (NOT STRICT)
         * @return the instantiate cache or null if a failure occurred
         */
        public DiskTimedCache open(@NonNull File file, int version, long maxSize) throws IOException {

            DiskTimedCache cache = new DiskTimedCache();
            cache.mDirectory = file;
            cache.mMaxSize = maxSize;

            // Create the directory if it doesn't exist
            if (cache.mDirectory.mkdirs()) {
                Log.d("__CACHE__", "Cache directory created");
            }

            return cache;
        }

        public DiskTimedCache empty() {

            return new DiskTimedCache();
        }
    }

    /* Exceptions */
    public class BadKeyException extends IllegalArgumentException {

        private static final long serialVersionUID = -5686499348456010299L;

        @Override
        public String getMessage() {
            return "Key must match [a-z0-9-_]";
        }
    }

    /* Private functions */

    private void checkMaxSize() {

        cleanDirectory();

        long size = 0;
        String[] files = mDirectory.list();
        for (String filename : files) {
            File file = new File(mDirectory, filename);

            size += file.length();
        }

        while (size > mMaxSize && mDirectory.list().length > 0) {

            String key = mStack.getLast();
            size -= mFiles.get(key).length();

            if (!mFiles.get(key).delete()) {
                Log.e("__CACHE__", "Error when trying to delete a file in the cache.");
            }

            mStack.remove(key);
            mFiles.remove(key);
        }
    }

    private void cleanDirectory() {

        Iterator<String> it = mFiles.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            CacheFile file = mFiles.get(key);
            if (file.isExpired()) {
                if (!file.delete()) {
                    Log.e("__CACHE__", "Can't remove a file");
                }

                mStack.remove(key);
                it.remove();
            }
        }

        String[] filesList = mDirectory.list();
        if (filesList != null) {
            for (String filename : filesList) {

                if (!mStack.contains(filename)) {
                    File file = new File(mDirectory, filename);

                    if (!file.delete()) {
                        Log.e("__CACHE__", "Error when trying to delete a file in the cache: " + file.getPath());
                    }
                }
            }
        } else {

            Log.e("__CACHE__", "Can't clean the cache directory");
        }

        Log.i("__CACHE__", "Directory size after cleaning: "+mDirectory.list().length);
    }

    private CacheFile searchCacheFile(String key, long expiration) {
        if (!key.matches("^[a-z0-9-_]{1,64}$")) {
            throw new BadKeyException();
        }

        if (mDirectory == null) {
            return null;
        }

        CacheFile file = mFiles.get(key);
        if (file == null) {
            file = new CacheFile(mDirectory, key, expiration);
        }

        return file;
    }

    private CacheFile searchCacheFile(String key) {
        if (!key.matches("^[a-z0-9-_]{1,64}$")) {
            throw new BadKeyException();
        }

        if (mDirectory == null) {
            return null;
        }

        return mFiles.get(key);
    }
}
