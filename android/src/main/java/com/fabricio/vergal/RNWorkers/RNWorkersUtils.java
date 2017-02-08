package com.fabricio.vergal.RNWorkers;

import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.modules.debug.DeveloperSettings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

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

    protected static void replacePrefs(final Context context,
                                       final ReactNativeHost host,
                                       final int port,
                                       final String debugHostFormat,
                                       final String prefsDebugServerHostKey) throws IllegalAccessException {

        final String debugHost = String.format(debugHostFormat, port);
        final ReactInstanceManager manager = host.getReactInstanceManager();
        final DeveloperSettings settings = manager.getDevSupportManager().getDevSettings();
        final Field sharedPreferenceField = getField(settings, SharedPreferences.class);

        sharedPreferenceField.setAccessible(true);
        final SharedPreferences preferences = (SharedPreferences) sharedPreferenceField.get(settings);
        final SharedPreferences newPreferences = context.getSharedPreferences(debugHost, MODE_PRIVATE);
        final SharedPreferences.Editor editor = newPreferences.edit();

        for (final Map.Entry<String, ?> entry : preferences.getAll().entrySet()) {
            RNWorkersUtils.putObject(editor, entry.getKey(), entry.getValue());
        }

        editor.putString(prefsDebugServerHostKey, debugHost);
        editor.apply();

        preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String keyChanged) {
                final SharedPreferences.Editor editor = newPreferences.edit();
                for (final Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
                    if (!entry.getKey().contains(prefsDebugServerHostKey)) {
                        RNWorkersUtils.putObject(editor, entry.getKey(), entry.getValue());
                    }
                }

                editor.apply();
            }
        });

        sharedPreferenceField.set(settings, newPreferences);
        final SharedPreferences.OnSharedPreferenceChangeListener listener = (SharedPreferences.OnSharedPreferenceChangeListener) settings;
        newPreferences.registerOnSharedPreferenceChangeListener(listener);

    }

}
