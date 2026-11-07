package br.com.thiago.tempero.testutil

import br.com.thiago.tempero.data.remote.dto.CategoryDto
import br.com.thiago.tempero.data.remote.dto.MealDto
import br.com.thiago.tempero.domain.model.Meal
import br.com.thiago.tempero.domain.model.MealCategory
import br.com.thiago.tempero.domain.model.MealIngredient
import br.com.thiago.tempero.domain.model.MealStep

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

fun categoryDto(name: String) = CategoryDto(
    idCategory = "1",
    strCategory = name,
    strCategoryThumb = null,
    strCategoryDescription = null
)

fun mealDto(
    id: String = "1",
    name: String = "Meal $id",
    category: String? = null,
    instructions: String? = null,
    tags: String? = null,
    ingredients: List<String?> = emptyList(),
    measures: List<String?> = emptyList(),
    alternate: String? = null,
    source: String? = null
): MealDto {
    fun ing(i: Int) = ingredients.getOrNull(i)
    fun mea(i: Int) = measures.getOrNull(i)
    return MealDto(
        idMeal = id,
        strMeal = name,
        strMealAlternate = alternate,
        strCategory = category,
        strArea = null,
        strCountry = null,
        strInstructions = instructions,
        strMealThumb = "https://img.test/$id.jpg",
        strTags = tags,
        strYoutube = null,
        strIngredient1 = ing(0), strIngredient2 = ing(1), strIngredient3 = ing(2), strIngredient4 = ing(3),
        strIngredient5 = ing(4), strIngredient6 = ing(5), strIngredient7 = ing(6), strIngredient8 = ing(7),
        strIngredient9 = ing(8), strIngredient10 = ing(9), strIngredient11 = ing(10), strIngredient12 = ing(11),
        strIngredient13 = ing(12), strIngredient14 = ing(13), strIngredient15 = ing(14), strIngredient16 = ing(15),
        strIngredient17 = ing(16), strIngredient18 = ing(17), strIngredient19 = ing(18), strIngredient20 = ing(19),
        strMeasure1 = mea(0), strMeasure2 = mea(1), strMeasure3 = mea(2), strMeasure4 = mea(3),
        strMeasure5 = mea(4), strMeasure6 = mea(5), strMeasure7 = mea(6), strMeasure8 = mea(7),
        strMeasure9 = mea(8), strMeasure10 = mea(9), strMeasure11 = mea(10), strMeasure12 = mea(11),
        strMeasure13 = mea(12), strMeasure14 = mea(13), strMeasure15 = mea(14), strMeasure16 = mea(15),
        strMeasure17 = mea(16), strMeasure18 = mea(17), strMeasure19 = mea(18), strMeasure20 = mea(19),
        strSource = source,
        strImageSource = null,
        strCreativeCommonsConfirmed = null,
        dateModified = null
    )
}
