package com.izzatismail.mealmap.service

import com.izzatismail.mealmap.dto.PantryItemDto
import com.izzatismail.mealmap.entity.PantryItem
import com.izzatismail.mealmap.exception.ResourceNotFoundException
import com.izzatismail.mealmap.repository.PantryItemRepository
import com.izzatismail.mealmap.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PantryService(
    private val pantryItemRepository: PantryItemRepository,
    private val userRepository: UserRepository,
) {

    fun getPantryItems(userId: Long): List<PantryItemDto> {
        return pantryItemRepository.findByUserId(userId).map { it.toDto() }
    }

    fun addPantryItem(userId: Long, name: String, amount: Double, unit: String, expirationDate: String? = null): PantryItemDto {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found with id: $userId") }

        val item = PantryItem(
            user = user,
            name = name,
            amount = amount,
            unit = unit,
        )
        return pantryItemRepository.save(item).toDto()
    }

    fun updatePantryItem(id: Long, amount: Double, unit: String): PantryItemDto {
        val item = pantryItemRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Pantry item not found with id: $id") }

        val updated = PantryItem(
            id = item.id,
            user = item.user,
            ingredient = item.ingredient,
            name = item.name,
            amount = amount,
            unit = unit,
            expirationDate = item.expirationDate,
            addedAt = item.addedAt,
        )
        return pantryItemRepository.save(updated).toDto()
    }

    fun deletePantryItem(id: Long) {
        if (!pantryItemRepository.existsById(id)) {
            throw ResourceNotFoundException("Pantry item not found with id: $id")
        }
        pantryItemRepository.deleteById(id)
    }

    private fun PantryItem.toDto() = PantryItemDto(
        id = id,
        userId = user.id,
        name = name,
        amount = amount,
        unit = unit,
        expirationDate = expirationDate,
        addedAt = addedAt,
        ingredientId = ingredient?.id,
    )
}