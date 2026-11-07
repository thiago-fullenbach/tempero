package br.com.thiago.tempero.feature.detail

sealed interface DetailIntent {
    data object ToggleFavorite : DetailIntent
    data object Retry : DetailIntent
}
