#import "RNWorkersManager.h"

@implementation RNWorkersManager
{
  RCTRootView *view;
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
  
  view = [[RCTRootView alloc] initWithBundleURL:jsCodeLocation moduleName:name initialProperties:nil launchOptions:nil];
  view.frame = CGRectMake(0, 0, 0, 0);
}

- (void) startWithRootView: (RCTRootView*) rootView
{
  _workerBridge = view.bridge;
  _mainBridge = rootView.bridge;
}

@end
