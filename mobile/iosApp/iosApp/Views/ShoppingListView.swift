import SwiftUI
import SharedLogic

struct ShoppingListView: View {
    @StateObject private var viewModel = ShoppingListViewModelWrapper()

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("Shopping List")
                .font(.displayLarge)
                .foregroundColor(.textPrimary)
                .padding(.horizontal, 16)
                .padding(.top, 16)

            if viewModel.isLoading {
                Spacer()
                ProgressView()
                    .frame(maxWidth: .infinity)
                Spacer()
            } else if viewModel.items.isEmpty {
                Spacer()
                VStack(spacing: 8) {
                    Text("No shopping list yet")
                        .font(.bodyLarge)
                        .foregroundColor(.textSecondary)
                    Text("Create a meal plan first to generate your list")
                        .font(.bodyMedium)
                        .foregroundColor(.textTertiary)
                }
                .frame(maxWidth: .infinity)
                Spacer()
            } else {
                let checkedCount = viewModel.items.filter { $0.isChecked }.count
                Text("\(checkedCount)/\(viewModel.items.count) items")
                    .font(.bodyMedium)
                    .foregroundColor(.textSecondary)
                    .padding(.horizontal, 16)
                    .padding(.top, 8)

                ScrollView {
                    LazyVStack(spacing: 4) {
                        let unchecked = viewModel.items.filter { !$0.isChecked }
                        let checked = viewModel.items.filter { $0.isChecked }

                        let grouped = Dictionary(grouping: unchecked) {
                            $0.category.isEmpty ? "Other" : $0.category
                        }

                        ForEach(Array(grouped.keys.sorted()), id: \.self) { category in
                            Text(category)
                                .font(.labelLarge)
                                .foregroundColor(.primary)
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .padding(.horizontal, 16)
                                .padding(.top, 12)
                                .padding(.bottom, 4)

                            ForEach(grouped[category] ?? [], id: \.id) { item in
                                ShoppingItemRowView(item: item, onToggle: {
                                    viewModel.toggleItem(itemId: item.id)
                                })
                            }
                        }

                        if !checked.isEmpty {
                            Text("Checked")
                                .font(.labelLarge)
                                .foregroundColor(.textTertiary)
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .padding(.horizontal, 16)
                                .padding(.top, 16)
                                .padding(.bottom, 4)

                            ForEach(checked, id: \.id) { item in
                                ShoppingItemRowView(item: item, onToggle: {
                                    viewModel.toggleItem(itemId: item.id)
                                })
                            }
                        }
                    }
                    .padding(.vertical, 8)
                }
            }
        }
        .background(Color.background)
        .onAppear {
            viewModel.loadCurrentList()
        }
    }
}

struct ShoppingItemRowView: View {
    let item: ShoppingItemDto
    let onToggle: () -> Void

    var body: some View {
        Button(action: onToggle) {
            HStack(spacing: 8) {
                Image(systemName: item.isChecked ? "checkmark.circle.fill" : "circle")
                    .foregroundColor(item.isChecked ? .primary : .border)
                    .font(.system(size: 20))

                VStack(alignment: .leading, spacing: 2) {
                    Text(item.name)
                        .font(.bodyLarge)
                        .foregroundColor(item.isChecked ? .textTertiary : .textPrimary)
                        .strikethrough(item.isChecked)
                        .lineLimit(1)
                    Text("\(String(format: "%.1f", item.amount)) \(item.unit)")
                        .font(.bodySmall)
                        .foregroundColor(.textSecondary)
                }
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 10)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color.cardBg)
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .shadow(color: Color(red: 60/255, green: 50/255, blue: 40/255).opacity(0.06), radius: 4, x: 0, y: 2)
        }
        .buttonStyle(PlainButtonStyle())
        .padding(.horizontal, 16)
        .padding(.vertical, 2)
    }
}