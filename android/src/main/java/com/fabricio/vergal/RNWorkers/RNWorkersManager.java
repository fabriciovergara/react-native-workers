package com.fabricio.vergal.RNWorkers;

import android.app.Activity;
import android.app.Application;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager.ReactInstanceEventListener;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RNWorkersManager {

    private static RNWorkersManager sInstance;
    private ReactNativeHost mMainHost;
    private ReactApplicationContext mMainReactContext;
    private boolean mSimulationEnabled = false;
    private final ReactInstanceEventListener mMainHostListener = new ReactInstanceEventListener() {
        @Override
        public void onReactContextInitialized(ReactContext context) {
            mMainReactContext = (ReactApplicationContext) context;
        }
    };
    private List<RNWorker> mRNWorkers;
    private final SimpleLifecycleCallbacks mLifecycleCallbacks = new SimpleLifecycleCallbacks() {
        @Override
        public void onActivityDestroyed(Activity activity) {
            mMainHost.getReactInstanceManager().removeReactInstanceEventListener(mMainHostListener);
            for (final RNWorker worker : mRNWorkers) {
                worker.stop();
            }
        }
    };

    public static RNWorkersManager getInstance() {
        if (sInstance == null) {
            sInstance = new RNWorkersManager();
        }

        return sInstance;
    }

    public <T extends Application & ReactApplication> void init(
            final T reactApp, final RNWorker... workers) {
        mMainHost = reactApp.getReactNativeHost();
        mRNWorkers = new ArrayList<>();
        if (workers != null && workers.length > 0) {
            mRNWorkers.addAll(Arrays.asList(workers));
        }

        reactApp.registerActivityLifecycleCallbacks(mLifecycleCallbacks);
    }

    public <T extends Application & ReactApplication> void init(
            final T reactApp, boolean developerSupport) {
        init(reactApp, RNWorker.createDefault(reactApp, developerSupport));
    }


    public void startWorkers() {
        if (mSimulationEnabled) {
            return;
        }

        mMainHost.getReactInstanceManager().addReactInstanceEventListener(mMainHostListener);
        for (final RNWorker worker : mRNWorkers) {
            worker.start();
        }
    }

    public ReactApplicationContext getMainReactContext() {
        return mMainReactContext;
    }

    public ReactApplicationContext getWorkerReactContext(int port) {
        if (mSimulationEnabled) {
            return mMainReactContext;
        }


        for (final RNWorker worker : mRNWorkers) {
            if (worker.getPort() == port) {
                return worker.getReactContext();
            }
        }

        return null;
    }

    public boolean isSimulationEnabled() {
        return mSimulationEnabled;
    }

    public void setSimulationEnabled(boolean enabled) {
        mSimulationEnabled = enabled;
    }
}
