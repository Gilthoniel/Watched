package ch.watched.android.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * Created by gaylor on 26.06.15.
 * Parse json to Java class and inversely
 */
public class MessageParser {

    private static MessageParser instance = new MessageParser();
    private static Gson gson;

    private MessageParser() {
        GsonBuilder builder = new GsonBuilder();

        gson = builder.create();
    }

    public static <T> T fromJson(String json, Type type) throws JsonSyntaxException {

        return gson.fromJson(json, type);
    }

    public static <T> T fromJson(JsonElement json, Type type) throws JsonSyntaxException {

        return gson.fromJson(json, type);
    }

    public static <T> T fromJson(InputStreamReader reader, Type type)
            throws JsonSyntaxException, IOException
    {

        return gson.fromJson(reader, type);
    }

    public static String toJson(Object object) {

        return gson.toJson(object);
    }
}
