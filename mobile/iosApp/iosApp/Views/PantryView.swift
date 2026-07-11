import SwiftUI
import SharedLogic

struct PantryView: View {
    @StateObject private var viewModel = PantryViewModelWrapper()
    @State private var showAddSheet = false

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("Pantry")
                .font(.displayLarge)
                .foregroundColor(.textPrimary)
                .padding(.horizontal, 16)
                .padding(.top, 16)

            Text("\(viewModel.items.count) ingredients")
                .font(.bodyMedium)
                .foregroundColor(.textSecondary)
                .padding(.horizontal, 16)

            if viewModel.isLoading {
                Spacer()
                ProgressView()
                    .frame(maxWidth: .infinity)
                Spacer()
            } else if viewModel.items.isEmpty {
                Spacer()
                Text("Your pantry is empty")
                    .font(.bodyLarge)
                    .foregroundColor(.textSecondary)
                    .frame(maxWidth: .infinity)
                Spacer()
            } else {
                ScrollView {
                    LazyVStack(spacing: 8) {
                        ForEach(viewModel.items, id: \.id) { item in
                            HStack {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text(item.name)
                                        .font(.bodyLarge)
                                        .foregroundColor(.textPrimary)
                                        .lineLimit(1)
                                    Text("\(String(format: "%.1f", item.amount)) \(item.unit)")
                                        .font(.bodySmall)
                                        .foregroundColor(.textSecondary)
                                }
                                Spacer()
                                Button(action: { viewModel.deleteItem(id: item.id) }) {
                                    Text("Delete")
                                        .font(.bodySmall)
                                        .foregroundColor(.error)
                                }
                            }
                            .padding(12)
                            .background(Color.cardBg)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                            .shadow(color: Color(red: 60/255, green: 50/255, blue: 40/255).opacity(0.06), radius: 4, x: 0, y: 2)
                            .padding(.horizontal, 16)
                        }
                    }
                    .padding(.vertical, 8)
                }
            }

            Button(action: { showAddSheet = true }) {
                Text("Add Ingredient")
                    .font(.labelLarge)
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .frame(height: 48)
                    .background(Color.primary)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
            }
            .padding(16)
        }
        .background(Color.background)
        .onAppear {
            viewModel.loadItems()
        }
        .sheet(isPresented: $showAddSheet) {
            AddPantryItemView { name, amount, unit in
                viewModel.addItem(name: name, amount: amount, unit: unit)
            }
        }
    }
}

struct AddPantryItemView: View {
    @Environment(\.dismiss) private var dismiss
    @State private var name = ""
    @State private var amount = ""
    @State private var unit = ""
    let onAdd: (String, Double, String) -> Void

    var body: some View {
        NavigationStack {
            Form {
                TextField("Ingredient name", text: $name)
                HStack {
                    TextField("Amount", text: $amount)
                        .keyboardType(.decimalPad)
                    TextField("Unit", text: $unit)
                }
            }
            .navigationTitle("Add Ingredient")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Add") {
                        onAdd(name, Double(amount) ?? 1.0, unit)
                        dismiss()
                    }
                    .disabled(name.isEmpty)
                }
            }
        }
    }
}