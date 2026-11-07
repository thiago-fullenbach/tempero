package br.com.thiago.tempero.core.data.remote.mapper

import br.com.thiago.tempero.core.network.dto.CategoryDto
import br.com.thiago.tempero.core.network.dto.CategoryNameDto
import br.com.thiago.tempero.core.model.MealCategory

fun CategoryNameDto.toDomain(): MealCategory = MealCategory(strCategory.trim())

fun CategoryDto.toDomain(): MealCategory = MealCategory(strCategory.trim())

@JvmName("categoryNamesToDomain")
fun List<CategoryNameDto>.toDomain(): List<MealCategory> = mapNotNull { it.toDomain() }

@JvmName("categoriesToDomain")
fun List<CategoryDto>.toDomain(): List<MealCategory> = mapNotNull { it.toDomain() }
