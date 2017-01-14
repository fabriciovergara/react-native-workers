#ifndef RNWorkersManager_h
#define RNWorkersManager_h

#import <React/RCTBridge+Private.h>
#import <React/RCTEventDispatcher.h>
#import <React/RCTBundleURLProvider.h>
#import <React/RCTBridge.h>
#import <React/RCTRootView.h>

@interface RNWorkersManager : NSObject
+ (instancetype)sharedInstance;
- (void) initWorkerWithBundleRoot: (NSString*) path fallbackResouce:(NSString*) resource moduleName:(NSString*) name;
- (void) startWithRootView: (RCTRootView*) rootView;

@property (nonatomic, strong, readonly) RCTBridge *workerBridge;
@property (nonatomic, strong, readonly) RCTBridge *mainBridge;

@end

#endif
