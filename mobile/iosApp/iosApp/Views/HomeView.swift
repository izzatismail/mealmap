import SwiftUI
import SharedLogic

struct HomeView: View {
    @StateObject private var recipeVM = RecipeViewModelWrapper()
    @StateObject private var shoppingVM = ShoppingListViewModelWrapper()
    @StateObject private var pantryVM = PantryViewModelWrapper()
    @StateObject private var favoriteVM = FavoriteViewModelWrapper()

    private let dayLabels = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"]

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                // Section 1: Greeting
                VStack(alignment: .leading, spacing: 4) {
                    Text("Good morning 👋")
                        .font(.bodyMedium)
                        .foregroundColor(.textSecondary)
                    Text("What's cooking today?")
                        .font(.displayLarge)
                        .foregroundColor(.textPrimary)
                }
                .padding(.horizontal, 16)
                .padding(.top, 16)

                // Section 2: Week day strip
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(Array(dayLabels.enumerated()), id: \.offset) { index, label in
                            VStack(spacing: 4) {
                                Text(String(label.prefix(1)))
                                    .font(.labelLarge)
                                    .foregroundColor(index == 0 ? .white : .textPrimary)
                                    .frame(width: 44, height: 44)
                                    .background(index == 0 ? Color.primary : Color.white)
                                    .clipShape(Circle())
                                Text(label)
                                    .font(.bodySmall)
                                    .foregroundColor(index == 0 ? .primary : .textSecondary)
                            }
                        }
                    }
                    .padding(.horizontal, 16)
                }
                .padding(.top, 16)

                // Section 3: Today's Meals
                sectionHeader("Today's Meals")

                ForEach(["Breakfast", "Lunch", "Dinner"], id: \.self) { mealType in
                    MealSlotView(recipe: nil, mealType: mealType)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 4)
                }

                // Section 4: Status Cards
                sectionHeader("Pantry & Shopping")

                HStack(spacing: 12) {
                    StatusCardView(emoji: "\u{1F9EA}", count: "\(pantryVM.items.count)", label: "in pantry")
                    StatusCardView(emoji: "\u{1F6D2}", count: "\(shoppingVM.items.count)", label: "to buy")
                    StatusCardView(emoji: "\u{2705}", count: "\(recipeVM.recipes.count)", label: "recipes")
                }
                .padding(.horizontal, 16)

                // Section 5: Try Something New
                if let suggestion = recipeVM.recipes.first {
                    sectionHeader("Try Something New")

                    Button(action: {}) {
                        VStack(alignment: .leading, spacing: 0) {
                            AsyncImage(url: URL(string: suggestion.image)) { phase in
                                switch phase {
                                case .success(let image):
                                    image.resizable().aspectRatio(contentMode: .fill)
                                default:
                                    Color.primaryLight
                                }
                            }
                            .frame(height: 180)
                            .clipped()

                            VStack(alignment: .leading, spacing: 4) {
                                Text(suggestion.title)
                                    .font(.titleLarge)
                                    .foregroundColor(.textPrimary)
                                    .lineLimit(2)
                                Text("\(suggestion.readyInMinutes) min  ·  \(suggestion.servings) servings")
                                    .font(.bodySmall)
                                    .foregroundColor(.textSecondary)
                            }
                            .padding(16)
                        }
                    }
                    .background(Color.cardBg)
                    .clipShape(RoundedRectangle(cornerRadius: 20))
                    .shadow(color: Color(red: 60/255, green: 50/255, blue: 40/255).opacity(0.09), radius: 16, x: 0, y: 4)
                    .padding(.horizontal, 16)
                    .buttonStyle(PlainButtonStyle())
                }

                // Section 6: Quick & Easy
                if recipeVM.recipes.count > 1 {
                    sectionHeader("Quick & Easy")

                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 12) {
                            ForEach(recipeVM.recipes.filter { $0.readyInMinutes <= 30 }.prefix(10), id: \.spoonacularId) { recipe in
                                CompactRecipeCardView(recipe: recipe)
                            }
                        }
                        .padding(.horizontal, 16)
                    }
                }
            }
        }
        .background(Color.background)
        .onAppear {
            recipeVM.loadCached()
            shoppingVM.loadCurrentList()
            pantryVM.loadItems()
        }
    }

    @ViewBuilder
    private func sectionHeader(_ title: String) -> some View {
        HStack {
            Text(title)
                .font(.headlineLarge)
                .foregroundColor(.textPrimary)
            Spacer()
            Text("More")
                .font(.bodySmall)
                .foregroundColor(.primary)
        }
        .padding(.horizontal, 16)
        .padding(.top, 24)
        .padding(.bottom, 12)
    }
}

struct MealSlotView: View {
    let recipe: Recipe?
    let mealType: String
    let mealTitle: String
    let showRemoveButton: Bool
    var onRemove: (() -> Void)?

    init(
        recipe: Recipe? = nil,
        mealType: String,
        mealTitle: String = "",
        showRemoveButton: Bool = false,
        onRemove: (() -> Void)? = nil
    ) {
        self.recipe = recipe
        self.mealType = mealType
        self.mealTitle = mealTitle
        self.showRemoveButton = showRemoveButton
        self.onRemove = onRemove
    }

    var body: some View {
        HStack(spacing: 12) {
            if let recipe = recipe {
                AsyncImage(url: URL(string: recipe.image)) { phase in
                    switch phase {
                    case .success(let image):
                        image.resizable().aspectRatio(contentMode: .fill)
                    default:
                        Color.primaryLight
                    }
                }
                .frame(width: 56, height: 56)
                .clipShape(RoundedRectangle(cornerRadius: 8))

                VStack(alignment: .leading, spacing: 2) {
                    Text(recipe.title)
                        .font(.bodyLarge)
                        .foregroundColor(.textPrimary)
                        .lineLimit(1)
                    Text("\(recipe.readyInMinutes) min")
                        .font(.bodySmall)
                        .foregroundColor(.textSecondary)
                }
            } else if showRemoveButton {
                Text(emojiForMealType(mealType))
                    .font(.titleLarge)
                    .frame(width: 56, height: 56)
                    .background(Color.primaryLight)
                    .clipShape(RoundedRectangle(cornerRadius: 8))

                VStack(alignment: .leading, spacing: 2) {
                    Text(mealTitle)
                        .font(.bodyLarge)
                        .foregroundColor(.textPrimary)
                        .lineLimit(1)
                }
            } else {
                Text("+")
                    .font(.titleLarge)
                    .foregroundColor(.primary)
                    .frame(width: 56, height: 56)
                    .background(Color.primaryLight)
                    .clipShape(RoundedRectangle(cornerRadius: 8))

                Text("Add \(mealType.lowercased())")
                    .font(.bodyLarge)
                    .foregroundColor(.textSecondary)
            }

            Spacer()

            if showRemoveButton {
                Button(action: { onRemove?() }) {
                    Text("✕")
                        .font(.caption)
                        .fontWeight(.bold)
                        .foregroundColor(.error)
                        .frame(width: 28, height: 28)
                        .background(Color.errorLight)
                        .clipShape(RoundedRectangle(cornerRadius: 8))
                }
                .buttonStyle(PlainButtonStyle())
            }
        }
        .padding(12)
        .background(Color.cardBg)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .shadow(color: Color(red: 60/255, green: 50/255, blue: 40/255).opacity(0.06), radius: 8, x: 0, y: 2)
    }

    private func emojiForMealType(_ type: String) -> String {
        switch type.uppercased() {
        case "BREAKFAST": return "\u{1F95A}"
        case "LUNCH": return "\u{1F957}"
        case "DINNER": return "\u{1F37D}\u{FE0F}"
        case "SNACK": return "\u{1F35F}"
        default: return "\u{1F374}"
        }
    }
}

struct StatusCardView: View {
    let emoji: String
    let count: String
    let label: String

    var body: some View {
        VStack(spacing: 4) {
            Text(emoji)
                .font(.titleLarge)
            Text(count)
                .font(.titleLarge)
                .fontWeight(.bold)
                .foregroundColor(.textPrimary)
            Text(label)
                .font(.bodySmall)
                .foregroundColor(.textSecondary)
        }
        .frame(maxWidth: .infinity)
        .padding(12)
        .background(Color.cardBg)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .shadow(color: Color(red: 60/255, green: 50/255, blue: 40/255).opacity(0.06), radius: 8, x: 0, y: 2)
    }
}

struct CompactRecipeCardView: View {
    let recipe: Recipe

    var body: some View {
        Button(action: {}) {
            VStack(alignment: .leading, spacing: 0) {
                AsyncImage(url: URL(string: recipe.image)) { phase in
                    switch phase {
                    case .success(let image):
                        image.resizable().aspectRatio(contentMode: .fill)
                    default:
                        Color.primaryLight
                    }
                }
                .frame(width: 200, height: 130)
                .clipped()

                VStack(alignment: .leading, spacing: 4) {
                    Text(recipe.title)
                        .font(.bodyLarge)
                        .foregroundColor(.textPrimary)
                        .lineLimit(2)
                    Text("\(recipe.readyInMinutes) min")
                        .font(.bodySmall)
                        .foregroundColor(.textSecondary)
                }
                .padding(12)
            }
        }
        .frame(width: 200)
        .background(Color.cardBg)
        .clipShape(RoundedRectangle(cornerRadius: 20))
        .shadow(color: Color(red: 60/255, green: 50/255, blue: 40/255).opacity(0.09), radius: 16, x: 0, y: 4)
        .buttonStyle(PlainButtonStyle())
    }
}