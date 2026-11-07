package br.com.thiago.tempero.core.data.remote.mapper

import br.com.thiago.tempero.core.data.mapper.toMealSteps
import br.com.thiago.tempero.core.network.dto.MealDto
import br.com.thiago.tempero.core.model.Meal
import br.com.thiago.tempero.core.model.MealCategory
import br.com.thiago.tempero.core.model.MealIngredient
import timber.log.Timber

/**
 * Converte o DTO em [Meal]. A categoria vem de `strCategory`; se ausente (ex.: `filter.php`),
 * usa [fallbackCategory]. Sem nenhuma das duas, a receita é descartada.
 */
fun MealDto.toDomain(fallbackCategory: MealCategory? = null): Meal? {
    val id = idMeal.toLongOrNull()
    if (id == null) {
        Timber.w("Receita descartada: idMeal inválido (%s)", idMeal)
        return null
    }
    val category = strCategory?.let { MealCategory(it) } ?: fallbackCategory
    if (category == null) {
        Timber.w("Receita %d descartada: sem categoria", id)
        return null
    }
    return Meal(
        id = id,
        name = strMeal,
        altName = strMealAlternate.cleanOrNull(),
        category = category,
        area = strArea.orEmpty(),
        country = strCountry.orEmpty(),
        steps = strInstructions.toMealSteps(),
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

fun List<MealDto>.toDomain(fallbackCategory: MealCategory? = null): List<Meal> =
    mapNotNull { it.toDomain(fallbackCategory) }

private fun MealDto.toIngredients(): List<MealIngredient> =
    rawIngredients.zip(rawMeasures).mapNotNull { (ingredient, measure) ->
        val name = ingredient.cleanOrNull() ?: return@mapNotNull null
        MealIngredient(ingredient = name, measure = measure.cleanOrNull())
    }

private fun String?.cleanOrNull(): String? = this?.trim()?.takeIf { it.isNotEmpty() }
