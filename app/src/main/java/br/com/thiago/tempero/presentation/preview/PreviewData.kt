package br.com.thiago.tempero.presentation.preview

import br.com.thiago.tempero.domain.model.Meal
import br.com.thiago.tempero.domain.model.MealCategory
import br.com.thiago.tempero.domain.model.MealIngredient
import br.com.thiago.tempero.domain.model.MealStep

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

    /** Receita completa (com ingredientes e preparo) para os previews do Detalhe. */
    val detailMeal: Meal = previewMeal(0, "Spicy Arrabiata Penne", "Vegetarian", "Italian").copy(
        steps = listOf(
            "Bring a large pot of water to a boil. Add kosher salt to the boiling water, then add the pasta. Cook according to the package instructions, about 9 minutes.",
            "In a large skillet over medium-high heat, add the olive oil and heat until the oil starts to shimmer. Add the garlic and cook, stirring, until fragrant, 1 to 2 minutes.",
            "Add the chopped tomatoes, red chilli flakes and Italian seasoning. Bring to a boil and cook for 5 minutes.",
            "Drain the pasta and add it to the sauce. Garnish with Parmigiano-Reggiano and basil."
        ).map { MealStep(it) },
        ingredients = listOf(
            MealIngredient("Penne Rigate", "1 pound"),
            MealIngredient("Olive Oil", "1/4 cup"),
            MealIngredient("Garlic", "3 cloves"),
            MealIngredient("Chopped Tomatoes", "1 tin"),
            MealIngredient("Red Chilli Flakes", "1/2 tsp"),
            MealIngredient("Italian Seasoning", "1/2 tsp"),
            MealIngredient("Basil", "6 leaves"),
            MealIngredient("Parmigiano-Reggiano", "sprinkling")
        )
    )

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
