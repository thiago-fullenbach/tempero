package br.com.thiago.tempero.data.remote.mapper

import br.com.thiago.tempero.data.remote.dto.MealDto
import br.com.thiago.tempero.domain.model.Meal
import br.com.thiago.tempero.domain.model.MealCategory
import br.com.thiago.tempero.domain.model.MealIngredient
import br.com.thiago.tempero.domain.model.MealStep

fun MealDto.toDomain(): Meal? {
    val id = idMeal.toLongOrNull() ?: return null
    return Meal(
        id = id,
        name = strMeal,
        altName = strMealAlternate.cleanOrNull(),
        category = MealCategory(strCategory.orEmpty()),
        area = strArea.orEmpty(),
        country = strCountry.orEmpty(),
        steps = strInstructions.toSteps(),
        imageUrl = strMealThumb.orEmpty(),
        videoUrl = strYoutube.cleanOrNull(),
        tags = strTags.orEmpty()
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() },
        ingredients = toIngredients(),
        source = strSource.cleanOrNull(),
        imageSrc = strImageSource.cleanOrNull(),
        creativeCommonsConfirmed = strCreativeCommonsConfirmed.cleanOrNull(),
        dateModified = dateModified.cleanOrNull()
    )
}

fun List<MealDto>.toDomain(): List<Meal> = mapNotNull { it.toDomain() }

private fun MealDto.toIngredients(): List<MealIngredient> =
    rawIngredients.zip(rawMeasures).mapNotNull { (ingredient, measure) ->
        val name = ingredient.cleanOrNull() ?: return@mapNotNull null
        MealIngredient(ingredient = name, measure = measure.cleanOrNull())
    }

private fun String?.toSteps(): List<MealStep> =
    orEmpty()
        .lines()
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { MealStep(it) }

private fun String?.cleanOrNull(): String? = this?.trim()?.takeIf { it.isNotEmpty() }
