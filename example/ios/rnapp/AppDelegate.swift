import Foundation
import UIKit

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
  
  var window: UIWindow?
  
  func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey : Any]? = nil) -> Bool{
    
    //Create default worker on port 8082
    RNWorkersManager.sharedInstance().initWorker()
    
    let jsCodeLocation = RCTBundleURLProvider.sharedSettings().jsBundleURL(forBundleRoot: "index.ios",
                                                                        fallbackResource: "main")
    
    let rootView = RCTRootView.init(bundleURL: jsCodeLocation, moduleName: "rnapp", initialProperties: nil, launchOptions: launchOptions)
    rootView?.backgroundColor = UIColor.init(colorLiteralRed: 1, green: 1, blue: 1, alpha: 1)
    
    
    let rootViewController = UIViewController()
    rootViewController.view = rootView
    
    RNWorkersManager.sharedInstance().startWorkers(with: rootView)
    
    self.window = UIWindow.init(frame: UIScreen.main.bounds)
    self.window!.rootViewController = rootViewController
    self.window!.makeKeyAndVisible()
    
    return true
  }
}

