# Observation
 
  * To start the project you need to manually start a second Packager using port 8082
  * Release bundle packaging must be done manually
  
### iOS

  * Sometime you need to start and stop the debugger to fully reload the worker js
  * Start the app debugger will automatically start the worker debugger
  
### Android

  * You need to manually execute adb reverse tcp:8082 tcp:8082
  * Two DevMenu will apears on shake (one for app and other for worker)
