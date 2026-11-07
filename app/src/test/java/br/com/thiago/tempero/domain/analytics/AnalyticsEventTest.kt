package br.com.thiago.tempero.domain.analytics

import br.com.thiago.tempero.domain.error.DataError
import br.com.thiago.tempero.testutil.meal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class AnalyticsEventTest {
    @Test
    fun `screen_view leva o nome da tela`() {
        val event = AnalyticsEvent.ScreenView("explore")

        assertEquals("screen_view", event.name)
        assertEquals(mapOf("screen_name" to "explore", "screen_class" to "explore"), event.params)
    }

    @Test
    fun `categoria nula significa todas`() {
        assertEquals("all", AnalyticsEvent.CategorySelected(null).params["item_id"])
        assertEquals("Beef", AnalyticsEvent.CategorySelected("Beef").params["item_id"])
        assertEquals("category", AnalyticsEvent.CategorySelected("Beef").params["content_type"])
    }

    @Test
    fun `valores de parametros sao truncados em 100 caracteres`() {
        val long = "x".repeat(150)

        assertEquals(100, (AnalyticsEvent.CategorySelected(long).params["item_id"] as String).length)
        assertEquals(100, (AnalyticsEvent.Search(long, AnalyticsEvent.METHOD_TYPED, 0).params["search_term"] as String).length)
        assertEquals(100, (AnalyticsEvent.SelectItem(meal(name = long), "explore").params["item_name"] as String).length)
        assertEquals(100, (AnalyticsEvent.ViewItem(meal(name = long)).params["item_name"] as String).length)
    }

    @Test
    fun `search leva termo metodo e quantidade`() {
        val event = AnalyticsEvent.Search("pasta", AnalyticsEvent.METHOD_SUGGESTION, 3)

        assertEquals("search", event.name)
        assertEquals(mapOf("search_term" to "pasta", "method" to "suggestion", "results_count" to 3), event.params)
    }

    @Test
    fun `select_item e view_item usam o id como texto`() {
        val meal = meal(id = 42, name = "Pie", category = "Beef", country = "British")

        assertEquals(
            mapOf("item_list_name" to "favorites", "item_id" to "42", "item_name" to "Pie"),
            AnalyticsEvent.SelectItem(meal, AnalyticsEvent.ORIGIN_FAVORITES).params
        )
        assertEquals(
            mapOf("item_id" to "42", "item_name" to "Pie", "item_category" to "Beef", "area" to "British"),
            AnalyticsEvent.ViewItem(meal).params
        )
    }

    @Test
    fun `favoritar usa o evento recomendado add_to_wishlist`() {
        val event = AnalyticsEvent.FavoriteAdded(meal(id = 1, name = "Pie"), AnalyticsEvent.ORIGIN_DETAIL)

        assertEquals("add_to_wishlist", event.name)
        assertEquals("detail", event.params["origin"])
        assertEquals("1", event.params["item_id"])
    }

    @Test
    fun `remover e desfazer`() {
        assertEquals(
            mapOf("item_id" to "9", "origin" to "favorites"),
            AnalyticsEvent.FavoriteRemoved(9, AnalyticsEvent.ORIGIN_FAVORITES).params
        )
        assertEquals(mapOf("item_id" to "9"), AnalyticsEvent.FavoriteRemovalUndone(9).params)
    }

    @Test
    fun `load_failed leva o tipo do erro`() {
        val event = AnalyticsEvent.LoadFailed(AnalyticsEvent.SCREEN_DETAIL, DataError.Network(IOException()))

        assertEquals(mapOf("screen" to "detail", "error_type" to "Network"), event.params)
    }

    @Test
    fun `nomes dos eventos tem no maximo 40 caracteres`() {
        val m = meal()
        val events = listOf(
            AnalyticsEvent.ScreenView("x"),
            AnalyticsEvent.CategorySelected(null),
            AnalyticsEvent.Search("x", "typed", 0),
            AnalyticsEvent.SelectItem(m, "explore"),
            AnalyticsEvent.ViewItem(m),
            AnalyticsEvent.FavoriteAdded(m, "detail"),
            AnalyticsEvent.FavoriteRemoved(1, "detail"),
            AnalyticsEvent.FavoriteRemovalUndone(1),
            AnalyticsEvent.LoadFailed("x", DataError.Unknown(Exception()))
        )
        assertTrue(events.all { it.name.length <= 40 })
    }
}
