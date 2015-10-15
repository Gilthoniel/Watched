package ch.watched.android.service;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import ch.watched.android.database.DatabaseService;
import ch.watched.android.database.WatcherDbHelper;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * Created by Gaylor on 15.10.2015.
 * Backup agent to save the watched medias
 */
public class BaseBackupAgent extends BackupAgent {

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState)
            throws IOException
    {
        synchronized (DatabaseService.getInstance().getLock()) {
            File database = new File(Environment.getDataDirectory(), "/data/ch.watched/databases/" + WatcherDbHelper.DATABASE_NAME);
            DataInputStream dataStream = new DataInputStream(new FileInputStream(oldState.getFileDescriptor()));

            long lastModified;
            try {
                lastModified = dataStream.readLong();
            } catch (IOException e) {
                lastModified = -1;
            }

            if (lastModified != database.lastModified()) {

                FileInputStream stream = new FileInputStream(database);
                byte[] bytes = IOUtils.toByteArray(stream);

                data.writeEntityHeader("DATABASE_BACKUP", bytes.length);
                data.writeEntityData(bytes, bytes.length);

                DataOutputStream output = new DataOutputStream(new FileOutputStream(newState.getFileDescriptor()));
                output.writeLong(database.lastModified());
                output.close();

                Log.d("--BACKUP--", "Backup successfully ended");
            }
        }
    }

    @Override
    public void onRestore(BackupDataInput data, int version, ParcelFileDescriptor newState) throws IOException {
        synchronized (DatabaseService.getInstance().getLock()) {
            while (data.readNextHeader()) {

                if (data.getKey().equals("DATABASE_BACKUP")) {
                    byte[] bytes = new byte[data.getDataSize()];
                    data.readEntityData(bytes, 0, bytes.length);

                    DatabaseService.getInstance().close();

                    File dbDirectory = new File(Environment.getDataDirectory(), "/data/ch.watched/databases");
                    dbDirectory.mkdirs();
                    File database = new File(dbDirectory, WatcherDbHelper.DATABASE_NAME);

                    FileOutputStream stream = new FileOutputStream(database);
                    stream.write(bytes, 0, bytes.length);
                    stream.flush();
                    stream.close();

                    DataOutputStream output = new DataOutputStream(new FileOutputStream(newState.getFileDescriptor()));
                    output.writeLong(database.lastModified());
                    output.close();

                    DatabaseService.getInstance().initHelper(getApplicationContext());

                    Log.d("--BACKUP--", "Restore successfully ended");
                }
            }
        }
    }
}
