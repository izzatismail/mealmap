package com.izzatismail.mealmap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.izzatismail.mealmap.ui.theme.ColorPrimary
import com.izzatismail.mealmap.ui.theme.ColorPrimaryLight
import com.izzatismail.mealmap.ui.theme.ColorTextPrimary
import com.izzatismail.mealmap.ui.theme.ColorTextSecondary
import com.izzatismail.mealmap.ui.theme.ColorWhite

@Composable
fun StatusCard(
    emoji: String,
    count: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ColorWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = emoji, fontSize = MaterialTheme.typography.titleLarge.fontSize)
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = ColorTextPrimary,
                modifier = Modifier.padding(top = 4.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = ColorTextSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}