import SwiftUI
import SharedLogic

class AuthViewModelWrapper: ObservableObject {
    private let viewModel: AuthViewModel

    @Published var isLoggedIn = false
    @Published var isLoading = false
    @Published var isCheckingAuth = true
    @Published var error: String? = nil

    init() {
        self.viewModel = IosKoinInitializer.companion.provideAuthViewModel()
    }

    func checkAuth() {
        isCheckingAuth = true
        viewModel.checkAuthAsync { [weak self] state in
            DispatchQueue.main.async {
                guard let self = self else { return }
                self.isLoggedIn = state.isLoggedIn
                self.isCheckingAuth = state.isCheckingAuth
                self.error = state.error
            }
        }
    }

    func login(email: String, password: String) {
        isLoading = true
        error = nil
        viewModel.loginAsync(email: email, password: password) { [weak self] state in
            DispatchQueue.main.async {
                guard let self = self else { return }
                self.isLoggedIn = state.isLoggedIn
                self.isLoading = state.isLoading
                self.error = state.error
            }
        }
    }

    func register(email: String, password: String, name: String) {
        isLoading = true
        error = nil
        viewModel.registerAsync(email: email, password: password, name: name) { [weak self] state in
            DispatchQueue.main.async {
                guard let self = self else { return }
                self.isLoggedIn = state.isLoggedIn
                self.isLoading = state.isLoading
                self.error = state.error
            }
        }
    }

    func logout() {
        viewModel.logout()
        isLoggedIn = false
    }
}