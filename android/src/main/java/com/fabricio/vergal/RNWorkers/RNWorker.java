package com.fabricio.vergal.RNWorkers;

import android.app.Application;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager.ReactInstanceEventListener;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.devsupport.RedBoxHandler;
import com.facebook.react.shell.MainReactPackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import static com.fabricio.vergal.RNWorkers.RNWorkersUtils.replacePrefs;


public class RNWorker implements ReactInstanceEventListener {

    public static final String DEBUG_HOST_FORMAT = "localhost:%d";
    public static final String DEFAULT_JS_ENTRY_POINT = "index.worker";
    public static final String DEFAULT_JS_BUNDLE_ASSET = "index.worker.bundle";
    public static final String PREFS_DEBUG_SERVER_HOST_KEY = "debug_http_host";
    public static final int DEFAULT_WORKER_PORT = 8082;

    private final Application mApplication;
    private final int mPort;
    private final String mEntryPoint;
    private final String mBundleAsset;
    private final String mBundleFile;
    private final RedBoxHandler mRedBoxHandler;
    private final List<ReactPackage> mPackages;
    private final boolean mDeveloperSupport;

    private ReactApplicationContext mReactContext;
    private ReactNativeHost mHost;

    public RNWorker(final Application application,
                    final int port,
                    final String entryPoint,
                    final String bundleAsset,
                    final String bundleFile,
                    final RedBoxHandler redBoxHandler,
                    final List<ReactPackage> packages,
                    final boolean developerSupport) {
        mApplication = application;
        mPort = port;
        mEntryPoint = entryPoint;
        mBundleAsset = bundleAsset;
        mBundleFile = bundleFile;
        mRedBoxHandler = redBoxHandler;
        mPackages = packages;
        mDeveloperSupport = developerSupport;
    }

    public static <T extends Application & ReactApplication> RNWorker createDefault(
                                                                                    final T application, final boolean developerSupport) {
        return new Builder(application, developerSupport)
        .entryPoint(DEFAULT_JS_ENTRY_POINT)
        .bundleAsset(DEFAULT_JS_BUNDLE_ASSET)
        .port(DEFAULT_WORKER_PORT)
        .build();
    }


    public void start(final boolean preferResource) {
        createHost(preferResource);
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

    private void createHost(final boolean preferResource) {
        if (mHost == null) {
            if (preferResource && mBundleAsset == null && mBundleFile == null) {
                throw new RuntimeException("preferResource is enabled but no resource was provided");
            }

            mHost = new ReactNativeHost(mApplication) {
                @Override
                public boolean getUseDeveloperSupport() {
                    return preferResource ? false : mDeveloperSupport;
                }

                @Override
                protected List<ReactPackage> getPackages() {
                    return mPackages;
                }

                @Override
                protected String getJSMainModuleName() {
                    return preferResource ? null : mEntryPoint;
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


            if (!preferResource && mDeveloperSupport) {
                try {
                    replacePrefs(mApplication, mHost, mPort, DEBUG_HOST_FORMAT,
                                 PREFS_DEBUG_SERVER_HOST_KEY);
                } catch (Exception e) {
                }
            }

        }
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

            if (!containMainReactPackage) {
                mPackages.add(new MainReactPackage());
            }

            if (!containRNWorkerPackage) {
                mPackages.add(new RNWorkersPackage());
            }

            return new RNWorker(mApplication, mPort, mEntryPoint, mBundleAsset, mBundleFile,
                                mRedBoxHandler, mPackages, mDeveloperSupport);
        }
    }
}