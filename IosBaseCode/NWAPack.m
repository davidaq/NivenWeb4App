//
//  NWAPack.m
//  Test
//
//  Created by AQ on 14-3-22.
//  Copyright (c) 2014年 AQ. All rights reserved.
//

#import "NWAPack.h"
#import "NWAArray.h"
#import "NWASerializable.h"

@implementation NWAPack

+(id)pack:(id)obj {
    if(obj == nil)
        return nil;
    if([obj conformsToProtocol:@protocol(NWASerializable)]) {
        return [obj pack];
    } else if([[obj class] isSubclassOfClass:NSArray.class]) {
        NWAArray* array = [[NWAArray alloc]init];
        [array setArray:obj];
        return [array pack];
    } else if([[obj class] isSubclassOfClass:NSDate.class]) {
        NSDate* date = obj;
        obj = [NSNumber numberWithLong:(long)[date timeIntervalSince1970]];
    }
    return obj;
}

+(id)unpack:(id)obj to:(Class)type {
    if(nil == obj)
        return nil;
    id ret = [[type alloc]init];
    if([ret conformsToProtocol:@protocol(NWASerializable)]) {
        [ret unpack:obj];
        return ret;
    } else if([type isSubclassOfClass:NSDate.class]) {
        return [(NSDate*)ret initWithTimeIntervalSince1970:[(NSNumber*)obj longValue]];
    } else {
        return obj;
    }
}

@end
