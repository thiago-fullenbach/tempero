package br.com.thiago.tempero.data.remote.mapper

import br.com.thiago.tempero.data.remote.dto.CategoryDto
import br.com.thiago.tempero.data.remote.dto.CategoryNameDto
import br.com.thiago.tempero.domain.model.MealCategory

fun CategoryNameDto.toDomain(): MealCategory = MealCategory(strCategory.trim())

fun CategoryDto.toDomain(): MealCategory = MealCategory(strCategory.trim())

@JvmName("categoryNamesToDomain")
fun List<CategoryNameDto>.toDomain(): List<MealCategory> = map { it.toDomain() }

@JvmName("categoriesToDomain")
fun List<CategoryDto>.toDomain(): List<MealCategory> = map { it.toDomain() }
