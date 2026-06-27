import SwiftUI
import SharedLogic

struct RecipeDetailView: View {
    let recipeId: Int64
    @StateObject private var viewModel = RecipeViewModelWrapper()

    var body: some View {
        ScrollView {
            if viewModel.isLoadingDetail {
                ProgressView()
                    .frame(maxWidth: .infinity, minHeight: 300)
            } else if let error = viewModel.detailError {
                Text(error)
                    .foregroundColor(.error)
                    .padding()
            } else if let recipe = viewModel.detailRecipe {
                VStack(alignment: .leading, spacing: 0) {
                    AsyncImage(url: URL(string: recipe.image)) { phase in
                        switch phase {
                        case .success(let image):
                            image
                                .resizable()
                                .aspectRatio(contentMode: .fill)
                        case .failure, .empty:
                            Color.primaryLight
                        @unknown default:
                            Color.primaryLight
                        }
                    }
                    .frame(height: 220)
                    .clipped()

                    VStack(alignment: .leading, spacing: 16) {
                        Text(recipe.title)
                            .font(.system(size: 18, weight: .bold))
                            .foregroundColor(.textPrimary)

                        Text("\(recipe.readyInMinutes) min  ·  \(recipe.servings) servings  ·  \(Int(recipe.healthScore))% health")
                            .font(.caption)
                            .foregroundColor(.textSecondary)

                        if !recipe.dishTypes.isEmpty {
                            Text(recipe.dishTypes.joined(separator: " · "))
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(.primary)
                        }

                        Text("Ingredients")
                            .font(.system(size: 22, weight: .bold))
                            .foregroundColor(.textPrimary)

                        ForEach(recipe.ingredients, id: \.id) { ingredient in
                            Text("• \(ingredient.original)")
                                .font(.system(size: 14))
                                .foregroundColor(.textPrimary)
                        }

                        Text("Instructions")
                            .font(.system(size: 22, weight: .bold))
                            .foregroundColor(.textPrimary)

                        Text(recipe.instructions)
                            .font(.system(size: 14))
                            .foregroundColor(.textPrimary)
                    }
                    .padding(16)
                }
            }
        }
        .background(Color.background)
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            viewModel.loadDetail(id: recipeId)
        }
    }
}