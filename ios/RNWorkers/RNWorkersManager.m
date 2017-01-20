#import "RNWorkersManager.h"

@implementation RNWorkersManager
{
  RCTBridge *_workerBridge;
  RCTBridge *_mainBridge;
}

+ (instancetype)sharedInstance
{
  static RNWorkersManager *instance;
  static dispatch_once_t once_token;
  dispatch_once(&once_token, ^{
    instance = [RNWorkersManager new];
  });
  return instance;
}

- (void) initWorkerWithBundleRoot: (NSString*) path
                  fallbackResouce:(NSString*) resource
                       moduleName:(NSString*) name
{
  NSURL *jsCodeLocation = [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:path fallbackResource:resource];
  
  if(RCT_DEBUG && jsCodeLocation != nil){
    NSString *portString = [NSString stringWithFormat:@"%d", 8082];
    NSString *path = [jsCodeLocation.absoluteString stringByReplacingOccurrencesOfString:@"8081" withString:portString];
    jsCodeLocation = [[NSURL alloc] initWithString:path];
  }
  
    _workerBridge = [[RCTBridge alloc] initWithBundleURL:jsCodeLocation
                                          moduleProvider:nil
                                           launchOptions:nil];
}

- (void) startWithRootView: (RCTRootView*) rootView
{
  _mainBridge = rootView.bridge;
}

@end
