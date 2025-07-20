import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        KoinKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    if url.scheme == "mention" {
                        PlatformKt.handleAuthCallback(url: url.absoluteString)
                    }
                }
        }
    }
}
