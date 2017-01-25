package com.fabricio.vergal.RNWorkers;

import android.content.SharedPreferences;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

public class RNWorkersUtils {

    static <T> Field getField(final T object, final Class<?> type) {
        final Class<?> clazz = object.getClass();
        final Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getType().equals(type)) {
                return fields[i];
            }
        }

        return null;

    }

    static <T> Field getField(final T object, final String name) {
        final Class<?> clazz = object.getClass();
        final Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().equals(name)) {
                return fields[i];
            }
        }

        return null;
    }

    static <T> Method getMethod(final T object, final String name) {
        final Class<?> clazz = object.getClass();
        final Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(name)) {
                return methods[i];
            }
        }

        return null;
    }


    static void putObject(final SharedPreferences.Editor editor, final String key,
                          final Object value) {
        if (value == null) {
            return;
        }

        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Set) {
            editor.putStringSet(key, (Set<String>) value);
        }
    }

}
