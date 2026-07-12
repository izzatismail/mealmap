import SwiftUI
import SharedLogic

class MealPlanViewModelWrapper: ObservableObject {
    private let viewModel: MealPlanViewModel

    @Published var meals: [PlannedMealDto] = []
    @Published var selectedDay: Int32 = 0
    @Published var isLoading = false
    @Published var error: String? = nil

    init() {
        self.viewModel = IosKoinInitializer.companion.provideMealPlanViewModel()
    }

    func loadMealPlans() {
        isLoading = true; error = nil
        viewModel.loadMealPlansAsync { [weak self] state in
            DispatchQueue.main.async {
                guard let self = self else { return }
                self.meals = state.currentWeekMeals
                self.selectedDay = state.selectedDay
                self.isLoading = state.isLoading
                self.error = state.error
            }
        }
    }

    func selectDay(day: Int32) {
        viewModel.selectDay(day: day)
    }

    func addMealToDay(recipeId: Int64, mealType: String, dayOfWeek: Int32, servings: Int32 = 1) {
        viewModel.addMealToDay(recipeId: recipeId, mealType: mealType, dayOfWeek: dayOfWeek, servings: servings)
    }

    func removeMeal(plannedMealId: Int64) {
        viewModel.removeMeal(plannedMealId: plannedMealId)
    }
}