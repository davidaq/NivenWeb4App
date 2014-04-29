//
//  NWAPack.h
//  Test
//
//  Created by AQ on 14-3-22.
//  Copyright (c) 2014年 AQ. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NWAPack : NSObject

+(id)pack:(id)obj;
+(id)unpack:(id)obj to:(Class)type;

@end
