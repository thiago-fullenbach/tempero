package br.com.thiago.tempero.core.data.remote.mapper

import br.com.thiago.tempero.core.model.MealCategory
import br.com.thiago.tempero.core.model.MealIngredient
import br.com.thiago.tempero.core.testing.util.mealDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MealMapperTest {
    private val fallback = MealCategory("Fallback")

    @Test
    fun `id invalido descarta a receita`() {
        assertNull(mealDto(id = "abc", category = "Beef").toDomain())
    }

    @Test
    fun `strCategory tem prioridade sobre o fallback`() {
        assertEquals(MealCategory("Beef"), mealDto(category = "Beef").toDomain(fallback)?.category)
    }

    @Test
    fun `usa o fallback quando a API nao manda categoria`() {
        assertEquals(fallback, mealDto(category = null).toDomain(fallback)?.category)
    }

    @Test
    fun `sem categoria e sem fallback descarta a receita`() {
        assertNull(mealDto(category = null).toDomain())
    }

    @Test
    fun `ingredientes vazios ou em branco sao ignorados e a medida e limpa`() {
        val meal = mealDto(
            category = "Beef",
            ingredients = listOf("Flour", " ", null, "Egg"),
            measures = listOf("200g", "1 ", "x", "")
        ).toDomain()!!

        assertEquals(
            listOf(MealIngredient("Flour", "200g"), MealIngredient("Egg", null)),
            meal.ingredients
        )
    }

    @Test
    fun `tags sao separadas por virgula com trim`() {
        assertEquals(listOf("a", "b", "c"), mealDto(category = "Beef", tags = "a, b,,c ").toDomain()!!.tags)
        assertEquals(emptyList<String>(), mealDto(category = "Beef", tags = null).toDomain()!!.tags)
    }

    @Test
    fun `strings em branco viram null e campos ausentes viram vazio`() {
        val meal = mealDto(category = "Beef", source = "  ", alternate = " ").toDomain()!!
        assertNull(meal.source)
        assertNull(meal.altName)
        assertEquals("", meal.area)
        assertEquals("", meal.country)
    }

    @Test
    fun `instrucoes viram passos`() {
        val meal = mealDto(category = "Beef", instructions = "STEP 1\nMix.").toDomain()!!
        assertEquals(listOf("Mix."), meal.steps.map { it.step })
    }

    @Test
    fun `lista descarta os itens invalidos`() {
        val result = listOf(
            mealDto(id = "1", category = "Beef"),
            mealDto(id = "x", category = "Beef"),
            mealDto(id = "3", category = null)
        ).toDomain()

        assertEquals(listOf(1L), result.map { it.id })
    }
}
