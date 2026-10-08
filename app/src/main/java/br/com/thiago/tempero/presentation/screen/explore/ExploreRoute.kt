package br.com.thiago.tempero.presentation.screen.explore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import br.com.thiago.tempero.domain.model.MealCategory
import br.com.thiago.tempero.presentation.viewmodel.ExploreViewModel

/**
 * Ponto de entrada da tela Explorar: obtém o ViewModel e guarda o estado da UI,
 * deixando [ExploreScreen] sem estado.
 */
@Composable
fun ExploreRoute(
    modifier: Modifier = Modifier,
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val meals = viewModel.meals.collectAsLazyPagingItems()
    // Guardamos só o nome (String) para sobreviver à rotação sem um Saver customizado.
    var selectedCategoryName by rememberSaveable { mutableStateOf<String?>(null) }

    ExploreScreen(
        meals = meals,
        uiState = ExploreUiState(
            categories = emptyList(), // TODO: vir do ViewModel
            selectedCategory = selectedCategoryName?.let(::MealCategory)
        ),
        onCategorySelected = { selectedCategoryName = it?.category },
        onSearchClick = {},
        onMealClick = {},
        modifier = modifier
    )
}
