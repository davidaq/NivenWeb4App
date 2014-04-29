//
//  NWARequestManager.m
//  Test
//
//  Created by AQ on 14-3-21.
//  Copyright (c) 2014年 AQ. All rights reserved.
//

#import "NWARequestManager.h"
#import "NWAThreadPool.h"

NWAThreadPool* requestThreadPool;
NSString* sessionID;

@implementation NWARequestManager

+(void)enque:(NWARequest*)request {
    if(!requestThreadPool)
        requestThreadPool = [NWAThreadPool NWAThreadPoolWithNumberOfThreads:2 call:@selector(doRequest)];
    [requestThreadPool enque:request];
}

+(NSString*)getSessionID {
    return sessionID;
}

+(void)setSessionID:(NSString*)sessid {
    sessionID = sessid;
}

@end
