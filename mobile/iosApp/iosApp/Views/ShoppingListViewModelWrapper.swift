import SwiftUI
import SharedLogic

class ShoppingListViewModelWrapper: ObservableObject {
    private let viewModel: ShoppingListViewModel

    @Published var items: [ShoppingItemDto] = []
    @Published var isLoading = false
    @Published var error: String? = nil

    init() {
        self.viewModel = IosKoinInitializer.companion.provideShoppingListViewModel()
    }

    func loadCurrentList() {
        isLoading = true; error = nil
        viewModel.loadCurrentListAsync { [weak self] state in
            DispatchQueue.main.async {
                guard let self = self else { return }
                self.items = state.shoppingList?.items ?? []
                self.isLoading = state.isLoading
                self.error = state.error
            }
        }
    }

    func toggleItem(itemId: Int64) {
        viewModel.toggleItem(itemId: itemId)
    }

    func generateFromMealPlan(mealPlanId: Int64) {
        isLoading = true; error = nil
        viewModel.generateFromMealPlan(mealPlanId: mealPlanId)
    }
}