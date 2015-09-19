package ch.watched.android.service.utils;

import java.io.File;
import java.net.URI;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by gaylor on 05-Aug-15.
 * Normal file implementation with a ReadWriteLock to manage the concurrency of the cache
 */
public class CacheFile extends File {

    private ReentrantReadWriteLock lock;

    public CacheFile(File dir, String name) {
        super(dir, name);

        lock = new ReentrantReadWriteLock();
    }

    public CacheFile(String path) {
        super(path);

        lock = new ReentrantReadWriteLock();
    }

    public CacheFile(String dirPath, String name) {
        super(dirPath, name);

        lock = new ReentrantReadWriteLock();
    }

    public CacheFile(URI uri) {
        super(uri);

        lock = new ReentrantReadWriteLock();
    }

    public ReentrantReadWriteLock getLock() {
        return lock;
    }
}
