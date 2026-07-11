package com.izzatismail.mealmap.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.izzatismail.mealmap.screens.FavoritesScreen
import com.izzatismail.mealmap.screens.HomeScreen
import com.izzatismail.mealmap.screens.LoginScreen
import com.izzatismail.mealmap.screens.PantryScreen
import com.izzatismail.mealmap.screens.PlannerScreen
import com.izzatismail.mealmap.screens.RecipeDetailScreen
import com.izzatismail.mealmap.screens.RecipeListScreen
import com.izzatismail.mealmap.screens.RegisterScreen
import com.izzatismail.mealmap.screens.ShoppingListScreen
import com.izzatismail.mealmap.ui.theme.*
import com.izzatismail.mealmap.viewmodel.AuthViewModel
import org.koin.compose.koinInject

data class BottomNavItem(
    val label: String,
    val icon: String,
    val screen: Screen,
)

private val bottomNavItems = listOf(
    BottomNavItem("Home", "\uD83C\uDFE0", Screen.Home),
    BottomNavItem("Recipes", "\uD83D\uDCD6", Screen.RecipeList),
    BottomNavItem("Planner", "\uD83D\uDCC5", Screen.Planner),
    BottomNavItem("Shopping", "\uD83D\uDED2", Screen.ShoppingList),
    BottomNavItem("Pantry", "\uD83C\uDF72", Screen.Pantry),
)

@Composable
fun AppNavigation(authViewModel: AuthViewModel = koinInject()) {
    val authState by authViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.checkAuth()
    }

    if (authState.isCheckingAuth) return

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (authState.isLoggedIn) Screen.Main else Screen.Login,
    ) {
        composable<Screen.Login> {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Main) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                },
            )
        }
        composable<Screen.Register> {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Main) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                },
            )
        }
        composable<Screen.Main> {
            MainTabs(
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail(recipeId))
                },
                onBackToLogin = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Main) { inclusive = true }
                    }
                },
            )
        }
        composable<Screen.RecipeDetail> { backStackEntry ->
            val detail: Screen.RecipeDetail = backStackEntry.toRoute()
            RecipeDetailScreen(
                recipeId = detail.recipeId,
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}

@Composable
fun MainTabs(
    onRecipeClick: (Long) -> Unit,
    onBackToLogin: () -> Unit,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = ColorWhite,
            ) {
                bottomNavItems.forEachIndexed { index, item ->
                    val isSelected = index == selectedTab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            selectedTab = index
                            tabNavController.navigate(item.screen) {
                                popUpTo(tabNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Text(
                                text = item.icon,
                                fontSize = 20.sp,
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ColorPrimary,
                            selectedTextColor = ColorPrimary,
                            unselectedIconColor = ColorTextTertiary,
                            unselectedTextColor = ColorTextTertiary,
                            indicatorColor = ColorWhite,
                        ),
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = tabNavController,
            startDestination = Screen.Home,
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            composable<Screen.Home> {
                HomeScreen(
                    onNavigateToRecipes = { selectedTab = 1 },
                    onRecipeClick = onRecipeClick,
                )
            }
            composable<Screen.RecipeList> {
                RecipeListScreen(
                    onRecipeClick = { recipeId ->
                        onRecipeClick(recipeId)
                    },
                )
            }
            composable<Screen.Planner> {
                PlannerScreen(onRecipeClick = onRecipeClick)
            }
            composable<Screen.ShoppingList> {
                ShoppingListScreen()
            }
            composable<Screen.Pantry> {
                PantryScreen()
            }
        }
    }
}