#ifndef RNWorkersManager_h
#define RNWorkersManager_h

#import <React/RCTBridge+Private.h>
#import <React/RCTEventDispatcher.h>
#import <React/RCTBundleURLProvider.h>
#import <React/RCTBridge.h>
#import <React/RCTRootView.h>

@interface RNWorkersManager : NSObject
{
    NSMutableDictionary *workerDictionary;
    RCTBridge *mainBridge;
}

+ (instancetype)sharedInstance;
- (void) initWorker;
- (void) initWorkerWithPort:(int) port bundleRoot:(NSString*) bundle fallbackResouce:(NSString*) resource;
- (void) startWorkersWithRootView: (RCTRootView*) rootView;

@property (nonatomic, strong, readonly) NSMutableDictionary *workerDictionary;
@property (nonatomic, strong, readonly) RCTBridge *mainBridge;
@property (nonatomic, assign) NSNumber *simulationEnabled;

@end

#endif
