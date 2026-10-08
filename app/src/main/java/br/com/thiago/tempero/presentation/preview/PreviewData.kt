package br.com.thiago.tempero.presentation.preview

import br.com.thiago.tempero.domain.model.Meal
import br.com.thiago.tempero.domain.model.MealCategory

/** Dados de exemplo usados apenas pelos `@Preview`. */
object PreviewData {
    val categories = listOf(
        MealCategory("Chicken"),
        MealCategory("Pasta"),
        MealCategory("Dessert"),
        MealCategory("Beef"),
        MealCategory("Vegetarian")
    )

    val meals: List<Meal> = listOf(
        Triple("Spicy Arrabiata Penne", "Vegetarian", "Italian"),
        Triple("Apple Frangipan Tart", "Dessert", "British"),
        Triple("Ayam Percik", "Chicken", "Malaysian"),
        Triple("Apam Balik", "Dessert", "Malaysian")
    ).mapIndexed { index, (name, category, area) ->
        Meal(
            id = index.toLong(),
            name = name,
            altName = null,
            category = MealCategory(category),
            area = area,
            country = area,
            steps = emptyList(),
            imageUrl = "",
            videoUrl = null,
            tags = emptyList(),
            ingredients = emptyList(),
            source = null,
            imageSrc = null,
            creativeCommonsConfirmed = null,
            dateModified = null
        )
    }
}
