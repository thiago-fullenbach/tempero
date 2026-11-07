package br.com.thiago.tempero.data.local.converter

import androidx.room.TypeConverter
import br.com.thiago.tempero.core.model.MealIngredient
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import timber.log.Timber

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
    fun toStrings(value: String): List<String> {
        val parsed = stringsAdapter.fromJson(value)
        if (parsed == null) Timber.w("toStrings: JSON nulo no banco, usando lista vazia")
        return parsed.orEmpty()
    }

    @TypeConverter
    fun fromIngredients(value: List<MealIngredient>): String =
        ingredientsAdapter.toJson(value.map { IngredientJson(it.ingredient, it.measure) })

    @TypeConverter
    fun toIngredients(value: String): List<MealIngredient> {
        val parsed = ingredientsAdapter.fromJson(value)
        if (parsed == null) Timber.w("toIngredients: JSON nulo no banco, usando lista vazia")
        return parsed.orEmpty().map { MealIngredient(it.ingredient, it.measure) }
    }
}

@JsonClass(generateAdapter = true)
internal data class IngredientJson(
    val ingredient: String,
    val measure: String?
)
