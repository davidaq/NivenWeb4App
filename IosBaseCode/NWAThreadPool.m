//
//  NWAThreadPool.m
//  VideoCreator
//
//  Created by 安秦 on 13-12-20.
//  Copyright (c) 2013年 ccme. All rights reserved.
//

#import "NWAThreadPool.h"

@implementation NWAThreadPool {
    NSMutableSet* pool;
    NSCondition* waiter;
    BOOL shouldEnd;
    SEL selector;
}

+(NWAThreadPool*)NWAThreadPoolWithNumberOfThreads:(int)number call:(SEL)selector {
    return [[NWAThreadPool alloc]initWithNumberOfThreads:number call:selector];
}

-(void)dealloc {
    shouldEnd = YES;
}

-(id)initWithNumberOfThreads:(int)number call:(SEL)selector_ {
    self = [self init];
    pool = [NSMutableSet set];
    waiter = [[NSCondition alloc]init];
    shouldEnd = NO;
    selector = selector_;
    for(int i = 0; i < number; i++) {
        NSThread* thread = [[NSThread alloc]initWithTarget:self
                                                  selector:@selector(thread)
                                                    object:nil];
        [thread start];
    }
    return self;
}

-(void)thread {
    while(!shouldEnd) {
        id job = nil;
        @synchronized(pool) {
            job = [pool anyObject];
            if(job)
                [pool removeObject:job];
        }
        if(job) {
            if(selector && [job respondsToSelector:selector]) {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Warc-performSelector-leaks"
                [job performSelector:selector];
#pragma clang diagnostic pop
            }
        } else {
            [waiter lock];
            [waiter wait];
            [waiter unlock];
        }
    }
    
}

-(void)enque:(id)target {
    if(!target)
        return;
    @synchronized(pool) {
        if(target && ![pool containsObject:target]) {
            [pool addObject:target];
            [waiter lock];
            [waiter signal];
            [waiter unlock];
        }
    }
}

-(void)remove:(id)target {
    @synchronized(pool) {
        [pool removeObject:target];
    }
}

@end
