package com.fabricio.vergal.RNWorkers;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager.ReactInstanceEventListener;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.debug.DeveloperSettings;
import com.facebook.react.shell.MainReactPackage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class RNWorkersManager implements Application.ActivityLifecycleCallbacks {

    private static final String DEFAULT_DEBUG_HOST = "localhost:8082";
    private static final String DEFAULT_COMPONENT_NAME = "worker";
    private static final String DEFAULT_JS_ENTRY_POINT = "index.worker";
    private static final String DEFAULT_JS_BUNDLE_NAME = "index.worker.bundle";
    private static final String PREFS_DEBUG_SERVER_HOST_KEY = "debug_http_host";

    private static RNWorkersManager sInstance;

    private final ReactNativeHost mReactNativeMainHost;
    private final ReactNativeHost mReactNativeHost;
    private final Application mApplication;
    private final String mDebugHost;
    private ReactApplicationContext mWorkerReactContext;
    private ReactApplicationContext mMainReactContext;

    final ReactInstanceEventListener mWorkerInstanceListener = new ReactInstanceEventListener() {
        @Override
        public void onReactContextInitialized(ReactContext context) {
            mWorkerReactContext = (ReactApplicationContext) context;
        }
    };

    final ReactInstanceEventListener mMainInstanceListener = new ReactInstanceEventListener() {
        @Override
        public void onReactContextInitialized(ReactContext context) {
            mMainReactContext = (ReactApplicationContext) context;
        }
    };

    private static RNWorkersManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("RNWorkersManager not initialized yet");
        }

        return sInstance;
    }

    public static void init(final ReactApplication reactApplication, final boolean useDeveloperSupport,
                            final ReactPackage... packages) {
        init(reactApplication, createDefaultWorkerHost((Application) reactApplication, useDeveloperSupport, packages),
                DEFAULT_DEBUG_HOST);
    }

    public static void init(final ReactApplication reactApplication, final ReactNativeHost workerHost, final String debugHost) {
        if (sInstance == null) {
            sInstance = new RNWorkersManager((Application) reactApplication,
                    reactApplication.getReactNativeHost(), workerHost, debugHost);
        }
    }

    public static void start() {
        start(DEFAULT_COMPONENT_NAME);
    }

    public static void start(final String componentName) {
        getInstance().startWorker(componentName);
    }

    private static ReactNativeHost createDefaultWorkerHost(final Application application,
                                                           final boolean useDeveloperSupport,
                                                           final ReactPackage... packages) {
        return new ReactNativeHost(application) {
            @Override
            protected boolean getUseDeveloperSupport() {
                return useDeveloperSupport;
            }

            @Override
            protected List<ReactPackage> getPackages() {
                final ArrayList<ReactPackage> list = new ArrayList<>();
                list.add(new MainReactPackage());
                list.add(new RNWorkersPackage());

                if (packages != null) {
                    for (final ReactPackage pkg : packages) {
                        if (!(pkg instanceof RNWorkersPackage)) {
                            list.add(pkg);
                        }
                    }
                }

                return list;
            }

            @Override
            protected String getJSMainModuleName() {
                return DEFAULT_JS_ENTRY_POINT;
            }

            @Nullable
            protected String getBundleAssetName() {
                return DEFAULT_JS_BUNDLE_NAME;
            }
        };
    }

    static ReactApplicationContext getMainReactContext() {
        return getInstance().mMainReactContext;
    }

    static ReactApplicationContext getWorkerReactContext() {
        return getInstance().mWorkerReactContext;
    }

    private RNWorkersManager(final Application application,
                             final ReactNativeHost mainHost,
                             final ReactNativeHost workerHost,
                             final String debugHost) {
        mReactNativeMainHost = mainHost;
        mApplication = application;
        mDebugHost = debugHost;
        mReactNativeHost = workerHost;
        mApplication.registerActivityLifecycleCallbacks(this);
    }

    private void startWorker(final String componentName) {
        mReactNativeHost.getReactInstanceManager().addReactInstanceEventListener(mWorkerInstanceListener);
        mReactNativeMainHost.getReactInstanceManager().addReactInstanceEventListener(mMainInstanceListener);

        if (!mReactNativeMainHost.getReactInstanceManager().hasStartedCreatingInitialContext()) {
            mReactNativeMainHost.getReactInstanceManager().createReactContextInBackground();
        }

        mReactNativeHost.getReactInstanceManager().onHostResume(null, null);

        final DeveloperSettings settings = mReactNativeHost.getReactInstanceManager().getDevSupportManager().getDevSettings();
        final Field sharedPreferenceField = RNWorkersUtils.getField(settings, SharedPreferences.class);

        try {
            sharedPreferenceField.setAccessible(true);
            final SharedPreferences preferences = (SharedPreferences) sharedPreferenceField.get(settings);
            final SharedPreferences newPreferences = mApplication.getSharedPreferences(mDebugHost, Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = newPreferences.edit();
            for (final Map.Entry<String, ?> entry : preferences.getAll().entrySet()) {
                RNWorkersUtils.putObject(editor, entry.getKey(), entry.getValue());
            }

            editor.putString(PREFS_DEBUG_SERVER_HOST_KEY, mDebugHost);
            editor.apply();

            preferences.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String keyChanged) {
                    final SharedPreferences.Editor editor = newPreferences.edit();
                    for (final Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
                        if (!entry.getKey().contains(PREFS_DEBUG_SERVER_HOST_KEY)) {
                            RNWorkersUtils.putObject(editor, entry.getKey(), entry.getValue());
                        }
                    }

                    editor.apply();
                }
            });

            sharedPreferenceField.set(settings, newPreferences);
            newPreferences.registerOnSharedPreferenceChangeListener((OnSharedPreferenceChangeListener) settings);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        if (!(activity instanceof ReactActivity)) {
            return;
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (!(activity instanceof ReactActivity)) {
            return;
        }

        mReactNativeHost.getReactInstanceManager().onHostPause(null);
        mReactNativeHost.getReactInstanceManager().removeReactInstanceEventListener(mWorkerInstanceListener);
        mReactNativeMainHost.getReactInstanceManager().removeReactInstanceEventListener(mMainInstanceListener);
        mReactNativeHost.getReactInstanceManager().onHostDestroy(activity);
    }
}
