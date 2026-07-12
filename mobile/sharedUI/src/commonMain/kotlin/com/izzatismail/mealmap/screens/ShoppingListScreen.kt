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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.izzatismail.mealmap.model.ShoppingItemDto
import com.izzatismail.mealmap.ui.theme.ColorBg
import com.izzatismail.mealmap.ui.theme.ColorBorder
import com.izzatismail.mealmap.ui.theme.ColorPrimary
import com.izzatismail.mealmap.ui.theme.ColorTextPrimary
import com.izzatismail.mealmap.ui.theme.ColorTextSecondary
import com.izzatismail.mealmap.ui.theme.ColorTextTertiary
import com.izzatismail.mealmap.ui.theme.ColorWhite
import com.izzatismail.mealmap.viewmodel.ShoppingListViewModel
import org.koin.compose.koinInject

@Composable
fun ShoppingListScreen(
    shoppingListViewModel: ShoppingListViewModel = koinInject(),
) {
    val state by shoppingListViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        shoppingListViewModel.loadCurrentList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBg),
    ) {
        Text(
            text = "Shopping List",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
        )

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            return
        }

        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp),
            )
        }

        val list = state.shoppingList

        if (list == null || list.items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No shopping list yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = ColorTextSecondary,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Create a meal plan first to generate your list",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ColorTextTertiary,
                    )
                }
            }
        } else {
            val checkedItems = list.items.filter { it.isChecked }
            val uncheckedItems = list.items.filter { !it.isChecked }

            Text(
                text = "${checkedItems.size}/${list.items.size} items",
                style = MaterialTheme.typography.bodyMedium,
                color = ColorTextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                val grouped = uncheckedItems.groupBy { it.category.ifEmpty { "Other" } }
                grouped.forEach { (category, items) ->
                    item {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.labelLarge,
                            color = ColorPrimary,
                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                        )
                    }
                    items(items) { item ->
                        ShoppingItemRow(
                            item = item,
                            onToggle = { shoppingListViewModel.toggleItem(item.id) },
                        )
                    }
                }

                if (checkedItems.isNotEmpty()) {
                    item {
                        Text(
                            text = "Checked",
                            style = MaterialTheme.typography.labelLarge,
                            color = ColorTextTertiary,
                            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                        )
                    }
                    items(checkedItems) { item ->
                        ShoppingItemRow(
                            item = item,
                            onToggle = { shoppingListViewModel.toggleItem(item.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShoppingItemRow(
    item: ShoppingItemDto,
    onToggle: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ColorWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = ColorPrimary),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (item.isChecked) ColorTextTertiary else ColorTextPrimary,
                )
                Text(
                    text = "${item.amount} ${item.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorTextSecondary,
                )
            }
        }
    }
}