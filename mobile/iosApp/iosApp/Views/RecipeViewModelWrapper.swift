import SwiftUI
import SharedLogic

class RecipeViewModelWrapper: ObservableObject {
    private let repository = RecipeRepository(
        api: MealMapApi(httpClient: HttpClientFactory.shared.create()),
        database: nil
    )
    private let viewModel: RecipeViewModel

    @Published var recipes: [Recipe] = []
    @Published var isLoading = false
    @Published var error: String? = nil
    @Published var selectedRecipeId: Int64? = nil

    @Published var detailRecipe: Recipe? = nil
    @Published var isLoadingDetail = false
    @Published var detailError: String? = nil

    init() {
        self.viewModel = RecipeViewModel(repository: repository)
    }

    func search(query: String) {
        guard !query.trimmingCharacters(in: .whitespaces).isEmpty else { return }
        isLoading = true
        error = nil
        viewModel.searchAsync(query: query) { [weak self] state in
            DispatchQueue.main.async {
                guard let self = self else { return }
                self.recipes = state.recipes
                self.isLoading = state.isLoading
                self.error = state.error
            }
        }
    }

    func loadDetail(id: Int64) {
        isLoadingDetail = true
        detailError = nil
        viewModel.loadDetailAsync(recipeId: id) { [weak self] state in
            DispatchQueue.main.async {
                guard let self = self else { return }
                self.detailRecipe = state.recipe
                self.isLoadingDetail = state.isLoading
                self.detailError = state.error
            }
        }
    }

    func loadCached() {
        isLoading = true
        error = nil
        viewModel.loadCachedAsync { [weak self] state in
            DispatchQueue.main.async {
                guard let self = self else { return }
                self.recipes = state.recipes
                self.isLoading = state.isLoading
                self.error = state.error
            }
        }
    }
}