package br.com.thiago.tempero.data.local.mapper

import br.com.thiago.tempero.data.local.entity.FavoriteMealEntity
import br.com.thiago.tempero.data.mapper.toMealSteps
import br.com.thiago.tempero.core.model.Meal
import br.com.thiago.tempero.core.model.MealCategory

fun Meal.toEntity(savedAt: Long): FavoriteMealEntity = FavoriteMealEntity(
    id = id,
    name = name,
    altName = altName,
    category = category.category,
    area = area,
    country = country,
    steps = steps.map { it.step },
    imageUrl = imageUrl,
    videoUrl = videoUrl,
    tags = tags,
    ingredients = ingredients,
    source = source,
    imageSrc = imageSrc,
    creativeCommonsConfirmed = creativeCommonsConfirmed,
    dateModified = dateModified,
    savedAt = savedAt
)

fun FavoriteMealEntity.toDomain(): Meal = Meal(
    id = id,
    name = name,
    altName = altName,
    category = MealCategory(category),
    area = area,
    country = country,
    // Reaplica a quebra de passos: favoritos salvos antes da regra atual saem corrigidos.
    steps = steps.joinToString("\n").toMealSteps(),
    imageUrl = imageUrl,
    videoUrl = videoUrl,
    tags = tags,
    ingredients = ingredients,
    source = source,
    imageSrc = imageSrc,
    creativeCommonsConfirmed = creativeCommonsConfirmed,
    dateModified = dateModified
)

fun List<FavoriteMealEntity>.toDomain(): List<Meal> = map { it.toDomain() }
