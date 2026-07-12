import SwiftUI

struct LoginView: View {
    @ObservedObject var authViewModel: AuthViewModelWrapper
    @State private var email = ""
    @State private var password = ""
    let onLoginSuccess: () -> Void
    let onNavigateToRegister: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            Spacer()

            Text("MealMap")
                .font(.displayLarge)
                .foregroundColor(.textPrimary)

            Text("Sign in to your account")
                .font(.bodyMedium)
                .foregroundColor(.textSecondary)
                .padding(.top, 8)

            Spacer().frame(height: 32)

            VStack(spacing: 12) {
                TextField("Email", text: $email)
                    .font(.bodyMedium)
                    .textContentType(.emailAddress)
                    .keyboardType(.emailAddress)
                    .autocapitalization(.none)
                    .disableAutocorrection(true)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 14)
                    .background(Color.surface)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(Color.border, lineWidth: 1)
                    )

                SecureField("Password", text: $password)
                    .font(.bodyMedium)
                    .textContentType(.password)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 14)
                    .background(Color.surface)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(Color.border, lineWidth: 1)
                    )
            }
            .padding(.horizontal, 24)

            if let error = authViewModel.error {
                Text(error)
                    .font(.bodySmall)
                    .foregroundColor(.error)
                    .padding(.top, 8)
            }

            Spacer().frame(height: 24)

            Button(action: {
                authViewModel.login(email: email, password: password)
            }) {
                if authViewModel.isLoading {
                    ProgressView()
                        .tint(.white)
                } else {
                    Text("Sign In")
                        .font(.labelLarge)
                }
            }
            .frame(maxWidth: .infinity)
            .frame(height: 48)
            .background(Color.primary)
            .foregroundColor(.white)
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .disabled(authViewModel.isLoading || email.isBlank() || password.isBlank())
            .padding(.horizontal, 24)

            Button(action: onNavigateToRegister) {
                Text("Create Account")
                    .font(.labelLarge)
                    .foregroundColor(.primary)
            }
            .frame(maxWidth: .infinity)
            .frame(height: 48)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(Color.border, lineWidth: 1)
            )
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .padding(.horizontal, 24)
            .padding(.top, 12)

            Spacer()
        }
        .background(Color.background)
        .ignoresSafeArea()
        .onChange(of: authViewModel.isLoggedIn) { loggedIn in
            if loggedIn { onLoginSuccess() }
        }
    }
}

extension String {
    func isBlank() -> Bool {
        return trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }
}