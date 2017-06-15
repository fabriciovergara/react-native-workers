# Setup

## iOS
    
```swift
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions:       [UIApplicationLaunchOptionsKey : Any]? = nil) -> Bool{
      //CRITICAL: Must be initialized before creation of rootView to be possible to debug on chrome console
      RNWorkersManager.sharedInstance().initWorker()

      // Objective C equivalent
      // [[RNWorkersManager sharedInstance] initWorker];

      let jsCodeLocation = RCTBundleURLProvider.sharedSettings().jsBundleURL(forBundleRoot: "index.ios",
                                                                          fallbackResource: "main")

      let rootView = RCTRootView.init(bundleURL: jsCodeLocation, moduleName: "rnapp", initialProperties: nil, launchOptions: launchOptions)
      rootView?.backgroundColor = UIColor.init(colorLiteralRed: 1, green: 1, blue: 1, alpha: 1)

      let rootViewController = UIViewController()
      rootViewController.view = rootView

      //Pass rootView referece
      RNWorkersManager.sharedInstance().start(with: rootView) 
      
      // Objective C equivalent
      // [[RNWorkersManager sharedInstance] startWorkersWithRootView:rootView];

      self.window = UIWindow.init(frame: UIScreen.main.bounds)
      self.window!.rootViewController = rootViewController
      self.window!.makeKeyAndVisible()

      return true
    }
```

## Android

```java
    public class MainApplication extends Application implements ReactApplication {
    
        (...)

        @Override
        public void onCreate() {
            super.onCreate();
            SoLoader.init(this, /* native exopackage */ false);
            //Initialize Manager instance
            RNWorkersManager.getInstance().init(this, BuildConfig.DEBUG);
        }
    }
```

```java
  public class MainActivity extends ReactActivity {
      
      (...)
      
      @Override
      protected void onCreate(Bundle savedInstanceState) {       
        //CRITICAL: Must be started before super.onCreate to be possible to debug on chrome console
        RNWorkersManager.getInstance().startWorkers();
        super.onCreate(savedInstanceState);
      }
  }
```

## Javascript
  
  1. Create a index.worker.js in the react-native root project (same level of index.ios.js and index.android.js)
  2. import a worker.jsbundle in iOS Project and index.worker.jsbundle  on Android Project
 
