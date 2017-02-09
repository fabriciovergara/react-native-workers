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

#### Javascript
  
  1. Create a index.worker.js in the react-native root project (same level of index.ios.js and index.android.js)
  2. import a worker.jsbundle in iOS Project and index.worker.jsbundle  on Android Project
