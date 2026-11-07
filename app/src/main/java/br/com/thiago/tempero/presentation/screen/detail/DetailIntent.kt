package br.com.thiago.tempero.presentation.screen.detail

sealed interface DetailIntent {
    data object ToggleFavorite : DetailIntent
    data object Retry : DetailIntent
}
