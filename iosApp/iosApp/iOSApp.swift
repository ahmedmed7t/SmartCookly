import SwiftUI
import Firebase
import GoogleSignIn
import RevenueCat

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()
        InitKoinKt.doInitKoin()
        
        let revenueCatApiKey = Bundle.main.object(forInfoDictionaryKey: "REVENUECAT_API_KEY") as? String ?? ""
        
        Purchases.logLevel = .debug
        Purchases.configure(
            with: .builder(withAPIKey: revenueCatApiKey)
                .build()
        )
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    GIDSignIn.sharedInstance.handle(url)
                }
        }
    }
}
