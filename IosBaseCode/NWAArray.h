//
//  NWAArray.h
//  Test
//
//  Created by AQ on 14-3-21.
//  Copyright (c) 2014年 AQ. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NWASerializable.h"

@interface NWAArray : NSObject<NSFastEnumeration,NWASerializable>

@property (readonly) NSUInteger count;
@property (nonatomic,retain) Class itemType;

-(void)setArray:(NSArray*)array;

-(id)objectAtIndex:(NSUInteger)index;

-(id)pack;

-(void)unpack:(id)raw;

@end
