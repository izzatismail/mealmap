import SwiftUI

struct AppRootView: View {
    @StateObject private var authViewModel = AuthViewModelWrapper()

    var body: some View {
        Group {
            if authViewModel.isCheckingAuth {
                Color.background
                    .ignoresSafeArea()
            } else if authViewModel.isLoggedIn {
                MainTabView()
                    .environmentObject(authViewModel)
            } else {
                NavigationStack {
                    LoginView(
                        onLoginSuccess: {},
                        onNavigateToRegister: {}
                    )
                    .navigationDestination(for: String.self) { route in
                        if route == "register" {
                            RegisterView(
                                onRegisterSuccess: {},
                                onNavigateToLogin: {}
                            )
                        }
                    }
                }
            }
        }
        .onAppear {
            authViewModel.checkAuth()
        }
    }
}

// Simple navigation-based auth flow without NavigationStack complexity
struct AuthFlowView: View {
    @StateObject private var authViewModel = AuthViewModelWrapper()
    @State private var showLogin = true

    var body: some View {
        Group {
            if authViewModel.isCheckingAuth {
                Color.background.ignoresSafeArea()
            } else if authViewModel.isLoggedIn {
                MainTabView()
                    .environmentObject(authViewModel)
            } else if showLogin {
                LoginView(
                    onLoginSuccess: { authViewModel.checkAuth() },
                    onNavigateToRegister: { showLogin = false }
                )
            } else {
                RegisterView(
                    onRegisterSuccess: { authViewModel.checkAuth() },
                    onNavigateToLogin: { showLogin = true }
                )
            }
        }
        .onAppear {
            authViewModel.checkAuth()
        }
    }
}