import SwiftUI
import SharedLogic

struct RecipeCardView: View {
    let recipe: Recipe
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            VStack(alignment: .leading, spacing: 8) {
                AsyncImage(url: URL(string: recipe.image)) { phase in
                    switch phase {
                    case .success(let image):
                        image
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                    case .failure:
                        Color.primaryLight
                    case .empty:
                        Color.primaryLight
                    @unknown default:
                        Color.primaryLight
                    }
                }
                .frame(height: 130)
                .clipShape(RoundedRectangle(cornerRadius: 20, style: .continuous))
                .clipped()

                VStack(alignment: .leading, spacing: 4) {
                    Text(recipe.title)
                        .font(.system(size: 16, weight: .semibold))
                        .foregroundColor(.textPrimary)
                        .lineLimit(2)

                    HStack(spacing: 8) {
                        Text("\(recipe.readyInMinutes) min")
                            .font(.caption)
                            .foregroundColor(.textSecondary)
                        Text("\(recipe.servings) servings")
                            .font(.caption)
                            .foregroundColor(.textSecondary)
                    }

                    if !recipe.dishTypes.isEmpty {
                        HStack(spacing: 4) {
                            ForEach(recipe.dishTypes.prefix(2), id: \.self) { type in
                                Text(type.uppercased())
                                    .font(.system(size: 11, weight: .bold))
                                    .foregroundColor(.primary)
                                    .padding(.horizontal, 8)
                                    .padding(.vertical, 2)
                                    .background(Color.primaryLight)
                                    .clipShape(Capsule())
                            }
                        }
                    }
                }
                .padding(.horizontal, 12)
                .padding(.bottom, 12)
            }
            .background(Color.white)
            .clipShape(RoundedRectangle(cornerRadius: 20, style: .continuous))
            .shadow(color: Color.black.opacity(0.09), radius: 16, x: 0, y: 4)
        }
        .buttonStyle(PlainButtonStyle())
    }
}