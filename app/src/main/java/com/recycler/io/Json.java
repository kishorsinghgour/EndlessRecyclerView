package com.recycler.io;

/**
 * Created by bvg on 21/01/2016.
 */


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.lang.reflect.Type;

public class Json {

    private static GsonBuilder getGsonBuilder(TypeDeserializer... typeDeserializers) {
        GsonBuilder builder = new GsonBuilder();
        if (typeDeserializers != null) {
            for (TypeDeserializer typeDeserializer : typeDeserializers) {
                builder.registerTypeAdapter(typeDeserializer.getType(), typeDeserializer.getDeserializer());
            }
        }
        builder.setDateFormat("MMM dd yyyy HH:mm");
        return builder;
    }

    private static Gson getGson(TypeDeserializer... typeDeserializers) {
        return getGsonBuilder(typeDeserializers).create();
    }

    public static String stringify(Object src) {
        return getGson().toJson(src);
    }

    public static <T> T parse(String json, Class<T> type, TypeDeserializer... typeDeserializers) {
        return getGson(typeDeserializers).fromJson(json, type);
    }

    public abstract static class TypeDeserializer {
        private Type type;
        private JsonDeserializer<?> deserializer;

        public TypeDeserializer(Type type, JsonDeserializer<?> deserializer) {
            this.type = type;
            this.deserializer = deserializer;
        }

        public Type getType() {
            return type;
        }

        public JsonDeserializer<?> getDeserializer() {
            return deserializer;
        }
    }



}
