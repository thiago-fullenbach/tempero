package br.com.thiago.tempero.domain.analytics

import br.com.thiago.tempero.domain.error.DataError
import br.com.thiago.tempero.domain.model.Meal

/**
 * Eventos de analytics do app, em Kotlin puro (sem depender do Firebase).
 * Sempre que existe um evento recomendado do GA4 o nome dele é usado, para aparecer nos
 * relatórios prontos. Nomes têm no máximo 40 caracteres e valores de parâmetros, 100.
 */
sealed interface AnalyticsEvent {
    val name: String
    val params: Map<String, Any>

    /** Tela exibida; o `screen_view` automático não enxerga a navegação do Compose. */
    data class ScreenView(val screen: String) : AnalyticsEvent {
        override val name = "screen_view"
        override val params = mapOf<String, Any>(
            "screen_name" to screen,
            "screen_class" to screen
        )
    }

    /** Categoria escolhida no Explorar; `null` significa "Todas". */
    data class CategorySelected(val category: String?) : AnalyticsEvent {
        override val name = "select_content"
        override val params = mapOf<String, Any>(
            "content_type" to "category",
            "item_id" to (category ?: "all").take(MAX_VALUE)
        )
    }

    /** Busca concluída (depois do debounce); [method] é [METHOD_TYPED] ou [METHOD_SUGGESTION]. */
    data class Search(
        val term: String,
        val method: String,
        val resultsCount: Int
    ) : AnalyticsEvent {
        override val name = "search"
        override val params = mapOf<String, Any>(
            "search_term" to term.take(MAX_VALUE),
            "method" to method,
            "results_count" to resultsCount
        )
    }

    /** Receita aberta a partir de uma lista; [origin] é `explore`, `search` ou `favorites`. */
    data class SelectItem(val meal: Meal, val origin: String) : AnalyticsEvent {
        override val name = "select_item"
        override val params = mapOf<String, Any>(
            "item_list_name" to origin,
            "item_id" to meal.id.toString(),
            "item_name" to meal.name.take(MAX_VALUE)
        )
    }

    /** Detalhe da receita carregado. */
    data class ViewItem(val meal: Meal) : AnalyticsEvent {
        override val name = "view_item"
        override val params = mapOf<String, Any>(
            "item_id" to meal.id.toString(),
            "item_name" to meal.name.take(MAX_VALUE),
            "item_category" to meal.category.category.take(MAX_VALUE),
            "area" to meal.area.take(MAX_VALUE)
        )
    }

    /** Receita favoritada. */
    data class FavoriteAdded(val meal: Meal, val origin: String) : AnalyticsEvent {
        override val name = "add_to_wishlist"
        override val params = mapOf<String, Any>(
            "item_id" to meal.id.toString(),
            "item_name" to meal.name.take(MAX_VALUE),
            "item_category" to meal.category.category.take(MAX_VALUE),
            "origin" to origin
        )
    }

    /** Favorito removido; [origin] é `detail` ou `favorites`. */
    data class FavoriteRemoved(val mealId: Long, val origin: String) : AnalyticsEvent {
        override val name = "favorite_removed"
        override val params = mapOf<String, Any>(
            "item_id" to mealId.toString(),
            "origin" to origin
        )
    }

    /** "Desfazer" usado após remover um favorito. */
    data class FavoriteRemovalUndone(val mealId: Long) : AnalyticsEvent {
        override val name = "favorite_removal_undone"
        override val params = mapOf<String, Any>("item_id" to mealId.toString())
    }

    /** Falha ao carregar uma tela; mede a taxa de erro por tela. */
    data class LoadFailed(val screen: String, val error: DataError) : AnalyticsEvent {
        override val name = "load_failed"
        override val params = mapOf<String, Any>(
            "screen" to screen,
            "error_type" to (error::class.simpleName ?: "Unknown")
        )
    }

    companion object {
        const val METHOD_TYPED = "typed"
        const val METHOD_SUGGESTION = "suggestion"

        const val ORIGIN_EXPLORE = "explore"
        const val ORIGIN_SEARCH = "search"
        const val ORIGIN_FAVORITES = "favorites"
        const val ORIGIN_DETAIL = "detail"

        const val SCREEN_EXPLORE = "explore"
        const val SCREEN_SEARCH = "search"
        const val SCREEN_FAVORITES = "favorites"
        const val SCREEN_DETAIL = "detail"

        private const val MAX_VALUE = 100
    }
}
