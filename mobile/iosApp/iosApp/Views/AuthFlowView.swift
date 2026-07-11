import SwiftUI

struct AuthFlowView: View {
    @StateObject private var authViewModel = AuthViewModelWrapper()
    @State private var showLogin = true

    var body: some View {
        Group {
            if authViewModel.isCheckingAuth {
                Color.background.ignoresSafeArea()
            } else if authViewModel.isLoggedIn {
                MainTabView()
            } else if showLogin {
                LoginView(
                    authViewModel: authViewModel,
                    onLoginSuccess: { authViewModel.checkAuth() },
                    onNavigateToRegister: { showLogin = false }
                )
            } else {
                RegisterView(
                    authViewModel: authViewModel,
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