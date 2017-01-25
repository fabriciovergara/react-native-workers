package com.fabricio.vergal.RNWorkers;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;


public class RNWorkersModule extends ReactContextBaseJavaModule {

    public RNWorkersModule(final ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "RNWorkers";
    }

    @ReactMethod
    public void sendMessageToWorker(final int port, final String message) {
        final ReactApplicationContext context = RNWorkersManager.getInstance().getWorkerReactContext(port);
        if (context == null) {
            return;
        }

        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("RNWorkers", message);
    }

    @ReactMethod
    public void sendMessageToApp(final String message) {
        final ReactApplicationContext context = RNWorkersManager.getInstance().getMainReactContext();
        if (context == null) {
            return;
        }

        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("RNWorkersApp", message);
    }
}
