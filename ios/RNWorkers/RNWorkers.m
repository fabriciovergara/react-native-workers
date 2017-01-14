#import "RNWorkers.h"

@implementation RNWorkers

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(sendMessageToWorker:(NSString *)message)
{
  RCTBridge *workerBridge = [RNWorkersManager sharedInstance].workerBridge;
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

@end
