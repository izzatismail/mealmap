import SwiftUI
import SharedLogic

struct RecipeCardView: View {
    let recipe: Recipe
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            VStack(alignment: .leading, spacing: 8) {
                ZStack(alignment: .topTrailing) {
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

                    Text("\(Int(recipe.healthScore))%")
                        .font(.bodySmall)
                        .foregroundColor(.textSecondary)
                        .padding(.horizontal, 10)
                        .padding(.vertical, 4)
                        .background(Color.white.opacity(0.85))
                        .clipShape(Capsule())
                        .padding(8)
                }

                VStack(alignment: .leading, spacing: 4) {
                    Text(recipe.title)
                        .font(.bodyLarge)
                        .foregroundColor(.textPrimary)
                        .lineLimit(2)

                    HStack(spacing: 8) {
                        Text("\(recipe.readyInMinutes) min")
                            .font(.bodySmall)
                            .foregroundColor(.textSecondary)
                        Text("\(recipe.servings) servings")
                            .font(.bodySmall)
                            .foregroundColor(.textSecondary)
                    }

                    if !recipe.dishTypes.isEmpty {
                        HStack(spacing: 4) {
                            ForEach(recipe.dishTypes.prefix(2), id: \.self) { type in
                                Text(type.uppercased())
                                    .font(.labelSmall)
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
            .background(Color.cardBg)
            .clipShape(RoundedRectangle(cornerRadius: 20))
            .shadow(color: Color(red: 60/255, green: 50/255, blue: 40/255).opacity(0.09), radius: 16, x: 0, y: 4)
        }
        .buttonStyle(PlainButtonStyle())
    }
}