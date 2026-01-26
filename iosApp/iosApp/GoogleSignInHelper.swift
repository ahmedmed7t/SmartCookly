import Foundation
import UIKit
import GoogleSignIn

@objc public class GoogleSignInHelper: NSObject {
    @objc public static let shared = GoogleSignInHelper()
    
    private override init() {
        super.init()
    }
    
    @objc public func signIn(
        clientId: String,
        completion: @escaping (String?, String?, NSError?) -> Void
    ) {
        let config = GIDConfiguration(clientID: clientId)
        GIDSignIn.sharedInstance.configuration = config
        
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let rootViewController = windowScene.windows.first?.rootViewController else {
            completion(nil, nil, NSError(
                domain: "GoogleSignIn",
                code: -1,
                userInfo: [NSLocalizedDescriptionKey: "No root view controller"]
            ))
            return
        }
        
        GIDSignIn.sharedInstance.signIn(withPresenting: rootViewController) { result, error in
            if let error = error {
                completion(nil, nil, error as NSError)
                return
            }
            guard let idToken = result?.user.idToken?.tokenString else {
                completion(nil, nil, NSError(
                    domain: "GoogleSignIn",
                    code: -2,
                    userInfo: [NSLocalizedDescriptionKey: "No ID token"]
                ))
                return
            }
            completion(idToken, result?.user.accessToken.tokenString, nil)
        }
    }
}
