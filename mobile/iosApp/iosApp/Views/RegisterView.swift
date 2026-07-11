import SwiftUI

struct RegisterView: View {
    @ObservedObject var authViewModel: AuthViewModelWrapper
    @State private var name = ""
    @State private var email = ""
    @State private var password = ""
    let onRegisterSuccess: () -> Void
    let onNavigateToLogin: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            Spacer()

            Text("Create Account")
                .font(.displayLarge)
                .foregroundColor(.textPrimary)

            Text("Join MealMap today")
                .font(.bodyMedium)
                .foregroundColor(.textSecondary)
                .padding(.top, 8)

            Spacer().frame(height: 32)

            VStack(spacing: 12) {
                TextField("Name", text: $name)
                    .font(.bodyMedium)
                    .autocapitalization(.words)
                    .disableAutocorrection(true)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 14)
                    .background(Color.surface)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(Color.border, lineWidth: 1)
                    )

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
                    .textContentType(.newPassword)
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
                authViewModel.register(email: email, password: password, name: name)
            }) {
                if authViewModel.isLoading {
                    ProgressView()
                        .tint(.white)
                } else {
                    Text("Create Account")
                        .font(.labelLarge)
                }
            }
            .frame(maxWidth: .infinity)
            .frame(height: 48)
            .background(Color.primary)
            .foregroundColor(.white)
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .disabled(authViewModel.isLoading || name.isBlank() || email.isBlank() || password.isBlank())
            .padding(.horizontal, 24)

            Button(action: onNavigateToLogin) {
                Text("Back to Sign In")
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
            if loggedIn { onRegisterSuccess() }
        }
    }
}