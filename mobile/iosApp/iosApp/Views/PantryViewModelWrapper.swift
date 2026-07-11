import SwiftUI
import SharedLogic

class PantryViewModelWrapper: ObservableObject {
    private let viewModel: PantryViewModel

    @Published var items: [PantryItemDto] = []
    @Published var isLoading = false
    @Published var error: String? = nil

    init() {
        self.viewModel = IosKoinInitializer.companion.providePantryViewModel()
    }

    func loadItems() {
        isLoading = true; error = nil
        viewModel.loadItemsAsync { [weak self] state in
            DispatchQueue.main.async {
                guard let self = self else { return }
                self.items = state.items
                self.isLoading = state.isLoading
                self.error = state.error
            }
        }
    }

    func addItem(name: String, amount: Double, unit: String) {
        viewModel.addItem(name: name, amount: amount, unit: unit, expirationDate: nil)
    }

    func deleteItem(id: Int64) {
        viewModel.deleteItem(id: id)
    }
}