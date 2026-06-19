package com.izzatismail.mealmap.service

import com.izzatismail.mealmap.dto.RecipeDto
import com.izzatismail.mealmap.dto.spoonacular.SpoonacularFindByIngredientsResult
import com.izzatismail.mealmap.dto.spoonacular.SpoonacularRecipeResponse
import com.izzatismail.mealmap.dto.spoonacular.SpoonacularSearchResult
import com.izzatismail.mealmap.entity.Recipe
import com.izzatismail.mealmap.exception.ResourceNotFoundException
import com.izzatismail.mealmap.exception.SpoonacularApiException
import com.izzatismail.mealmap.repository.RecipeRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.time.LocalDateTime

@Service
@Transactional
class SpoonacularService(
    private val recipeRepository: RecipeRepository,
    private val restTemplate: RestTemplate,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val objectMapper = ObjectMapper()

    @Value("\${spoonacular.api.key}")
    private lateinit var apiKey: String

    @Value("\${spoonacular.api.base-url}")
    private lateinit var baseUrl: String

    fun searchRecipes(query: String, limit: Int = 10): List<RecipeDto> {
        val cached = recipeRepository.findByTitleContainingIgnoreCase(query)
        if (cached.size >= limit) {
            log.info("Returning {} cached results for query '{}'", cached.size, query)
            return cached.take(limit).map { RecipeDto.fromEntity(it) }
        }

        log.info("Calling Spoonacular search API for query '{}'", query)
        val url = UriComponentsBuilder.fromUriString("$baseUrl/recipes/complexSearch")
            .queryParam("query", query)
            .queryParam("number", limit)
            .queryParam("apiKey", apiKey)
            .toUriString()

        val response = try {
            restTemplate.getForObject(url, SpoonacularSearchResult::class.java)
        } catch (e: RestClientException) {
            log.error("Spoonacular search API call failed for query '{}': {}", query, e.message)
            throw SpoonacularApiException("Failed to search recipes", e)
        }

        if (response == null) {
            log.warn("Spoonacular search returned null for query '{}'", query)
            return cached.map { RecipeDto.fromEntity(it) }
        }

        val newRecipes = response.results.mapNotNull { summary ->
            if (recipeRepository.findBySpoonacularId(summary.id) != null) return@mapNotNull null
            recipeRepository.save(
                Recipe(
                    spoonacularId = summary.id,
                    title = summary.title,
                    image = summary.image,
                    imageType = summary.imageType,
                    cachedAt = LocalDateTime.now(),
                )
            )
        }

        log.info("Cached {} new recipes from search query '{}'", newRecipes.size, query)
        return (cached + newRecipes).take(limit).map { RecipeDto.fromEntity(it) }
    }

    fun getRecipeById(id: Long): RecipeDto {
        val cached = recipeRepository.findBySpoonacularId(id)
        if (cached != null && cached.summary.isNotBlank()) {
            log.info("Returning cached recipe detail for spoonacularId {}", id)
            return RecipeDto.fromEntity(cached)
        }

        if (cached != null) {
            recipeRepository.delete(cached)
            recipeRepository.flush()
        }

        log.info("Calling Spoonacular detail API for recipe {}", id)
        val url = UriComponentsBuilder.fromUriString("$baseUrl/recipes/$id/information")
            .queryParam("apiKey", apiKey)
            .toUriString()

        val response = try {
            restTemplate.getForObject(url, SpoonacularRecipeResponse::class.java)
        } catch (e: HttpClientErrorException.NotFound) {
            throw ResourceNotFoundException("Recipe not found with id: $id")
        } catch (e: RestClientException) {
            log.error("Spoonacular detail API call failed for recipe {}: {}", id, e.message)
            throw SpoonacularApiException("Failed to fetch recipe details", e)
        }

        if (response == null) {
            throw ResourceNotFoundException("Recipe not found with id: $id")
        }

        val recipe = recipeRepository.save(response.toEntity())
        log.info("Cached recipe detail for spoonacularId {}", id)
        return RecipeDto.fromEntity(recipe)
    }

    fun findByIngredients(ingredients: List<String>): List<RecipeDto> {
        val ingredientsStr = ingredients.joinToString(",")
        log.info("Calling Spoonacular findByIngredients API with: {}", ingredientsStr)
        val url = UriComponentsBuilder.fromUriString("$baseUrl/recipes/findByIngredients")
            .queryParam("ingredients", ingredientsStr)
            .queryParam("number", 10)
            .queryParam("apiKey", apiKey)
            .toUriString()

        val response = try {
            restTemplate.getForObject(url, Array<SpoonacularFindByIngredientsResult>::class.java)
        } catch (e: RestClientException) {
            log.error("Spoonacular findByIngredients API call failed: {}", e.message)
            throw SpoonacularApiException("Failed to find recipes by ingredients", e)
        }

        if (response == null) return emptyList()

        return response.map { result ->
            val existing = recipeRepository.findBySpoonacularId(result.id)
            if (existing != null) {
                RecipeDto.fromEntity(existing)
            } else {
                val recipe = recipeRepository.save(
                    Recipe(
                        spoonacularId = result.id,
                        title = result.title,
                        image = result.image,
                        imageType = result.imageType,
                        cachedAt = LocalDateTime.now(),
                    )
                )
                RecipeDto.fromEntity(recipe)
            }
        }
    }

    fun getCachedRecipes(): List<RecipeDto> {
        return recipeRepository.findAll().map { RecipeDto.fromEntity(it) }
    }

    private fun SpoonacularRecipeResponse.toEntity() = Recipe(
        spoonacularId = id,
        title = title,
        image = image,
        imageType = imageType,
        servings = servings,
        readyInMinutes = readyInMinutes,
        sourceUrl = sourceUrl,
        sourceName = sourceName,
        creditsText = creditsText,
        summary = summary,
        instructions = instructions,
        healthScore = healthScore,
        spoonacularScore = spoonacularScore,
        pricePerServing = pricePerServing,
        cheap = cheap,
        dairyFree = dairyFree,
        glutenFree = glutenFree,
        vegan = vegan,
        vegetarian = vegetarian,
        veryHealthy = veryHealthy,
        veryPopular = veryPopular,
        sustainable = sustainable,
        whole30 = whole30,
        cuisines = objectMapper.writeValueAsString(cuisines),
        dishTypes = objectMapper.writeValueAsString(dishTypes),
        diets = objectMapper.writeValueAsString(diets),
        ingredients = objectMapper.writeValueAsString(extendedIngredients),
        cachedAt = LocalDateTime.now(),
    )
}