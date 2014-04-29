//
//  NWARequest.m
//  Test
//
//  Created by AQ on 14-3-21.
//  Copyright (c) 2014年 AQ. All rights reserved.
//

#import "NWARequest.h"
#import "NWARequestManager.h"
#import "NWASerializable.h"

@implementation NWARequest

-(id)init {
    self = [super init];
    if(self) {
        self.params = [NSMutableDictionary dictionary];
        self.headers = [NSMutableDictionary dictionary];
    }
    return self;
}

-(void)fire {
    [NWARequestManager enque:self];
}

-(void)doRequest {
    NSMutableURLRequest* request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:self.url]
                                                           cachePolicy:NSURLRequestReloadIgnoringCacheData
                                                       timeoutInterval:20];
    [request setHTTPMethod:@"POST"];
    [self.headers enumerateKeysAndObjectsUsingBlock:^(id key, id obj, BOOL *stop) {
        [request addValue:obj forHTTPHeaderField:key];
    }];
    [request addValue:@"application/json; charset=UTF-8" forHTTPHeaderField:@"Content-type"];
    [request addValue:@"json" forHTTPHeaderField:@"reqtype"];
    if([NWARequestManager getSessionID]) {
        [request addValue:[NWARequestManager getSessionID] forHTTPHeaderField:@"sessionid"];
        [request addValue:[NSString stringWithFormat:@"sessionid=%@", [NWARequestManager getSessionID]]
       forHTTPHeaderField:@"Cookie"];
    }
    
    NSMutableDictionary* postData = [NSMutableDictionary dictionary];
    [self.params enumerateKeysAndObjectsUsingBlock:^(id key, id obj, BOOL *stop) {
        if ([obj conformsToProtocol:@protocol(NWASerializable)]) {
            obj = [obj pack];
        }
        [postData setObject:obj forKey:key];
    }];
    [request setHTTPBody:[NSJSONSerialization dataWithJSONObject:postData
                                                         options:0
                                                           error:nil]];
    
    NSURLConnection* conn = [[NSURLConnection alloc]initWithRequest:request delegate:self startImmediately:NO];
    [conn performSelectorOnMainThread:@selector(start) withObject:nil waitUntilDone:YES];
}

-(void)put:(id)value forParam:(NSString*)key {
    if(value)
       [self.params setObject:value forKey:key];
}

-(void)connection:(NSURLConnection*)connection didReceiveResponse:(NSHTTPURLResponse*)response {
    NSString* sessid = [[response allHeaderFields]objectForKey:@"sessionid"];
    [NWARequestManager setSessionID:sessid];
}

-(void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
    if(self.resultCallback) {
        NSDictionary* result = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
        int errorCode = [[result objectForKey:@"error"] intValue];
        NSString* errorMessage = [result objectForKey:@"message"];
        if(errorCode) {
            self.resultCallback(errorCode, errorMessage, nil);
        } else {
            id resultObj = [result objectForKey:@"result"];
            self.resultCallback(0, nil, resultObj);
        }
    }
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
    self.resultCallback(999, @"Connection Failed", nil);
}

-(void)onResult:(ResultCallback)callback {
    self.resultCallback = callback;
}


@end
