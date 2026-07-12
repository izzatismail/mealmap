package com.izzatismail.mealmap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.izzatismail.mealmap.model.Recipe
import com.izzatismail.mealmap.ui.theme.ColorError
import com.izzatismail.mealmap.ui.theme.ColorErrorLight
import com.izzatismail.mealmap.ui.theme.ColorPrimary
import com.izzatismail.mealmap.ui.theme.ColorPrimaryLight
import com.izzatismail.mealmap.ui.theme.ColorTextPrimary
import com.izzatismail.mealmap.ui.theme.ColorTextSecondary
import com.izzatismail.mealmap.ui.theme.ColorWhite

fun mealTypeLabel(type: String): String {
    return when (type.uppercase()) {
        "BREAKFAST" -> "Breakfast"
        "LUNCH" -> "Lunch"
        "DINNER" -> "Dinner"
        "SNACK" -> "Snack"
        "DESSERT" -> "Dessert"
        else -> type
    }
}

fun mealTypeEmoji(type: String): String {
    return when (type.uppercase()) {
        "BREAKFAST" -> "\uD83E\uDD5A"
        "LUNCH" -> "\uD83E\uDD57"
        "DINNER" -> "\uD83C\uDF7D\uFE0F"
        "SNACK" -> "\uD83C\uDF5F"
        "DESSERT" -> "\uD83C\uDF6E"
        else -> "\uD83C\uDF74"
    }
}

@Composable
fun MealSlot(
    recipe: Recipe?,
    mealType: String,
    hasMeal: Boolean = false,
    mealTitle: String = "",
    onClick: () -> Unit,
    onRemove: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ColorWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (recipe != null) {
                AsyncImage(
                    model = recipe.image,
                    contentDescription = recipe.title,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${recipe.readyInMinutes} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = ColorTextSecondary,
                    )
                }
            } else if (hasMeal) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ColorPrimaryLight),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = mealTypeEmoji(mealType),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mealTitle,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ColorPrimaryLight),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.titleLarge,
                        color = ColorPrimary,
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Add meal",
                        style = MaterialTheme.typography.bodyLarge,
                        color = ColorTextSecondary,
                    )
                }
            }
            if (hasMeal) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ColorErrorLight)
                        .clickable(onClick = onRemove),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "✕",
                        color = ColorError,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                }
            }
        }
    }
}