package br.com.thiago.tempero.data.local.converter

import androidx.room.TypeConverter
import br.com.thiago.tempero.domain.model.MealIngredient
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

/** Conversores de listas para JSON (Moshi), usados pelas colunas de lista do Room. */
class MealConverters {
    private val moshi = Moshi.Builder().build()

    private val stringsAdapter =
        moshi.adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java))

    private val ingredientsAdapter =
        moshi.adapter<List<IngredientJson>>(Types.newParameterizedType(List::class.java, IngredientJson::class.java))

    @TypeConverter
    fun fromStrings(value: List<String>): String = stringsAdapter.toJson(value)

    @TypeConverter
    fun toStrings(value: String): List<String> = stringsAdapter.fromJson(value).orEmpty()

    @TypeConverter
    fun fromIngredients(value: List<MealIngredient>): String =
        ingredientsAdapter.toJson(value.map { IngredientJson(it.ingredient, it.measure) })

    @TypeConverter
    fun toIngredients(value: String): List<MealIngredient> =
        ingredientsAdapter.fromJson(value).orEmpty().map { MealIngredient(it.ingredient, it.measure) }
}

@JsonClass(generateAdapter = true)
internal data class IngredientJson(
    val ingredient: String,
    val measure: String?
)
