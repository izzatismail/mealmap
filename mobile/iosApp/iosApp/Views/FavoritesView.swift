import SwiftUI
import SharedLogic

struct FavoritesView: View {
    @StateObject private var viewModel = FavoriteViewModelWrapper()

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text("Favorites")
                    .font(.displayLarge)
                    .foregroundColor(.textPrimary)

                if viewModel.isLoading {
                    ProgressView()
                        .frame(maxWidth: .infinity)
                        .padding()
                }

                if let error = viewModel.error {
                    Text(error)
                        .font(.bodyMedium)
                        .foregroundColor(.error)
                }

                if viewModel.favorites.isEmpty && !viewModel.isLoading {
                    Text("No favorites yet")
                        .font(.bodyLarge)
                        .foregroundColor(.textSecondary)
                        .frame(maxWidth: .infinity)
                        .padding(.top, 64)
                }

                LazyVStack(spacing: 16) {
                    ForEach(viewModel.favorites, id: \.spoonacularId) { recipe in
                        RecipeCardView(recipe: recipe) {
                            viewModel.toggleFavorite(recipeId: recipe.spoonacularId)
                        }
                    }
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 16)
        }
        .background(Color.background)
        .onAppear {
            viewModel.loadFavorites()
        }
    }
}