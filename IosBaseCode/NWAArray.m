//
//  NWAArray.m
//  Test
//
//  Created by AQ on 14-3-21.
//  Copyright (c) 2014年 AQ. All rights reserved.
//

#import "NWAArray.h"
#import "NWAPack.h"

@implementation NWAArray

-(NSUInteger)countByEnumeratingWithState:(NSFastEnumerationState *)state objects:(id __unsafe_unretained [])buffer count:(NSUInteger)len {
    return [self.array countByEnumeratingWithState:state objects:buffer count:len];
}

-(id)objectAtIndex:(NSUInteger)index {
    return [self.array objectAtIndex:index];
}

-(NSUInteger)count {
    return [self.array count];
}

-(id)pack {
    NSMutableArray* ret = [NSMutableArray arrayWithCapacity:[self.array count]];
    for(id item in self) {
        [ret addObject:[NWAPack pack:item]];
    }
    return ret;
}

-(void)unpack:(id)raw {
    if(![[raw class]isSubclassOfClass:NSArray.class]) {
        @throw [NSException exceptionWithName:@"UnpackException"
                                       reason:@"Type mismatch, expect array"
                                     userInfo:nil];
    }
    NSMutableArray* array = [NSMutableArray arrayWithCapacity:[raw count]];
    for(id o in raw) {
        [array addObject:[NWAPack unpack:o to:self.itemType]];
    }
    [self setArray:array];
}

@end
