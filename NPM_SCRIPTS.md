# NPM Scripts


  
```json
    "start-app": "node node_modules/react-native/local-cli/cli.js start --reset-cache",
    
    "start-worker": "node node_modules/react-native/local-cli/cli.js start --port 8082 --reset-cache",
    
    "start": "concurrently --kill-others \"npm run start-app\" \"npm run start-worker\"",
    
    "adb-reverse": "adb reverse tcp:8081 tcp:8081 && adb reverse tcp:8082 tcp:8082",
    
    "bundle-app-ios": "node node_modules/react-native/local-cli/cli.js bundle --dev false --assets-dest ./ios --entry-file index.ios.js --platform ios --bundle-output ./ios/main.jsbundle --sourcemap-output ./sourcemap/ios.main.map",
    
    "bundle-app-android": "node node_modules/react-native/local-cli/cli.js bundle --dev false --assets-dest ./android/app/src/main/res/ --entry-file index.android.js --platform android  --bundle-output ./android/app/src/main/assets/index.android.bundle --sourcemap-output ./sourcemap/android.main.map",
    
    "bundle-worker-ios": "node node_modules/react-native/local-cli/cli.js bundle --dev false --assets-dest ./ios --entry-file index.worker.js --platform ios --bundle-output ./ios/worker.jsbundle --sourcemap-output ./sourcemap/ios.worker.map",
    
    "bundle-worker-android": "node node_modules/react-native/local-cli/cli.js bundle --dev false --assets-dest ./android/app/src/main/res/ --entry-file index.worker.js --platform android  --bundle-output ./android/app/src/main/assets/index.worker.bundle --sourcemap-output ./sourcemap/android.worker.map",
    
    "bundle-ios": "npm run bundle-app-ios && npm run bundle-worker-ios",
    
    "bundle-android": "npm run bundle-app-android && npm run bundle-worker-android",
    
    "bundle": "concurrently --kill-others \"npm run bundle-ios\" \"run bundle-android\"
``` 
