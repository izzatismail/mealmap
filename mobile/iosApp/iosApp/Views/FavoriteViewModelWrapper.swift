import SwiftUI
import SharedLogic

class FavoriteViewModelWrapper: ObservableObject {
    private let viewModel: FavoriteViewModel

    @Published var favorites: [Recipe] = []
    @Published var isLoading = false
    @Published var error: String? = nil

    init() {
        self.viewModel = IosKoinInitializer.companion.provideFavoriteViewModel()
    }

    func loadFavorites() {
        isLoading = true; error = nil
        viewModel.loadFavoritesAsync { [weak self] state in
            DispatchQueue.main.async {
                guard let self = self else { return }
                self.favorites = state.favorites
                self.isLoading = state.isLoading
                self.error = state.error
            }
        }
    }

    func toggleFavorite(recipeId: Int64) {
        viewModel.toggleFavorite(recipeId: recipeId)
    }

    func isFavorited(recipeId: Int64) -> Bool {
        return viewModel.isFavorited(recipeId: recipeId)
    }
}