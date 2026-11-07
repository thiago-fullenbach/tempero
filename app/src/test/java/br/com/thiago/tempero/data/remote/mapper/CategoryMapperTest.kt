package br.com.thiago.tempero.data.remote.mapper

import br.com.thiago.tempero.data.remote.dto.CategoryNameDto
import br.com.thiago.tempero.core.model.MealCategory
import br.com.thiago.tempero.core.testing.util.categoryDto
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryMapperTest {
    @Test
    fun `categoria completa faz trim`() {
        assertEquals(MealCategory("Beef"), categoryDto("  Beef ").toDomain())
    }

    @Test
    fun `lista de categorias completas`() {
        assertEquals(
            listOf(MealCategory("Beef"), MealCategory("Chicken")),
            listOf(categoryDto("Beef"), categoryDto(" Chicken")).toDomain()
        )
    }

    @Test
    fun `categoria so com nome faz trim`() {
        assertEquals(MealCategory("Pasta"), CategoryNameDto(" Pasta ").toDomain())
        assertEquals(
            listOf(MealCategory("Pasta")),
            listOf(CategoryNameDto("Pasta ")).toDomain()
        )
    }
}
