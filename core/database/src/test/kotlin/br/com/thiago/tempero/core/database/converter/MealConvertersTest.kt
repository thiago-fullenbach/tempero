package br.com.thiago.tempero.core.database.converter

import br.com.thiago.tempero.core.model.MealIngredient
import org.junit.Assert.assertEquals
import org.junit.Test

class MealConvertersTest {
    private val converters = MealConverters()

    @Test
    fun `lista de strings faz ida e volta`() {
        val list = listOf("a", "b, c", "d \"e\"")
        assertEquals(list, converters.toStrings(converters.fromStrings(list)))
    }

    @Test
    fun `lista vazia faz ida e volta`() {
        assertEquals(emptyList<String>(), converters.toStrings(converters.fromStrings(emptyList())))
        assertEquals(emptyList<MealIngredient>(), converters.toIngredients(converters.fromIngredients(emptyList())))
    }

    @Test
    fun `ingredientes fazem ida e volta inclusive com medida nula`() {
        val list = listOf(MealIngredient("Flour", "200g"), MealIngredient("Egg", null))
        assertEquals(list, converters.toIngredients(converters.fromIngredients(list)))
    }

    @Test
    fun `json nulo no banco vira lista vazia`() {
        assertEquals(emptyList<String>(), converters.toStrings("null"))
        assertEquals(emptyList<MealIngredient>(), converters.toIngredients("null"))
    }
}
