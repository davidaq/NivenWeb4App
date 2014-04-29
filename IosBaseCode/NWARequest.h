//
//  NWARequest.h
//  Test
//
//  Created by AQ on 14-3-21.
//  Copyright (c) 2014年 AQ. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef void (^ResultCallback)(int errorCode, NSString* errorMsg, id result);

@interface NWARequest : NSObject<NSURLConnectionDataDelegate>

@property (nonatomic,retain) NSString* url;
@property (nonatomic,retain) NSMutableDictionary* params;
@property (nonatomic,retain) NSMutableDictionary* headers;
@property (nonatomic,copy) ResultCallback resultCallback;

-(void)put:(id)value forParam:(NSString*)key;
-(void)fire;
-(void)doRequest;
-(void)onResult:(ResultCallback)callback;

@end
