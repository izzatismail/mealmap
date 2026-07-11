package com.izzatismail.mealmap.service

import com.izzatismail.mealmap.dto.PantryItemDto
import com.izzatismail.mealmap.dto.PantryItemRequest
import com.izzatismail.mealmap.dto.UpdatePantryItemRequest
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

    fun addPantryItem(userId: Long, request: PantryItemRequest): PantryItemDto {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found with id: $userId") }

        val item = PantryItem(
            user = user,
            name = request.name,
            amount = request.amount,
            unit = request.unit,
            expirationDate = request.expirationDate,
        )
        return pantryItemRepository.save(item).toDto()
    }

    fun updatePantryItem(userId: Long, id: Long, request: UpdatePantryItemRequest): PantryItemDto {
        val item = pantryItemRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Pantry item not found with id: $id") }
        if (item.user.id != userId) {
            throw ResourceNotFoundException("Pantry item not found with id: $id")
        }
        item.amount = request.amount
        item.unit = request.unit
        return pantryItemRepository.save(item).toDto()
    }

    fun deletePantryItem(userId: Long, id: Long) {
        val item = pantryItemRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Pantry item not found with id: $id") }
        if (item.user.id != userId) {
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