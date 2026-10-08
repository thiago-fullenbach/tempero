package br.com.thiago.tempero.presentation.screen.explore

import androidx.compose.runtime.Immutable
import br.com.thiago.tempero.domain.model.MealCategory

/**
 * Estado da tela Explorar. A lista de receitas fica fora porque vem como
 * `PagingData`, que tem seu próprio ciclo de carregamento.
 */
@Immutable
data class ExploreUiState(
    val categories: List<MealCategory> = emptyList(),
    val selectedCategory: MealCategory? = null // null = "Todas"
)
