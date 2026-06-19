package com.izzatismail.mealmap.dto.spoonacular

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpoonacularFindByIngredientsResult(
    val id: Long = 0,
    val title: String = "",
    val image: String = "",
    val imageType: String = "",
)