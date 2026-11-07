package br.com.thiago.tempero.core.testing.util

import br.com.thiago.tempero.core.model.Meal
import br.com.thiago.tempero.core.model.MealCategory
import br.com.thiago.tempero.core.model.MealIngredient
import br.com.thiago.tempero.core.model.MealStep

fun meal(
    id: Long = 1L,
    name: String = "Meal $id",
    category: String = "Beef",
    country: String = "British",
    steps: List<String> = emptyList(),
    ingredients: List<MealIngredient> = emptyList(),
    tags: List<String> = emptyList()
): Meal = Meal(
    id = id,
    name = name,
    altName = null,
    category = MealCategory(category),
    area = country,
    country = country,
    steps = steps.map { MealStep(it) },
    imageUrl = "https://img.test/$id.jpg",
    videoUrl = null,
    tags = tags,
    ingredients = ingredients,
    source = null,
    imageSrc = null,
    creativeCommonsConfirmed = null,
    dateModified = null
)

/** Receita completa (com ingredientes e preparo), como vem de `lookup.php` ou do Room. */
fun completeMeal(id: Long = 1L, name: String = "Meal $id", category: String = "Beef"): Meal = meal(
    id = id,
    name = name,
    category = category,
    steps = listOf("Mix everything.", "Bake it."),
    ingredients = listOf(MealIngredient("Flour", "200g"), MealIngredient("Egg", null))
)
