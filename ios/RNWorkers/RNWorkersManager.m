#import "RNWorkersManager.h"

@implementation RNWorkersManager

@synthesize mainBridge;
@synthesize workerDictionary;
@synthesize simulationEnabled;
@synthesize preferResourceEnabled;

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
    simulationEnabled = [NSNumber numberWithBool: NO];
    preferResourceEnabled = [NSNumber numberWithBool: NO];
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
  
    if([preferResourceEnabled boolValue] ==  YES){
        if(resource == nil){
            [NSException raise:@"rn-worker" format:@"preferResourceEnabled is enabled but no resource was provided"];
        }
        
        jsCodeLocation = [[NSBundle mainBundle] URLForResource:resource withExtension:@"jsbundle"];
        
        if(jsCodeLocation == nil){
            [NSException raise:@"rn-worker" format:@"JS bundle '%@.jsbundle' not found", resource];
        }
    }else{
        NSString *workerPort = [nsPort stringValue];
        NSString *appPort = [jsCodeLocation.port stringValue];
        NSString *path = [jsCodeLocation.absoluteString stringByReplacingOccurrencesOfString:appPort withString:workerPort];
        jsCodeLocation = [[NSURL alloc] initWithString:path];
    }
    
    #endif
    
    
    if([simulationEnabled boolValue] ==  NO){
        RCTBridge *worker = [[RCTBridge alloc] initWithBundleURL:jsCodeLocation
                                       moduleProvider:nil
                                        launchOptions:nil];
        [workerDictionary setObject:worker forKey:nsPort];
    }else {
        [workerDictionary setObject:nsPort forKey:nsPort];
    }
    
    
    
}

- (void) startWorkersWithRootView: (RCTRootView*) rootView
{
    mainBridge = rootView.bridge;
    if([simulationEnabled boolValue] == YES){
        [self fillDictionaryWithMainBridge];
    }
}

- (void) fillDictionaryWithMainBridge
{
    NSArray *keys = [workerDictionary allKeys];
    if(keys != nil){
        for (id key in keys) {
            [workerDictionary setObject:mainBridge forKey:key];
        }
    }
}

@end
