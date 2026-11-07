package br.com.thiago.tempero.data.local.mapper

import br.com.thiago.tempero.data.local.entity.FavoriteMealEntity
import br.com.thiago.tempero.core.model.MealIngredient
import br.com.thiago.tempero.core.model.MealStep
import br.com.thiago.tempero.core.testing.util.completeMeal
import br.com.thiago.tempero.core.testing.util.meal
import org.junit.Assert.assertEquals
import org.junit.Test

class FavoriteMealMapperTest {
    @Test
    fun `ida e volta preserva todos os campos`() {
        val original = completeMeal(id = 7, name = "Pie", category = "Beef").copy(
            altName = "Torta",
            videoUrl = "https://video",
            tags = listOf("Meat", "Pie"),
            source = "https://source",
            imageSrc = "https://img-src",
            creativeCommonsConfirmed = "Yes",
            dateModified = "2024-01-01"
        )

        val entity = original.toEntity(savedAt = 123L)

        assertEquals(123L, entity.savedAt)
        assertEquals("Beef", entity.category)
        assertEquals(listOf("Mix everything.", "Bake it."), entity.steps)
        assertEquals(original, entity.toDomain())
    }

    @Test
    fun `receita sem ingredientes nem preparo continua igual`() {
        val original = meal(id = 2)
        assertEquals(original, original.toEntity(0).toDomain())
    }

    @Test
    fun `passos salvos antes da regra atual sao normalizados ao ler`() {
        val entity = FavoriteMealEntity(
            id = 1, name = "Pie", altName = null, category = "Beef", area = "", country = "",
            steps = listOf("STEP 1", "Mix."),
            imageUrl = "", videoUrl = null, tags = emptyList(),
            ingredients = listOf(MealIngredient("Flour", null)),
            source = null, imageSrc = null, creativeCommonsConfirmed = null, dateModified = null,
            savedAt = 0
        )

        assertEquals(listOf(MealStep("Mix.")), entity.toDomain().steps)
    }

    @Test
    fun `lista de entidades vira lista de receitas`() {
        val entities = listOf(meal(1), meal(2)).map { it.toEntity(0) }
        assertEquals(listOf(1L, 2L), entities.toDomain().map { it.id })
    }
}
