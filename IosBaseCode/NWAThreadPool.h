//
//  NWAThreadPool.h
//  VideoCreator
//
//  Created by 安秦 on 13-12-20.
//  Copyright (c) 2013年 ccme. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NWAThreadPool : NSObject

+(NWAThreadPool*)NWAThreadPoolWithNumberOfThreads:(int)number call:(SEL)selector;

-(void)enque:(id)target;

-(void)remove:(id)target;

@end
