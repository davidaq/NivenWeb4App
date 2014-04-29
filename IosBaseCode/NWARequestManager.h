//
//  NWARequestManager.h
//  Test
//
//  Created by AQ on 14-3-21.
//  Copyright (c) 2014年 AQ. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NWARequest.h"

@interface NWARequestManager : NSObject

+(void)enque:(NWARequest*)request;

+(NSString*)getSessionID;
+(void)setSessionID:(NSString*)sessionID;

@end
