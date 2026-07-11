import SwiftUI
import SharedLogic

struct PlannerView: View {
    @StateObject private var viewModel = MealPlanViewModelWrapper()
    @State private var selectedDay = 0

    private let dayLabels = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"]
    private let mealTypes = ["Breakfast", "Lunch", "Dinner"]

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                Text("Meal Plan")
                    .font(.displayLarge)
                    .foregroundColor(.textPrimary)
                    .padding(.horizontal, 16)
                    .padding(.top, 16)

                // Day strip
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(Array(dayLabels.enumerated()), id: \.offset) { index, label in
                            VStack(spacing: 4) {
                                Text(String(label.prefix(1)))
                                    .font(.labelLarge)
                                    .foregroundColor(index == selectedDay ? .white : .textPrimary)
                                    .frame(width: 44, height: 44)
                                    .background(index == selectedDay ? Color.primary : Color.white)
                                    .clipShape(Circle())
                                Text(label)
                                    .font(.bodySmall)
                                    .foregroundColor(index == selectedDay ? .primary : .textSecondary)
                            }
                            .onTapGesture {
                                selectedDay = index
                                viewModel.selectDay(day: Int32(index))
                            }
                        }
                    }
                    .padding(.horizontal, 16)
                }
                .padding(.top, 16)

                Text("Meals")
                    .font(.headlineLarge)
                    .foregroundColor(.textPrimary)
                    .padding(.horizontal, 16)
                    .padding(.top, 24)
                    .padding(.bottom, 12)

                if viewModel.isLoading {
                    ProgressView()
                        .frame(maxWidth: .infinity)
                        .padding()
                }

                ForEach(mealTypes, id: \.self) { type in
                    let dayMeals = viewModel.meals.filter { $0.dayOfWeek == Int32(selectedDay) }
                    let meal = dayMeals.first { $0.mealType == type.uppercased() }

                    VStack(alignment: .leading, spacing: 4) {
                        Text(type)
                            .font(.labelLarge)
                            .foregroundColor(.textSecondary)
                    }
                    .padding(.horizontal, 16)
                    .padding(.top, 8)
                    .padding(.bottom, 4)

                    MealSlotView(recipe: nil, mealType: type)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 4)
                }
            }
        }
        .background(Color.background)
        .onAppear {
            viewModel.loadMealPlans()
        }
    }
}