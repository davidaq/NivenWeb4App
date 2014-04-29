
#ifndef Test_NWABody_h
#define Test_NWABody_h

#pragma clang diagnostic ignored "-Wincomplete-implementation"

#define NWA_BEAN_UNPACK_BEGIN -(void)unpack:(NSDictionary*)raw {
#define NWA_BEAN_UNPACK(TYPE,NAME) self.NAME = [NWAPack unpack:[raw objectForKey:@#NAME] to:TYPE.class];
#define NWA_BEAN_UNPACK_END }

#define NWA_BEAN_PACK_BEGIN -(id)pack { NSMutableDictionary* ret = [NSMutableDictionary dictionary];
#define NWA_BEAN_PACK(NAME) if(self.NAME) [ret setObject:self.NAME forKey:@#NAME];
#define NWA_BEAN_PACK_END return ret; }

#define NWA_BEAN_ARRAY_BODY(NAME) \
@implementation ArrayOf##NAME \
-(id)init { \
    self = [super init]; \
    self.itemType = NAME.class; \
    return self; \
} \
@end \

#endif
