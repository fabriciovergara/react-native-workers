#import "RNWorkers.h"

@implementation RNWorkers

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(sendMessageToWorker:(int)port message:(NSString *)message)
{
    NSNumber *nsPort = [NSNumber numberWithInt:port];
    NSMutableDictionary *dic = [RNWorkersManager sharedInstance].workerDictionary;
    RCTBridge *workerBridge = [dic objectForKey:nsPort];
    if(workerBridge == nil){
        return;
    }
  
    [workerBridge.eventDispatcher sendAppEventWithName:@"RNWorkers" body:message];
}

RCT_EXPORT_METHOD(sendMessageToApp:(NSString *)message)
{
    RCTBridge *mainBridge = [RNWorkersManager sharedInstance].mainBridge;
    if(mainBridge == nil){
        return;
    }
  
    [mainBridge.eventDispatcher sendAppEventWithName:@"RNWorkersApp" body:message];
}

- (NSDictionary *)constantsToExport
{
    return @{ @"simulationEnabled": [RNWorkersManager sharedInstance].simulationEnabled };
}

@end
