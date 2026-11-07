package br.com.thiago.tempero.core.data.mapper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MealStepsParserTest {
    private fun String?.steps(): List<String> = toMealSteps().map { it.step }

    @Test
    fun `null e vazio viram lista vazia`() {
        assertTrue(null.steps().isEmpty())
        assertTrue("".steps().isEmpty())
        assertTrue("  \n \n".steps().isEmpty())
    }

    @Test
    fun `descarta rotulos soltos de passo`() {
        assertEquals(listOf("Mix.", "Bake."), "STEP 1\nMix.\nStep 2:\nBake.".steps())
        assertEquals(listOf("Preheat oven."), "3.\nPreheat oven.".steps())
        assertEquals(listOf("Preheat oven."), "4)\nPreheat oven.".steps())
    }

    @Test
    fun `numero de tres digitos nao e rotulo`() {
        assertEquals(listOf("350"), "350".steps())
    }

    @Test
    fun `cola titulo terminado em dois pontos a linha seguinte`() {
        assertEquals(
            listOf("Cook the filling: Fry the onions."),
            "Cook the filling:\nFry the onions.".steps()
        )
    }

    @Test
    fun `titulos seguidos se acumulam`() {
        assertEquals(listOf("A: B: Text."), "A:\nB:\nText.".steps())
    }

    @Test
    fun `titulo no fim sem conteudo e descartado`() {
        assertEquals(listOf("Fry the onions."), "Fry the onions.\nServe:".steps())
    }

    @Test
    fun `normaliza espacos e linhas vazias`() {
        assertEquals(listOf("Mix well.", "Bake."), "  Mix well.  \n\n  Bake.  ".steps())
    }

    @Test
    fun `nunca junta linhas diferentes`() {
        assertEquals(listOf("One.", "Two."), "One.\nTwo.".steps())
    }

    @Test
    fun `junta frases curtas no mesmo passo`() {
        assertEquals(listOf("Mix well. Bake it."), "Mix well. Bake it.".steps())
    }

    @Test
    fun `separa frases quando passam do limite de 250`() {
        val first = "A" + "a".repeat(198) + "."
        val second = "B" + "b".repeat(198) + "."
        assertEquals(listOf(first, second), "$first $second".steps())
    }

    @Test
    fun `frase maior que o limite fica sozinha e inteira`() {
        val long = "A" + "a".repeat(299) + "."
        assertEquals(listOf(long), long.steps())
    }

    @Test
    fun `nao corta depois de abreviacao e g`() {
        // Sem o tratamento de abreviação, o texto viraria dois passos (frases de ~150 e ~150 caracteres).
        val text = "A".repeat(150) + " e.g. Cut " + "b".repeat(150) + "."
        assertEquals(1, text.steps().size)
    }

    @Test
    fun `nao corta em reticencias`() {
        val text = "A".repeat(200) + "... Then " + "b".repeat(100) + "."
        assertEquals(1, text.steps().size)
    }

    @Test
    fun `nao corta quando depois do ponto vem numero ou minuscula`() {
        val text = "x".repeat(200) + " approx. 1cm " + "y".repeat(100) + "."
        assertEquals(1, text.steps().size)
    }

    @Test
    fun `reaplicar sobre o resultado da o mesmo resultado`() {
        val original = "STEP 1\nHeat oil: then fry. " + "Stir. ".repeat(60) + "\nServe hot."
        val once = original.steps()
        assertEquals(once, once.joinToString("\n").steps())
    }
}
