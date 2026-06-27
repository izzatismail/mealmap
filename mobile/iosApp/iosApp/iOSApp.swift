import SwiftUI
import SharedLogic

@main
struct iOSApp: App {
    init() {
        let baseUrl = Bundle.main.object(forInfoDictionaryKey: "API_BASE_URL") as? String
            ?? "http://localhost:8080"
        IosKoinInitializer().setupKoin(baseUrl: baseUrl)
    }

    var body: some Scene {
        WindowGroup {
            RecipeListView()
        }
    }
}