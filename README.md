# react-native-workers (RN 0.41^)
Do heavy data process outside of your UI JS thread.

Before using this kind of solution you should check if [InteractionManager.runAfterInteractions](https://facebook.github.io/react-native/docs/interactionmanager.html) is not enough for your needs, because creating a aditional worker can considerably increase app memory usage. 

### Automatic Instalation
```
npm install --save rn-workers
react-native link rn-workers
```

### Manual Instalation

#### iOS

1. In the XCode's "Project navigator", right click on your project's Libraries folder ➜ `Add Files to <...>`
2. Go to `node_modules` ➜ `rn-workers` ➜ `ios` ➜ select `RNWorkers.xcodeproj`
3. Add `RNWorkers.a` to `Build Phases -> Link Binary With Libraries`
4. Pray and try to compile

#### Android
1. Add the following lines to `android/settings.gradle`:

```gradle
    include ':rn-workers'
    project(':rn-workers').projectDir = new File(rootProject.projectDir, '../node_modules/rn-workers/android')
```

2. Add the compile line to the dependencies in `android/app/build.gradle`:

```gradle
    dependencies {
        compile project(':rn-workers')
    }
```
3. Add the import and link the package in `MainApplication.java`:

```java
   
    public class MainApplication extends Application implements ReactApplication {
        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                new MainReactPackage(),
                new RNWorkersPackage() // <-- add this line
            );
        }
    }
```
### Setup

#### iOS
    
```swift
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions:       [UIApplicationLaunchOptionsKey : Any]? = nil) -> Bool{
      //CRITICAL: Must be initialized before creation of rootView to be possible to debug on chrome console
      //Initialize using default worker
      RNWorkersManager.sharedInstance().initWorker()  
      
      //If you want to use the default worker and an adicional one
      RNWorkersManager.sharedInstance().initWorker()
      RNWorkersManager.sharedInstance().initWorker(withPort: 8083, bundleRoot: "index.worker2", fallbackResouce: "worker2")
    
      let jsCodeLocation = RCTBundleURLProvider.sharedSettings().jsBundleURL(forBundleRoot: "index.ios",
                                                                          fallbackResource: "main")

      let rootView = RCTRootView.init(bundleURL: jsCodeLocation, moduleName: "rnapp", initialProperties: nil, launchOptions: launchOptions)
      rootView?.backgroundColor = UIColor.init(colorLiteralRed: 1, green: 1, blue: 1, alpha: 1)

      let rootViewController = UIViewController()
      rootViewController.view = rootView

      //Pass rootView referece
      RNWorkersManager.sharedInstance().startWorkers(with: rootView)

      self.window = UIWindow.init(frame: UIScreen.main.bounds)
      self.window!.rootViewController = rootViewController
      self.window!.makeKeyAndVisible()

      return true
    }
```

#### Android

```java
    public class MainApplication extends Application implements ReactApplication {
    
        (...)

        @Override
        public void onCreate() {
            super.onCreate();
            SoLoader.init(this, /* native exopackage */ false);
            
            //Initialize using default worker
            RNWorkersManager.getInstance().init(this, BuildConfig.DEBUG);
            
           
            //If you want to use the default worker and an adicional one
            final RNWorker worker1 = RNWorker.createDefault(this, BuildConfig.DEBUG);
            final RNWorker worker2 = new RNWorker.Builder(this, BuildConfig.DEBUG)
                .entryPoint("index.worker2")
                .bundleAsset("index.worker2.bundle")
                .port(8082)
                .packages(new CustomPackage())
                .build();

            RNWorkersManager.getInstance().init(this, worker1, worker2);
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

#### Javascript
  
  1. Create a index.worker.js in the react-native root project (same level of index.ios.js and index.android.js)
  2. import a worker.jsbundle in iOS Project and index.worker.jsbundle  on Android Project
  
## Usage

### App side

```javascript 
   
    import { Worker } from 'rn-workers'

    export default class rnapp extends React.Component {

        constructor (props, context) {
            super(props, context)
        }

        componentDidMount () {
            //Create using default worker port (8082)
            this.worker = new Worker();
            
            //Create worker pointing to custom one
            this.worker2 = new Worker(8083);
            
            
            //Add listener to receve messages
            this.worker.onmessage = message => this.setState({
                 text: message,
                 count: this.state.count + 1
            });

            //Send message to worker (Only strings is allowed for now)
            this.worker.postMessage("Hey Worker!")
        }

        componentWillUnmount () {
            //Terminate worker
            this.worker.terminate();
        }
        
        (...)
     }
 ```
 
### Worker side

```javascript 
   
    import { WorkerService } from 'rn-workers'
    
    const worker = new WorkerService();
    worker.onmessage = message => {
        //Reply the message back to app
        worker.postMessage("Hello from the other side (" + message + ")")
    };

 ```
 
# Observation
 
  * To start the project you need to manually start a second Packager using port 8082
  * Release bundle packaging must be done manually
  
### iOS

  * Sometime you need to start and stop the debugger to fully reload the worker js
  * Start the app debugger will automatically start the worker debugger
  
### Android

  * You need to manually execute adb reverse tcp:8082 tcp:8082
  * Two DevMenu will apears on shake (one for app and other for worker)
  
# Scripts

### Release packing
  
  * iOS:
  ```
  react-native bundle --dev false --assets-dest ./ios --entry-file index.worker.js --platform ios --bundle-output ./ios/worker.jsbundle
```

  * Android:
  ```
  react-native bundle --dev false --assets-dest ./android/app/src/main/res/ --entry-file index.worker.js --platform android  --bundle-output ./android/app/src/main/assets/index.worker.bundle
``` 
 
# License
~~Cancer~~ GPL ..... just kindind, its Apache 2.0
