package br.com.thiago.tempero.feature.detail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import br.com.thiago.tempero.feature.detail.DetailEntry
import br.com.thiago.tempero.feature.detail.DetailEvent
import kotlinx.serialization.Serializable

/** Rota type-safe da tela Detalhe; [mealId] também é lido pelo `DetailViewModel` via `SavedStateHandle.toRoute`. */
@Serializable
data class DetailRoute(val mealId: Long)

fun NavController.navigateToDetail(mealId: Long, builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(DetailRoute(mealId), builder)
}

fun NavGraphBuilder.detailScreen(onEvent: (DetailEvent) -> Unit) {
    composable<DetailRoute> {
        DetailEntry(onEvent = onEvent)
    }
}
