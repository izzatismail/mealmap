package com.izzatismail.mealmap.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.izzatismail.mealmap.ui.components.DayItem
import com.izzatismail.mealmap.ui.components.MealSlot
import com.izzatismail.mealmap.ui.components.SearchBar
import com.izzatismail.mealmap.ui.components.WeekDayStrip
import com.izzatismail.mealmap.ui.components.mealTypeEmoji
import com.izzatismail.mealmap.ui.components.mealTypeLabel
import com.izzatismail.mealmap.ui.theme.ColorBg
import com.izzatismail.mealmap.ui.theme.ColorPrimary
import com.izzatismail.mealmap.ui.theme.ColorTextSecondary
import com.izzatismail.mealmap.viewmodel.MealPlanViewModel
import com.izzatismail.mealmap.viewmodel.RecipeViewModel
import org.koin.compose.koinInject

private val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val mealTypes = listOf("BREAKFAST", "LUNCH", "DINNER")

@Composable
fun PlannerScreen(
    onRecipeClick: (Long) -> Unit = {},
    mealPlanViewModel: MealPlanViewModel = koinInject(),
    recipeViewModel: RecipeViewModel = koinInject(),
) {
    val state by mealPlanViewModel.state.collectAsState()
    val recipeState by recipeViewModel.listState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var addingMealType by remember { mutableStateOf("") }
    var showRemoveDialog by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        mealPlanViewModel.loadMealPlans()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBg),
    ) {
        item {
            Text(
                text = "Meal Plan",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
            )
        }

        item {
            val days = dayLabels.mapIndexed { index, label ->
                DayItem(label = label, dayIndex = index, isToday = index == MealPlanViewModel.currentDayOfWeek())
            }
            WeekDayStrip(
                days = days,
                selectedDay = state.selectedDay,
                onDaySelected = { mealPlanViewModel.selectDay(it) },
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text(
                text = "Meals",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (state.isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        state.error?.let { error ->
            item {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }

        val dayMeals = state.currentWeekMeals.filter { it.dayOfWeek == state.selectedDay }

        mealTypes.forEach { type ->
            val meal = dayMeals.find { it.mealType == type }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${mealTypeEmoji(type)} ${mealTypeLabel(type)}",
                        style = MaterialTheme.typography.labelLarge,
                        color = ColorTextSecondary,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
            }
            item {
                MealSlot(
                    recipe = null,
                    mealType = type,
                    hasMeal = meal != null,
                    mealTitle = meal?.recipeTitle ?: "",
                    onClick = {
                        if (meal != null) {
                            onRecipeClick(meal.recipeId)
                        } else {
                            addingMealType = type
                            showAddDialog = true
                        }
                    },
                    onRemove = {
                        if (meal != null) {
                            showRemoveDialog = meal.id
                        }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showRemoveDialog != null) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = null },
            title = { Text("Remove Meal") },
            text = { Text("Are you sure you want to remove this meal?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRemoveDialog?.let { mealPlanViewModel.removeMeal(it) }
                        showRemoveDialog = null
                    },
                ) {
                    Text("Remove", color = ColorPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = null }) {
                    Text("Cancel")
                }
            },
        )
    }

    if (showAddDialog) {
        AddMealDialog(
            query = recipeState.query,
            onQueryChange = { recipeViewModel.onQueryChanged(it) },
            onSearch = { recipeViewModel.search(recipeState.query) },
            searchResults = recipeState.recipes,
            onSelectRecipe = { recipeId ->
                mealPlanViewModel.addMealToDay(
                    recipeId = recipeId,
                    mealType = addingMealType,
                    dayOfWeek = state.selectedDay,
                )
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false },
        )
    }
}

@Composable
private fun AddMealDialog(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    searchResults: List<com.izzatismail.mealmap.model.Recipe>,
    onSelectRecipe: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBg.copy(alpha = 0.95f))
            .clickable(onClick = onDismiss),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clickable(enabled = false) {},
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "Add Recipe",
                style = MaterialTheme.typography.displayLarge,
            )
            Spacer(modifier = Modifier.height(16.dp))
            SearchBar(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
            )
            Spacer(modifier = Modifier.height(12.dp))
            searchResults.take(10).forEach { recipe ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onSelectRecipe(recipe.spoonacularId) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(12.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}