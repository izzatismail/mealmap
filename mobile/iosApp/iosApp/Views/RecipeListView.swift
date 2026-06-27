import SwiftUI
import SharedLogic

struct RecipeListView: View {
    @StateObject private var viewModel = RecipeViewModelWrapper()
    @State private var searchText = ""

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    Text("What's cooking today?")
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(.textPrimary)

                    HStack {
                        TextField("Search recipes...", text: $searchText)
                            .font(.system(size: 14))
                            .padding(.horizontal, 16)
                            .padding(.vertical, 12)
                            .background(Color.surface)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                            .overlay(
                                RoundedRectangle(cornerRadius: 12)
                                    .stroke(Color.border, lineWidth: 1)
                            )
                            .onSubmit {
                                viewModel.search(query: searchText)
                            }
                    }

                    Text("Recipes")
                        .font(.system(size: 22, weight: .bold))
                        .foregroundColor(.textPrimary)

                    if viewModel.isLoading {
                        ProgressView()
                            .frame(maxWidth: .infinity)
                            .padding()
                    }

                    if let error = viewModel.error {
                        Text(error)
                            .font(.body)
                            .foregroundColor(.error)
                    }

                    LazyVStack(spacing: 16) {
                        ForEach(viewModel.recipes, id: \.spoonacularId) { recipe in
                            RecipeCardView(recipe: recipe) {
                                viewModel.selectedRecipeId = recipe.spoonacularId
                            }
                        }
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 16)
            }
            .background(Color.background)
            .navigationDestination(item: $viewModel.selectedRecipeId) { id in
                RecipeDetailView(recipeId: id)
            }
        }
    }
}

extension Binding where Value == Int64? {
    init(_ id: Binding<Int64?>) {
        self.init(get: { id.wrappedValue }, set: { id.wrappedValue = $0 })
    }
}