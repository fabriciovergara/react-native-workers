
## Extra Features

All extra features must be defined before creating any worker.

### Simulation
   
With Simulation enabled no aditional worker will started and the worker code on app main process.
To make it works you need to load the workers entry point when enabled.
You don't need to start any aditional packager using this option.

```java
    public class MainApplication extends Application implements ReactApplication {
    
        @Override
        public void onCreate() {
            super.onCreate();
            SoLoader.init(this, /* native exopackage */ false);
            RNWorkersManager.getInstance().setSimulationEnabled(true);
            RNWorkersManager.getInstance().init(this, BuildConfig.DEBUG);
        }
    }
```

```swift
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions:       [UIApplicationLaunchOptionsKey : Any]? = nil) -> Bool{
      RNWorkersManager.sharedInstance().simulationEnabled = true
      RNWorkersManager.sharedInstance().initWorker()  
      
        (...)
      }
```

```javascript 
   
    import { Worker, isSimulationEnabled } from 'rn-workers';

    if (isSimulationEnabled) {
        /* eslint-disable global-require */
        require('../../../index.worker');
        /* eslint-enable global-require */
    }
 ```
 


### PreferResouce (Not supported on iOS)
    
With PreferResource enabled the library will try to load a pre generated worker jsbundle from given resource name.
You don't need to start any aditional packager using this option.

```java
    public class MainApplication extends Application implements ReactApplication {
    
        @Override
        public void onCreate() {
            super.onCreate();
            SoLoader.init(this, /* native exopackage */ false);
            RNWorkersManager.getInstance().setPreferResourceEnabled(true);
            RNWorkersManager.getInstance().init(this, BuildConfig.DEBUG);
        }
    }
```
  
