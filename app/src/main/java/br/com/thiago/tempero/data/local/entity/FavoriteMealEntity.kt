package br.com.thiago.tempero.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.thiago.tempero.domain.model.MealIngredient

/** Receita favorita. Guarda todos os campos de `Meal` para o detalhe funcionar offline. */
@Entity(tableName = "favorite_meals")
data class FavoriteMealEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val altName: String?,
    val category: String,
    val area: String,
    val country: String,
    val steps: List<String>,
    val imageUrl: String,
    val videoUrl: String?,
    val tags: List<String>,
    val ingredients: List<MealIngredient>,
    val source: String?,
    val imageSrc: String?,
    val creativeCommonsConfirmed: String?,
    val dateModified: String?,
    val savedAt: Long
)
