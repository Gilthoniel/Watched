package ch.watched.android.service.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.*;
import java.util.*;

/**
 * Created by gaylor on 04-Aug-15.
 *
 * Cache on the disk with LRU policy and expiration time for each object
 */
public class DiskTimedCache {

    public static final String JOURNAL_FILE_NAME = "journal";

    private LinkedList<CacheFile> mStack;
    private File mDirectory;
    private CacheFile mJournal;
    private long mMaxSize;
    private int mVersion;
    private boolean isInitiated;

    private DiskTimedCache() {

        // Stack to manage the LRU policy
        mStack = new LinkedList<>();

        isInitiated = false;
    }

    public File getDirectory() {

        return mDirectory;
    }

    public List<CacheFile> getStack() {
        return mStack;
    }

    /**
     * Get an editor to set a value
     * @param key Key of the object
     * @param expiredTime in millisecond
     * @return Editor
     */
    public Editor edit(String key, int expiredTime) {
        if (!key.matches("^[a-z0-9-_]{1,64}$")) {
            throw new BadKeyException();
        }

        // Check the state of the cache
        if (!isInitiated) {
            return null;
        }

        // File where to populate the object
        CacheFile file = searchFileInCache(key);

        // Get the write lock
        file.getLock().writeLock().lock();

        Editor editor = new Editor();
        try {

            // Stream for the user
            editor.mStream = new BufferedOutputStream(new FileOutputStream(file));
            editor.mFile = file;

            // Only if we can open the stream without exception
            if (!updateStack(file, expiredTime)) {

                editor.commit();
                editor = null;
            }

        } catch (FileNotFoundException e) {

            Log.e("__CACHE__", "When create object file: " + file.getAbsolutePath());
            e.printStackTrace();

            file.getLock().writeLock().unlock();

        }

        return editor;
    }

    /**
     * Edit without cache
     * @param key Key of the object
     * @return an Editor which contains a stream
     */
    public Editor edit(String key) {

        return edit(key, 0);

    }

    /**
     * Get an accessor to read a object in the stack
     * @param key Key of the object
     * @return Accessor
     */
    public Accessor get(String key) {
        if (!key.matches("^[a-z0-9-_]{1,64}$")) {
            throw new BadKeyException();
        }

        // Check the state of the cache
        if (!isInitiated) {
            return null;
        }

        CacheFile file = searchFileInCache(key);

        file.getLock().readLock().lock();

        try {
            // throw exception if the key doesn't exist
            Accessor accessor = new Accessor();
            accessor.mFile = file;
            accessor.mStream = new BufferedInputStream(new FileInputStream(file));

            // Put the file at the top of the stack if it exists
            if (updateStack(file)) {

                return accessor;
            } else {

                accessor.commit();
            }

        } catch (FileNotFoundException e) {
            file.getLock().readLock().unlock();
        }

        return null;
    }

    /**
     * Shutdown the cache, without removing the content
     */
    public void close() {

    }

    /* Public class */

    public class Editor {

        private OutputStream mStream;
        private CacheFile mFile;

        private Editor() {}

        public OutputStream getStream() {
            return mStream;
        }

        /**
         * End of transaction, to check max size
         */
        public void commit() {
            try {

                mStream.flush();
                mStream.close();

                // Check the size of the cache
                long size = 0;
                for (CacheFile file : mStack) {

                    size += file.length();
                }

                /* Check the size of the cache */
                while (size > mMaxSize - mJournal.length()) {
                    CacheFile file = mStack.removeLast();
                    long fileSize = file.length();

                    if (file.delete()) {
                        size -= fileSize;
                    }
                }

            } catch (IOException e) {

                e.printStackTrace();

            }

            mFile.getLock().writeLock().unlock();
        }
    }

    public class Accessor {

        private InputStream mStream;
        private CacheFile mFile;

        private Accessor() {}

        public InputStream getStream() {
            return mStream;
        }

        public void commit() {
            try {
                mStream.close();
            } catch (IOException ignored) {}

            mFile.getLock().readLock().unlock();
        }
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
        public DiskTimedCache open(@NonNull File file, int version, long maxSize)
            throws IOException
        {

            DiskTimedCache cache = new DiskTimedCache();
            cache.mDirectory = file;
            cache.mMaxSize = maxSize;

            if (version > 0) {
                cache.mVersion = version;
            } else {
                cache.mVersion = -1 * version;
            }

            // Create the directory if it doesn't exist
            if (cache.mDirectory.mkdirs()) {
                Log.d("__CACHE__", "Cache directory created");
            }

            // Create log journal
            CacheFile journal = new CacheFile(file.getAbsolutePath(), JOURNAL_FILE_NAME + "_" + cache.mVersion);
            if (!journal.createNewFile() && !journal.canWrite()) {
                throw new IOException("Can't read/write the journal file");
            }
            cache.mJournal = journal;

            // Try to open the journal file
            try {
                BufferedReader stream = new BufferedReader(new FileReader(journal));

                String line;
                while ((line = stream.readLine()) != null) {

                    String[] parts = line.split(" ");
                    CacheFile item = new CacheFile(cache.mDirectory, parts[0]);

                    cache.mStack.addLast(item);
                }
                stream.close();

            } catch (IOException e) {
                // The file is not found or corrupt so we don't have anything to do
                // because the files will be deleted
            }

            cache.isInitiated = true;
            return cache;
        }

        public DiskTimedCache empty() {

            DiskTimedCache cache = new DiskTimedCache();
            cache.isInitiated = false;

            return cache;
        }
    }

    /* Exceptions */
    public class BadKeyException extends IllegalArgumentException {

        @Override
        public String getMessage() {
            return "Key must match [a-z0-9-_]";
        }
    }

    /* Private functions */

    private boolean updateStack(CacheFile file, int expirationTime) {

        int index = mStack.indexOf(file);
        if (index > -1) {
            mStack.remove(index);
        }
        mStack.addFirst(file);

        try {

            BufferedReader reader = new BufferedReader(new FileReader(mJournal));

            Map<String, Integer> data = new HashMap<>();
            data.put(file.getName(), expirationTime);

            String line;
            while ((line = reader.readLine()) != null) {

                String filename = getFilenameFromLine(line);
                if (!filename.equals(file.getName())) {
                    int expiration = getExpirationFromLine(line);
                    data.put(filename, expiration);
                }
            }

            reader.close();

            // Write data in the journal
            updateJournal(data);

        } catch (IOException e) {

            e.printStackTrace();

            return false;
        }

        return true;
    }

    private boolean updateStack(CacheFile file) {

        int index = mStack.indexOf(file);
        if (index > -1) {
            mStack.remove(index);
        }

        try {

            BufferedReader reader = new BufferedReader(new FileReader(mJournal));
            for (int i = 0; i < index; i++) {
                reader.readLine();
            }

            String line = reader.readLine();
            reader.close();
            if (line != null) {
                int expiration = getExpirationFromLine(line);
                if (expiration <= 0 || file.lastModified() + expiration > new Date().getTime()) {

                    mStack.addFirst(file);
                } else {

                    cleanDirectory();
                    return false;
                }
            } else {

                Log.e("__CACHE__", "Journal corrupted");
            }

        } catch (IOException e) {

            e.printStackTrace();
            return false;
        }

        return true;
    }

    private CacheFile searchFileInCache(String key) {

        for (CacheFile file : mStack) {
            if (file.getName().equals(key)) {
                return file;
            }
        }

        return new CacheFile(mDirectory, key);
    }

    private int getExpirationFromLine(String line) {

        int index = line.lastIndexOf(' ');
        if (index >= 0) {

            return Integer.parseInt(line.substring(index + 1));
        } else {

            return 0;
        }
    }

    private String getFilenameFromLine(String line) {

        int index = line.lastIndexOf(' ');
        if (index >= 0) {

            return line.substring(0, index);
        } else {

            return "";
        }
    }

    private void updateJournal(Map<String, Integer> data) {

        try {

            PrintWriter writer = new PrintWriter(new BufferedOutputStream(new FileOutputStream(mJournal, false)));
            for (CacheFile file : mStack) {

                int expiration = data.containsKey(file.getName()) ? data.get(file.getName()) : 0;

                writer.println(file.getName() + " " + expiration);
            }

            writer.close();

        } catch (FileNotFoundException e) {

            Log.e("__CACHE__", "Can't write on the journal");
        }
    }

    private void cleanDirectory() {

        String[] filesList = mDirectory.list();
        if (filesList != null) {
            for (String filename : filesList) {
                CacheFile item = searchFileInCache(filename);

                if (!item.getName().equals(mJournal.getName()) && !mStack.contains(item) && !item.delete()) {
                    Log.e("__CACHE__", "Error when trying to delete a file in the cache: " + item.getPath());
                }
            }
        } else {

            Log.e("__CACHE__", "Can't clean the cache directory");
        }
    }
}
