import SwiftUI

struct MainTabView: View {
    @EnvironmentObject var authViewModel: AuthViewModelWrapper
    @State private var selectedTab = 0

    var body: some View {
        TabView(selection: $selectedTab) {
            NavigationStack {
                HomeView()
            }
            .tabItem {
                TabLabel(emoji: "\u{1F3E0}", label: "Home", isSelected: selectedTab == 0)
            }
            .tag(0)

            NavigationStack {
                RecipeListView()
            }
            .tabItem {
                TabLabel(emoji: "\u{1F4D6}", label: "Recipes", isSelected: selectedTab == 1)
            }
            .tag(1)

            NavigationStack {
                PlannerView()
            }
            .tabItem {
                TabLabel(emoji: "\u{1F4C5}", label: "Planner", isSelected: selectedTab == 2)
            }
            .tag(2)

            NavigationStack {
                ShoppingListView()
            }
            .tabItem {
                TabLabel(emoji: "\u{1F6D2}", label: "Shopping", isSelected: selectedTab == 3)
            }
            .tag(3)

            NavigationStack {
                PantryView()
            }
            .tabItem {
                TabLabel(emoji: "\u{1F372}", label: "Pantry", isSelected: selectedTab == 4)
            }
            .tag(4)
        }
        .tint(Color.primary)
    }
}

private struct TabLabel: View {
    let emoji: String
    let label: String
    let isSelected: Bool

    var body: some View {
        VStack(spacing: 2) {
            Text(emoji)
                .font(.system(size: 20))
            Text(label)
                .font(.labelSmall)
                .foregroundColor(isSelected ? .primary : .textTertiary)
        }
    }
}