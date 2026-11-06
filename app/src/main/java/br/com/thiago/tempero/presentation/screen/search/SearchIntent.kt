package br.com.thiago.tempero.presentation.screen.search

sealed interface SearchIntent {
    data class QueryChanged(val query: String) : SearchIntent
    data object Clear : SearchIntent

    /** Sugestão tocada: busca imediata, sem esperar o debounce. */
    data class SuggestionSelected(val query: String) : SearchIntent
    data object Retry : SearchIntent
}
