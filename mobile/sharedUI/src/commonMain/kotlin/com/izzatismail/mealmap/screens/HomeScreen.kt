package com.izzatismail.mealmap.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.izzatismail.mealmap.ui.components.DayItem
import com.izzatismail.mealmap.ui.components.MealSlot
import com.izzatismail.mealmap.ui.components.RecipeCardCompact
import com.izzatismail.mealmap.ui.components.StatusCard
import com.izzatismail.mealmap.ui.components.WeekDayStrip
import com.izzatismail.mealmap.ui.theme.ColorBg
import com.izzatismail.mealmap.ui.theme.ColorPrimary
import com.izzatismail.mealmap.ui.theme.ColorPrimaryLight
import com.izzatismail.mealmap.ui.theme.ColorTextPrimary
import com.izzatismail.mealmap.ui.theme.ColorTextSecondary
import com.izzatismail.mealmap.ui.theme.ColorTextTertiary
import com.izzatismail.mealmap.ui.theme.ColorWhite
import com.izzatismail.mealmap.viewmodel.FavoriteViewModel
import com.izzatismail.mealmap.viewmodel.MealPlanViewModel
import com.izzatismail.mealmap.viewmodel.PantryViewModel
import com.izzatismail.mealmap.viewmodel.RecipeViewModel
import com.izzatismail.mealmap.viewmodel.ShoppingListViewModel
import org.koin.compose.koinInject

private val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

@Composable
fun HomeScreen(
    onNavigateToRecipes: () -> Unit,
    onRecipeClick: (Long) -> Unit,
    recipeViewModel: RecipeViewModel = koinInject(),
    mealPlanViewModel: MealPlanViewModel = koinInject(),
    shoppingListViewModel: ShoppingListViewModel = koinInject(),
    pantryViewModel: PantryViewModel = koinInject(),
    favoriteViewModel: FavoriteViewModel = koinInject(),
) {
    val recipeState by recipeViewModel.listState.collectAsState()
    val mealPlanState by mealPlanViewModel.state.collectAsState()
    val shoppingState by shoppingListViewModel.state.collectAsState()
    val pantryState by pantryViewModel.state.collectAsState()
    val favoriteState by favoriteViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        recipeViewModel.loadCachedRecipes()
        mealPlanViewModel.loadMealPlans()
        shoppingListViewModel.loadCurrentList()
        pantryViewModel.loadItems()
        favoriteViewModel.loadFavoriteIds()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBg),
    ) {
        // Section 1: Greeting header
        item {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
                Text(
                    text = "Good morning \uD83D\uDC4B",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColorTextSecondary,
                )
                Text(
                    text = "What's cooking today?",
                    style = MaterialTheme.typography.displayLarge,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section 2: Week day strip
        item {
            val days = dayLabels.mapIndexed { index, label ->
                DayItem(label = label, dayIndex = index, isToday = index == 0)
            }
            WeekDayStrip(
                days = days,
                selectedDay = mealPlanState.selectedDay,
                onDaySelected = { mealPlanViewModel.selectDay(it) },
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Section 3: Today's meals
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Today's Meals",
                    style = MaterialTheme.typography.headlineLarge,
                )
                Text(
                    text = "Edit",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorPrimary,
                    modifier = Modifier.clickable { },
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        val todayMeals = mealPlanState.currentWeekMeals.filter { it.dayOfWeek == 0 }
        val mealTypes = listOf("BREAKFAST", "LUNCH", "DINNER")

        mealTypes.forEach { type ->
            val meal = todayMeals.find { it.mealType == type }
            item {
                MealSlot(
                    recipe = null,
                    mealType = type,
                    onClick = {
                        if (meal != null) onRecipeClick(meal.recipeId)
                        else onNavigateToRecipes()
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // Section 4: Pantry & Shopping status cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Pantry & Shopping",
                    style = MaterialTheme.typography.headlineLarge,
                )
                Text(
                    text = "View",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorPrimary,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatusCard(
                    emoji = "\uD83E\uDDEA",
                    count = "${pantryState.items.size}",
                    label = "in pantry",
                    modifier = Modifier.weight(1f),
                )
                StatusCard(
                    emoji = "\uD83D\uDED2",
                    count = "${shoppingState.shoppingList?.items?.size ?: 0}",
                    label = "to buy",
                    modifier = Modifier.weight(1f),
                )
                StatusCard(
                    emoji = "\u2705",
                    count = "${favoriteState.favoriteIds.size}",
                    label = "favorites",
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Section 5: Try Something New (first cached recipe as suggestion)
        if (recipeState.recipes.isNotEmpty()) {
            val suggestion = recipeState.recipes.firstOrNull()
            if (suggestion != null) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Try Something New",
                            style = MaterialTheme.typography.headlineLarge,
                        )
                        Text(
                            text = "More",
                            style = MaterialTheme.typography.bodySmall,
                            color = ColorPrimary,
                            modifier = Modifier.clickable { onNavigateToRecipes() },
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable { onRecipeClick(suggestion.spoonacularId) },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = ColorWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    ) {
                        Column {
                            AsyncImage(
                                model = suggestion.image,
                                contentDescription = suggestion.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .background(ColorPrimaryLight),
                                contentScale = ContentScale.Crop,
                            )
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = suggestion.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${suggestion.readyInMinutes} min  \u00B7  ${suggestion.servings} servings",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ColorTextSecondary,
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Section 6: Quick & Easy horizontal scroll
        if (recipeState.recipes.size > 1) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Quick & Easy",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    Text(
                        text = "More",
                        style = MaterialTheme.typography.bodySmall,
                        color = ColorPrimary,
                        modifier = Modifier.clickable { onNavigateToRecipes() },
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                val quickRecipes = recipeState.recipes
                    .filter { it.readyInMinutes in 1..30 }
                    .take(10)
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(quickRecipes) { recipe ->
                        RecipeCardCompact(
                            recipe = recipe,
                            onClick = { onRecipeClick(recipe.spoonacularId) },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}