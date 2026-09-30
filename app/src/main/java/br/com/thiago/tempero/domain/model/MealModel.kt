package br.com.thiago.tempero.domain.model

data class Meal(
    val id: Long,
    val name: String,
    val altName: String?,
    val category: String,
    val area: String,
    val country: String,
    val steps: List<MealStep>,
    val imageUrl: String,
    val videoUrl: String?,
    val tags: List<String>,
    val ingredients: List<MealIngredient>,
    val source: String?,
    val imageSrc: String?,
    val creativeCommonsConfirmed: String?,
    val dateModified: String?
)

@JvmInline
value class MealStep(val step: String)

data class MealIngredient(
    val ingredient: String,
    val measure: String?
)