package com.izzatismail.mealmap.dto.spoonacular

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpoonacularSearchResult(
    val offset: Int = 0,
    val number: Int = 0,
    val results: List<RecipeSummary> = emptyList(),
    val totalResults: Int = 0,
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class RecipeSummary(
        val id: Long = 0,
        val title: String = "",
        val image: String = "",
        val imageType: String = "",
    )
}