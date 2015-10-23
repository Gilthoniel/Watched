package ch.watched.android.constants;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import ch.watched.android.service.MessageParser;

import java.io.*;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by gaylor on 09/20/2015.
 * General useful functions
 */
public class Utils {

    private static final SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.US);

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

    public static ProgressDialog createProgressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setTitle("Please wait...");
        dialog.setMessage("Downloading data...");
        dialog.setCancelable(false);

        return dialog;
    }

    public static void setLocaleDate(Locale locale) {
        dateFormatter = new SimpleDateFormat("dd MMM yyyy", locale);
    }

    public static String parseDate(String date) {
        if (date == null) {
            return "";
        }

        try {
            return dateFormatter.format(dateParser.parse(date));
        } catch (ParseException e) {
            return "";
        }
    }
}
