#import "RNWorkersManager.h"

@implementation RNWorkersManager

@synthesize mainBridge;
@synthesize workerDictionary;


+ (instancetype)sharedInstance
{
  static RNWorkersManager *instance;
  static dispatch_once_t once_token;
  dispatch_once(&once_token, ^{
    instance = [[RNWorkersManager alloc] init];
  });
  return instance;
}

- (id)init
{
    self = [super init];
    workerDictionary = [[NSMutableDictionary alloc] init];    
    return self;
}

- (void) initWorker
{
    [self initWorkerWithPort:8082 bundleRoot:@"index.worker" fallbackResouce:@"worker"];
}

- (void) initWorkerWithPort:(int) port
                 bundleRoot:(NSString*) bundle
            fallbackResouce:(NSString*) resource
{
    NSNumber *nsPort = [NSNumber numberWithInt:port];
    NSURL *jsCodeLocation = [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:bundle fallbackResource:resource];
    
    #if RCT_DEV
        NSString *workerPort = [NSString stringWithFormat:@"%d", nsPort];
        NSString *appPort = [NSString stringWithFormat:@"%d", jsCodeLocation.port];
        NSString *path = [jsCodeLocation.absoluteString stringByReplacingOccurrencesOfString:appPort withString:workerPort];
        jsCodeLocation = [[NSURL alloc] initWithString:path];
    #endif
  
    RCTBridge *worker = [[RCTBridge alloc] initWithBundleURL:jsCodeLocation
                                              moduleProvider:nil
                                               launchOptions:nil];
    
    [workerDictionary setObject:worker forKey:nsPort];
}

- (void) startWorkersWithRootView: (RCTRootView*) rootView
{
  mainBridge = rootView.bridge;
}

@end
