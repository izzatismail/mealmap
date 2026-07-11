package com.izzatismail.mealmap.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.izzatismail.mealmap.ui.theme.ColorTextSecondary

@Composable
fun PlannerScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Meal Planner",
            style = MaterialTheme.typography.headlineLarge,
        )
        Text(
            text = "Plan your week — coming soon",
            style = MaterialTheme.typography.bodyMedium,
            color = ColorTextSecondary,
        )
    }
}

@Composable
fun ShoppingListScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Shopping List",
            style = MaterialTheme.typography.headlineLarge,
        )
        Text(
            text = "Your generated list — coming soon",
            style = MaterialTheme.typography.bodyMedium,
            color = ColorTextSecondary,
        )
    }
}

@Composable
fun PantryScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Pantry",
            style = MaterialTheme.typography.headlineLarge,
        )
        Text(
            text = "Track your ingredients — coming soon",
            style = MaterialTheme.typography.bodyMedium,
            color = ColorTextSecondary,
        )
    }
}