#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface GoogleSignInHelper : NSObject

+ (instancetype)shared;

- (void)signInWithClientId:(NSString *)clientId
                completion:(void (^)(NSString * _Nullable idToken,
                                     NSString * _Nullable accessToken,
                                     NSError * _Nullable error))completion;

@end

NS_ASSUME_NONNULL_END
