//
//  NWAPrimitive.m
//  Test
//
//  Created by AQ on 14-3-24.
//  Copyright (c) 2014年 AQ. All rights reserved.
//

#import "NWAPrimitive.h"

@implementation NWANumberPrimitive

-(id)pack {
    return self.number;
}

-(void)unpack:(NSNumber*)raw {
    self.number = raw;
}

@end

#define BODY(OTYPE,TYPE) \
@implementation NWA##OTYPE \
-(TYPE)value {return [self.number TYPE##Value];} \
+(NWA##OTYPE*)of:(TYPE)value { \
    NWA##OTYPE* ret = [[NWA##OTYPE alloc]init]; \
    ret.number = [NSNumber numberWith##OTYPE:value]; \
    return ret; \
} \
@end

BODY(Integer,int)
BODY(Long,long)
BODY(Float, float)
BODY(Double, double)
BODY(Char,char)

@implementation NWAByte

-(unsigned char)value {
    return (unsigned char)[[self pack]charValue];
}

+(NWAByte*)of:(unsigned char)value {
    NWAByte* ret = [[NWAByte alloc]init];
    ret.number = [NSNumber numberWithChar:(char)value];
    return ret;
}

@end

@implementation NWABool

-(BOOL)value {
    return [[self pack]boolValue];
}

+(NWAByte*)of:(BOOL)value {
    NWAByte* ret = [[NWAByte alloc]init];
    ret.number = [NSNumber numberWithBool:value];
    return ret;
}

@end

@implementation NWAVoid

@end