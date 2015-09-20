package ch.watched.android.constants;

import android.util.Log;
import ch.watched.android.service.MessageParser;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by gaylor on 09/20/2015.
 * General useful functions
 */
public class Utils {

    public static byte[] getBytes(Object object) {

        byte[] bytes;

        String json = MessageParser.toJson(object);

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            ObjectOutputStream stream = new ObjectOutputStream(bos);
            stream.writeObject(json);

            bytes = bos.toByteArray();

        } catch (IOException e) {

            Log.e("--SERIALIZATION--", "Can't serialize an object: " + e.getLocalizedMessage());
            bytes = new byte[0];
        }

        return bytes;
    }

    public static <T> T getObject(byte[] bytes, Type type) {

        try (ByteArrayInputStream ios = new ByteArrayInputStream(bytes)) {

            ObjectInput stream = new ObjectInputStream(ios);
            String json = (String) stream.readObject();

            return MessageParser.fromJson(json, type);

        } catch (IOException | ClassNotFoundException e) {

            Log.e("--SERIALIZATION--", "Can't deserialize an object: " + e.getLocalizedMessage());

            return null;
        }
    }

    public static <T> String join(Collection<T> collection) {
        StringBuilder builder = new StringBuilder();

        Iterator<T> it = collection.iterator();
        while (it.hasNext()) {
            builder.append(it.next().toString());

            if (it.hasNext()) {
                builder.append(", ");
            }
        }

        return builder.toString();
    }
}
