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
    ).mapIndexed { index, (name, category, area) -> previewMeal(index.toLong(), name, category, area) }

    val searchResults: List<Meal> = listOf(
        Triple("Chicken Handi", "Chicken", "Indian"),
        Triple("Chicken Alfredo Primavera", "Chicken", "Italian"),
        Triple("Chicken Congee", "Chicken", "Chinese"),
        Triple("Ayam Percik", "Chicken", "Malaysian")
    ).mapIndexed { index, (name, category, area) -> previewMeal(index.toLong(), name, category, area) }

    val favorites: List<Meal> = listOf(
        Triple("Spicy Arrabiata Penne", "Vegetarian", "Italian"),
        Triple("Chicken Handi", "Chicken", "Indian"),
        Triple("Lamb Biryani", "Lamb", "Indian")
    ).mapIndexed { index, (name, category, area) -> previewMeal(index.toLong(), name, category, area) }

    private fun previewMeal(id: Long, name: String, category: String, area: String) = Meal(
        id = id,
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
