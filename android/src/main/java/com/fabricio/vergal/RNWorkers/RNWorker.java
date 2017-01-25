package com.fabricio.vergal.RNWorkers;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactInstanceManager.ReactInstanceEventListener;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.devsupport.RedBoxHandler;
import com.facebook.react.modules.debug.DeveloperSettings;
import com.facebook.react.shell.MainReactPackage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import static android.content.Context.MODE_PRIVATE;
import static com.fabricio.vergal.RNWorkers.RNWorkersUtils.getField;


public class RNWorker implements ReactInstanceEventListener {

    public static final String DEBUG_HOST_FORMAT = "localhost:%d";
    public static final String DEFAULT_JS_ENTRY_POINT = "index.worker";
    public static final String DEFAULT_JS_BUNDLE_ASSET = "index.worker.bundle";
    public static final String PREFS_DEBUG_SERVER_HOST_KEY = "debug_http_host";
    public static final int DEFAULT_WORKER_PORT = 8082;

    public static <T extends Application & ReactApplication> RNWorker createDefault(
            final T application, final boolean developerSupport) {
        return new Builder(application, developerSupport)
                .entryPoint(DEFAULT_JS_ENTRY_POINT)
                .bundleAsset(DEFAULT_JS_BUNDLE_ASSET)
                .port(DEFAULT_WORKER_PORT)
                .build();
    }

    private final ReactNativeHost mHost;
    private final int mPort;
    private ReactApplicationContext mReactContext;

    private RNWorker(final ReactNativeHost host, final int port) {
        mHost = host;
        mPort = port;
    }

    public void start() {
        mHost.getReactInstanceManager().addReactInstanceEventListener(this);
        if (!mHost.getReactInstanceManager().hasStartedCreatingInitialContext()) {
            mHost.getReactInstanceManager().createReactContextInBackground();
        }

        mHost.getReactInstanceManager().onHostResume(null, null);
    }

    public void stop() {
        mHost.getReactInstanceManager().onHostPause();
        mHost.getReactInstanceManager().removeReactInstanceEventListener(this);
        mHost.getReactInstanceManager().onHostDestroy();
    }

    public int getPort() {
        return mPort;
    }

    public ReactApplicationContext getReactContext() {
        return mReactContext;
    }

    @Override
    public void onReactContextInitialized(ReactContext context) {
        mReactContext = (ReactApplicationContext) context;
    }

    public static class Builder {

        private final Application mApplication;
        private final List<ReactPackage> mPackages;
        private RedBoxHandler mRedBoxHandler;
        private String mEntryPoint;
        private String mBundleAsset;
        private String mBundleFile;
        private boolean mDeveloperSupport;
        private int mPort;

        public Builder(final Application application, final boolean developerSupport) {
            mApplication = application;
            mDeveloperSupport = developerSupport;
            mPackages = new ArrayList<>();
            mEntryPoint = null;
            mBundleAsset = null;
            mBundleFile = null;
            mPort = 8082;
        }

        public Builder entryPoint(final String entryPoint) {
            mEntryPoint = entryPoint;
            return this;
        }

        public Builder bundleAsset(final String bundleAsset) {
            mBundleAsset = bundleAsset;
            return this;
        }


        public Builder bundleFile(final String bundleFile) {
            mBundleFile = bundleFile;
            return this;
        }

        public Builder redBoxHandler(final RedBoxHandler handler) {
            mRedBoxHandler = handler;
            return this;
        }


        public Builder port(final int port) {
            mPort = port;
            return this;
        }

        public Builder packages(final ReactPackage pkg, final ReactPackage... pkgs) {
            if (pkg != null) {
                mPackages.add(pkg);
            }

            if (pkgs != null && pkgs.length > 0) {
                mPackages.addAll(Arrays.asList(pkgs));
            }

            return this;
        }

        public Builder packages(final List<ReactPackage> pkgs) {
            if (pkgs != null && pkgs.size() > 0) {
                mPackages.addAll(pkgs);
            }

            return this;
        }

        public RNWorker build() {
            boolean containMainReactPackage = false;
            boolean containRNWorkerPackage = false;
            for (final ReactPackage pkg : mPackages) {
                if (pkg instanceof MainReactPackage) {
                    containMainReactPackage = true;
                } else if (pkg instanceof RNWorkersPackage) {
                    containRNWorkerPackage = true;
                }
            }

            if(!containMainReactPackage){
                mPackages.add(new MainReactPackage());
            }

            if(!containRNWorkerPackage){
                mPackages.add(new RNWorkersPackage());
            }

            final ReactNativeHost host = new ReactNativeHost(mApplication) {
                @Override
                protected boolean getUseDeveloperSupport() {
                    return mDeveloperSupport;
                }

                @Override
                protected List<ReactPackage> getPackages() {
                    return mPackages;
                }

                @Override
                protected String getJSMainModuleName() {
                    return mEntryPoint;
                }

                @Nullable
                @Override
                protected String getBundleAssetName() {
                    return mBundleAsset;
                }

                @Nullable
                @Override
                protected String getJSBundleFile() {
                    return mBundleFile;
                }

                @Nullable
                @Override
                protected RedBoxHandler getRedBoxHandler() {
                    return mRedBoxHandler;
                }
            };


            try {
                replacePrefs(mApplication, host, mPort);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            return new RNWorker(host, mPort);
        }

        private static void replacePrefs(final Context context,
                                         final ReactNativeHost host,
                                         final int port) throws IllegalAccessException {
            final String debugHost = String.format(DEBUG_HOST_FORMAT, port);
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

            editor.putString(PREFS_DEBUG_SERVER_HOST_KEY, debugHost);
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
            final OnSharedPreferenceChangeListener listener = (OnSharedPreferenceChangeListener) settings;
            newPreferences.registerOnSharedPreferenceChangeListener(listener);

        }
    }
}
