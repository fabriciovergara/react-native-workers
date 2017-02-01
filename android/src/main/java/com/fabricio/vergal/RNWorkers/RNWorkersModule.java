package com.fabricio.vergal.RNWorkers;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;


public class RNWorkersModule extends ReactContextBaseJavaModule {

    public RNWorkersModule(final ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "RNWorkers";
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("simulationEnabled", RNWorkersManager.getInstance().isSimulationEnabled());
        return constants;
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
