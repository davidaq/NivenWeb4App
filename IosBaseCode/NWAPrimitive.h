//
//  NWAPrimitive.h
//  Test
//
//  Created by AQ on 14-3-24.
//  Copyright (c) 2014年 AQ. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NWASerializable.h"

@interface NWANumberPrimitive : NSObject<NWASerializable>

@property (nonatomic,retain) NSNumber* number;

@end

@interface NWAInteger : NWANumberPrimitive
-(int)value;
+(NWAInteger*)of:(int)value;
@end

@interface NWALong : NWANumberPrimitive
-(long)value;
+(NWALong*)of:(long)value;
@end

@interface NWAFloat : NWANumberPrimitive
-(float)value;
+(NWAFloat*)of:(float)value;
@end

@interface NWADouble : NWANumberPrimitive
-(double)value;
+(NWADouble*)of:(double)value;
@end

@interface NWAByte : NWANumberPrimitive
-(unsigned char)value;
+(NWAByte*)of:(unsigned char)value;
@end

@interface NWAChar : NWANumberPrimitive
-(char)value;
+(NWAChar*)of:(char)value;
@end

@interface NWABool : NWANumberPrimitive
-(BOOL)value;
+(NWABool*)of:(BOOL)value;
@end

@interface NWAVoid : NWANumberPrimitive
@end
