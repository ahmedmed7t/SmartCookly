import SwiftUI
import Firebase
import GoogleSignIn
import RevenueCat

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()
        InitKoinKt.doInitKoin()
        
        Purchases.logLevel = .debug
        Purchases.configure(
            with: .builder(withAPIKey: "test_iLsCsQkMfQTdbXoZCiKWieECQkb")
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
