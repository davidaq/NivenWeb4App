//
//  NWASerializable.h
//  Test
//
//  Created by AQ on 14-3-21.
//  Copyright (c) 2014年 AQ. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol NWASerializable <NSObject>

@required

-(void)unpack:(id)raw;
-(id)pack;


@end
