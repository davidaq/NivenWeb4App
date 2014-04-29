
#ifndef Test_NWABegin_h
#define Test_NWABegin_h

#import <Foundation/Foundation.h>
#import "NWASerializable.h"
#import "NWAPack.h"
#import "NWAArray.h"
#import "NWARequestManager.h"
#import "NWARequest.h"
#import "NWAPrimitive.h"

#define NWA_BEAN_BEGIN(NAME) @interface NAME : NSObject<NWASerializable>
#define NWA_FIELD(TYPE,NAME) @property (nonatomic,retain) TYPE * NAME ;
#define NWA_BEAN_END(NAME) @end NWA_BEAN_ARRAY(NAME)
#define NWA_BEAN_ARRAY(NAME) \
@interface ArrayOf##NAME : NWAArray \
-(NAME *)objectAtIndex:(NSUInteger)index; \
@end

#endif
